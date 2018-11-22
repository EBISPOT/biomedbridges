package client.Utils;

import client.SpreadsheetSources.ModelSpreadsheet;
import client.SpreadsheetSources.OntologyWorksheet;
import com.google.gdata.data.spreadsheet.CellEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;

/**
 * User: jmcmurry
 * Date: 16/05/2013
 * Time: 18:09
 */
public class XmlParseUtils {

    private static Logger log = LoggerFactory.getLogger(XmlParseUtils.class);

    // Entered values to ignore from parsing
    private static ArrayList<String> valuesToIgnore = new ArrayList<String>(
            Arrays.asList("no", "n/a", "na", "N/A", "?", "-", "~", "..."));

    // for use in logging. Stores the names of terms entered that have no corresponding URI
    private static Map<String, Set<String>> termRequests = new HashMap<String, Set<String>>();

    private static String termRequestAlreadyPending = "https://docs.google.com/spreadsheet/ccc?key=0ArTv2ysg_H9-dDdjWWZabkpZYUNETzlZdXJLSGVDTGc#gid=16";
    private static String termRequestNowIssued = "https://docs.google.com/spreadsheet/ccc?key=0ArTv2ysg_H9-dDdjWWZabkpZYUNETzlZdXJLSGVDTGc#gid=16";

    private static String xmlDelimiter = "|";

    private static String textForSearchTerms = "";

    private static String tagTextForJoins = "";

    private static Set<String> searchTermsCache = new HashSet<String>();
    private static Set<String> brokenLinks = new HashSet<String>();
    private static String trailingspace = " ";

    private static String doiPrefix = "https://doi.org/";
    private static String pmcIdPrefix = "http://www.ncbi.nlm.nih.gov/pmc/articles/";
    private static String pmIdPrefix = "http://www.ncbi.nlm.nih.gov/pubmed/";


    public static Map<String, Set<String>> getTermRequests() {

        return termRequests;
    }

    public static String openTag(String tagName) {
        return "\n<" + tagName + ">";
    }

    public static String openTagWithIncrement(String nameOfCollection, String tagName, int currentIncrement) {
        nameOfCollection = nameOfCollection.replaceAll(" |-", "_");
        return "\n<" + tagName + " " + tagName.toLowerCase() + "id=\'" + nameOfCollection + "_" + currentIncrement + "\'>";
    }

    public static String closeTag(String tagName) {
        return trailingspace + "</" + tagName + ">";
    }

//    public static String bindNode(){
//
//    }


    public static String bindCell(ModelSpreadsheet.ModelWorksheet.ModelAttribute modelAttribute, CellEntry cell, boolean writeUrisInXml) {

        String elementResult = "";

        String cellText = normaliseText(modelAttribute, cell.getPlainTextContent());
        cellText = cleanAmpersands(modelAttribute.isURL(), cellText);

        // if the contents of the cell are empty or to be ignored
        if (treatAsEmpty(cellText)) elementResult = bindEmpty(modelAttribute);

            // if the column has a *single* value, proceed with binding the whole of the text.
        else {
            if (!modelAttribute.isAllowsMultiVal()) {
                elementResult += bindValue(modelAttribute, cellText, writeUrisInXml);
                // otherwise parse out each of the values and bind each separately.
            } else {
//            String colDelimiter = modelAttribute.getDelimiter();
//            if (colDelimiter.equals("|")) colDelimiter = "\\|";
                //todo: configure delims and check for presence of actual tags containing commas
                String[] multiVals = cellText.split("\\||,|;");

                // per Jon's request, adding a trailing space to all multival nodes
                for (String val : multiVals) {
                    elementResult += bindValue(modelAttribute, val.trim(), writeUrisInXml);
                }
            }
        }

        return elementResult;

    }

    private static String bindValue(ModelSpreadsheet.ModelWorksheet.ModelAttribute modelAttribute, String originalText, boolean writeUrisInXml) {

        // if the contents of the cell are empty or to be ignored
        if (treatAsEmpty(originalText)) return bindEmpty(modelAttribute);

        String normalisedText = "";
        String nodeContent = "";
        String result = "";

        if (modelAttribute.getAttributeName().contains("ublication")) {
            try {
                nodeContent = getPubURL(originalText);
                String tagName = getTagName(modelAttribute, normalisedText);
                result = openTag(tagName) + nodeContent + closeTag(tagName);

            } catch (UnsupportedEncodingException e) {
                log.error("Could not encode " + originalText);
                e.printStackTrace();
            }
        } else {

            // remove line breaks, clean double spaces, check urls, emails etc
            normalisedText = normaliseText(modelAttribute, originalText);

            // clean ampersands
            normalisedText = cleanAmpersands(modelAttribute.isURL(), normalisedText);

            // build up the search term string for later
            buildSearchTerms(modelAttribute, normalisedText);

            // fetch the string that will be used as the open xml tag
            // if the column has enumerated values to be written in the style of <interfacesWebGUI>true</interfacesWebGUI>
            // the tag name will be stored accordingly

            String tagName = getTagName(modelAttribute, normalisedText);
            nodeContent = getNodeContent(modelAttribute, normalisedText, writeUrisInXml, false);


            if (modelAttribute.isJoinWithNext()) result = openTag(tagName) + nodeContent + xmlDelimiter;
            else if (modelAttribute.isJoinWithPrevious()) result = nodeContent + closeTag(tagName);
            else result = openTag(tagName) + nodeContent + closeTag(tagName);

            if (modelAttribute.isWriteElementsAsBool()) {
                result += openTag(modelAttribute.getAttributeName()) + getNodeContent(modelAttribute, normalisedText, writeUrisInXml, true) + closeTag(modelAttribute.getAttributeName());
            }

            result = result.replace("true <", "true<");
        }

        return result;
    }

    private static String getPubURL(String originalText) throws UnsupportedEncodingException {

        if (originalText.indexOf('|') != -1) {
            originalText = originalText.substring(0, originalText.indexOf('|'));
        }
        String pubUrl = "";
        String lowercaseString = originalText.toLowerCase();
        if (lowercaseString.startsWith("doi:")) {
            pubUrl = originalText.replaceAll("doi:", "").replaceAll("DOI:", "");
            pubUrl = doiPrefix + pubUrl.trim();
        } else if (lowercaseString.startsWith("pmid:")) {
            pubUrl = originalText.replaceAll("pmid:", "").replaceAll("PMID:", "");
            pubUrl = pmIdPrefix + pubUrl.trim();
        } else if (lowercaseString.startsWith("pmcid:")) {
            pubUrl = originalText.replaceAll("pmcid:", "").replaceAll("PMCID:", "");
            pubUrl = pmcIdPrefix + pubUrl.trim();
        }

        return pubUrl;
    }

    private static String getNodeContent(ModelSpreadsheet.ModelWorksheet.ModelAttribute modelAttribute, String normalisedText, boolean writeUrisInXml, boolean overrideWriteAsBoolean) {
        String nodeContent = "";
        String ontologyLabel = "";

        // node content will either be an ontologyLabel, "true", or the normalised text, depending on whether the model attribute is
        // semantic, writeasboolean, or neither of these
        // if it is writeasboolean, but overridden, it should be treated as a simple semantic

        if (modelAttribute.isSemantic() || modelAttribute.isWriteElementsAsBool()) {// isWriteElementsAsBool is elements in the style of <interfaceWebUI>true</interfaceWebUI>
            ArrayList<String> labelAndURI = getLabelAndURI(modelAttribute, normalisedText);

            if (modelAttribute.isWriteElementsAsBool() && !overrideWriteAsBoolean) {
                nodeContent = "true";
            }
            // if issemantic, check that normalised text corresponds to an ontology term
            // if it does, fetch the uri
            if ((modelAttribute.isSemantic() && !modelAttribute.isWriteElementsAsBool()) || overrideWriteAsBoolean) {
                ontologyLabel = labelAndURI.get(0);

                nodeContent = ontologyLabel;
                if (writeUrisInXml)
                    nodeContent = normalisedText + "|" + labelAndURI.get(1);
            }
        }
        // if neither semantic nor writeasboolean
        if (!modelAttribute.isSemantic() && !modelAttribute.isWriteElementsAsBool()) nodeContent = normalisedText;

        // pair node content with the appropriate tags

        if (nodeContent == null)
            System.out.println();

        return nodeContent;

    }

    public static String bindEmpty(ModelSpreadsheet.ModelWorksheet.ModelAttribute modelAttribute) {

        if (modelAttribute.isDefaultToNotSpecified()) {
            String tagName = modelAttribute.getAttributeName();
            return openTag(tagName) + "not specified" + closeTag(tagName);
        } else return "";
    }

    private static boolean treatAsEmpty(String text) {

        boolean treatAsEmpty = false;

        if (text.equals("")) treatAsEmpty = true;

        for (String value : valuesToIgnore) {
            if (text.equals(value)) treatAsEmpty = true;
        }

        return treatAsEmpty;
    }

    private static String getElementResult(ModelSpreadsheet.ModelWorksheet.ModelAttribute modelAttribute, String openTag, String normalisedText, String closeTag, boolean writeUrisInXml, boolean overrideWriteAsBoolean) {
        String nodeContent = "";
        String ontologyLabel = "";

        // node content will either be an ontologyLabel, "true", or the normalised text, depending on whether the model attribute is
        // semantic, writeasboolean, or neither of these
        // if it is writeasboolean, but overridden, it should be treated as a simple semantic

        if (modelAttribute.isSemantic() || modelAttribute.isWriteElementsAsBool()) {// isWriteElementsAsBool is elements in the style of <interfaceWebUI>true</interfaceWebUI>
            ArrayList<String> labelAndURI = getLabelAndURI(modelAttribute, normalisedText);

            if (modelAttribute.isWriteElementsAsBool() && !overrideWriteAsBoolean) {
                nodeContent = "true";
            }
            // if issemantic, check that normalised text corresponds to an ontology term
            // if it does, fetch the uri
            if ((modelAttribute.isSemantic() && !modelAttribute.isWriteElementsAsBool()) || overrideWriteAsBoolean) {
                ontologyLabel = labelAndURI.get(0);

                nodeContent = ontologyLabel;
                if (writeUrisInXml)
                    nodeContent = normalisedText + "|" + labelAndURI.get(1);
            }
        }
        // if neither semantic nor writeasboolean
        if (!modelAttribute.isSemantic() && !modelAttribute.isWriteElementsAsBool()) nodeContent = normalisedText;

        // pair node content with the appropriate tags

        if (nodeContent == null)
            System.out.println();

        if (modelAttribute.isJoinWithNext()) return openTag + nodeContent + xmlDelimiter;
        if (modelAttribute.isJoinWithPrevious()) return nodeContent + closeTag;

        else return openTag + nodeContent + closeTag;
    }
//

    private static void buildSearchTerms(ModelSpreadsheet.ModelWorksheet.ModelAttribute modelAttribute, String text) {

        text = text.replaceAll("\n", " ");

        //todo: this is currently hackery to get search to work easily
        // the program will use this single node of text to execute the search
        // the text blob will not appear in the UI
        if (modelAttribute.isContainsSearchTerms()) {

            // unless the column is the description one
            if (!modelAttribute.getAttributeName().equals("Description")) {
                String line = "<Term><TermName>" + text + trailingspace + "</TermName></Term>";
                searchTermsCache.add(line);
            }

            // if the column profile is not an enumerated list
            if (!modelAttribute.isDoParseEnum()) {
                // just concatenate the text into the search field stash for this row
                textForSearchTerms += (text + " | ");
                // and add the text to the global list of terms
            }
            // otherwise if the profile IS an enumerated list, populate the search field with
            // the name of the header (rather than word "true" which would be pointless to string match in search).
            else {
                textForSearchTerms += modelAttribute.getAttributeName() + " | ";
            }
        }
    }

    private static String getTagName(ModelSpreadsheet.ModelWorksheet.ModelAttribute modelAttribute, String nodeContent) {

        if (modelAttribute.isJoinWithPrevious()) return tagTextForJoins;

        // Use the column header name as the open tag
        String tagName = modelAttribute.getAttributeName();

        // if the column has enumerated values to be written in the style of <interfacesWebGUI>true</interfacesWebGUI>
        if (modelAttribute.isWriteElementsAsBool()) {
            String label = "";

            if (modelAttribute.getOntologyWorksheet().containsKey(nodeContent.toLowerCase().replaceAll("-", " "))) {
                label = modelAttribute.getOntologyWorksheet().getLabel(nodeContent);
                if (label == null) {
                    label = "Other";
                    log.warn("No ontology term label for " + nodeContent);
                }
            } else label = "Other";

            // and change the tag name by appending the value
            tagName += label.replaceAll(" |-", "");
        }

        if (modelAttribute.isJoinWithNext()) tagTextForJoins = tagName;

        return tagName;
    }

    private static String validateContact(String contactString) {


        if (contactString.equals("")) return "";

        String normalisedEmail = "";

        contactString = contactString.trim();

        if (contactString.contains(" ") || contactString.contains(","))
            log.warn("Contact \"" + contactString + "\" not properly formatted.");


        int indexOfBracket = contactString.indexOf('<');
        if (indexOfBracket != -1) {
            int indexOfCloseBracket = contactString.indexOf('>');
            contactString = contactString.substring(indexOfBracket + 1, indexOfCloseBracket);
        }

        if (contactString.contains("@")) {
            if (contactString.contains("mailto:")) return contactString;
            else normalisedEmail = "mailto:" + contactString;
        } else {
            if (contactString.contains("http://") || contactString.contains("https://")) return contactString;
            else return "http://" + contactString;
        }

        return normalisedEmail;
    }

    private static ArrayList<String> getLabelAndURI(ModelSpreadsheet.ModelWorksheet.ModelAttribute modelAttribute, String enteredTerm) {

        checkParams(modelAttribute, enteredTerm);


        if (!modelAttribute.isSemantic()) {
            log.warn("Model attribute " + modelAttribute.getAttributeName() + " does not call for ontology terms.");
            return null;
        }

        String uri = modelAttribute.getOntologyWorksheet().getUri(enteredTerm.toLowerCase());
        String label = modelAttribute.getOntologyWorksheet().getLabel(enteredTerm.toLowerCase());

        // if there is no exact match for term
        if (uri == null) {
            log.warn("No exact match URI found within: \"" + modelAttribute.getAttributeName() + "\" for \"" + enteredTerm + "\""); //todo: link to spreadsheet

            // getLabel fuzzy matches
            HashMap<String, OntologyWorksheet.OntologyEntry> ontologyEntryHashMap = modelAttribute.getOntologyWorksheet().getOntologyEntries();
            ArrayList<String> matchedTerms = getFuzzyMatch(enteredTerm, ontologyEntryHashMap);

            int numURIsMatched = matchedTerms.size();

            // if numURIsMatched == 0, log the term request
            if (numURIsMatched == 0) {
                storeTermRequest(modelAttribute.getOntologySourceUrl(), enteredTerm);
                // so that the xml parsing can continue, just return a temporary value.
                uri = termRequestNowIssued;
                label = enteredTerm;
            }

            // if only one fuzzy match matchingURI, take it and warn
            else if (numURIsMatched == 1) {
                String matchedTerm = matchedTerms.get(0);
                log.warn("Fuzzy match accepted for \"" + enteredTerm + "\":\"" + matchedTerm + "\"");
                //todo: dynamically prompt user in each case?
                uri = ontologyEntryHashMap.get(matchedTerm.toLowerCase()).getUri();
                label = matchedTerm;
            }


            // if more than one fuzzy match matchingURI, don't make assumptions
            else if (numURIsMatched >= 1) {
                //todo: fix this
                String matchedTerm = matchedTerms.get(0);
                uri = ontologyEntryHashMap.get(matchedTerm.toLowerCase()).getUri();
                label = matchedTerm;
                log.warn("Fuzzy match accepted for \"" + enteredTerm + "\":\"" + matchedTerm + "\"");
            }


        }

        assert uri != null;
        if (uri.equals("TBD")) uri = termRequestAlreadyPending;

        uri = cleanAmpersands(true, uri);

        ArrayList<String> labelAndUri = new ArrayList<String>();
        labelAndUri.add(label);
        labelAndUri.add(uri);
        return labelAndUri;
    }

    private static void checkParams(ModelSpreadsheet.ModelWorksheet.ModelAttribute modelAttribute, String enteredTerm) {
        if (modelAttribute == null) {
            log.error("ModelAttribute corresponding to " + enteredTerm + " is null.");
            System.exit(1);
        }


        if (enteredTerm == null || enteredTerm.equals(""))
            log.warn("Entered term is null for this column: " + modelAttribute.getTitle());

        if (modelAttribute.getOntologyWorksheet() == null) {
            log.error("The OntTermMap for ModelAttribute corresponding to \"" + enteredTerm + "\" is null.");
            System.exit(1);
        }
    }

    public static void storeTermRequest(String ontUrl, String enteredTerm) {

        // if a term request has not already been filed for this attribute make a new entry
        if (!termRequests.containsKey(ontUrl)) {
            HashSet<String> enteredTerms = new HashSet<String>();
            enteredTerms.add(enteredTerm);
            termRequests.put(ontUrl, enteredTerms);
        }
        // else, if a term request has already been filed for this column, add to it.
        else {
            termRequests.get(ontUrl).add(enteredTerm);
        }

    }

    private static ArrayList<String> getFuzzyMatch(String enteredTerm, HashMap<String, OntologyWorksheet.OntologyEntry> ontologyEntryHashMap) {

        ArrayList<String> matchedEntities = new ArrayList<String>();


        for (Map.Entry<String, OntologyWorksheet.OntologyEntry> entry : ontologyEntryHashMap.entrySet()) {
            if (TextUtils.isFuzzyMatch(enteredTerm.toLowerCase(), entry.getKey().toLowerCase(), 3, 5)) {
                matchedEntities.add(entry.getValue().getLabel());
            }
        }

        return matchedEntities;
    }


    private static StringBuilder arrayListToString(ArrayList<String> strings) {

        StringBuilder result = new StringBuilder();

        for (String string : strings) {
            result.append("\n");
            result.append(string);
        }

        return result;
    }

    public static Set<String> getSearchTermsCache() {
        return searchTermsCache;
    }


    public static String normaliseText(ModelSpreadsheet.ModelWorksheet.ModelAttribute modelAttribute, String originalString) {

        String cleanString = originalString
                .replaceAll("\\r?\\n", " ").replaceAll(" +", " ")
                .replaceAll("<", "&lt;").replaceAll(">", "&gt;").trim();


        if (modelAttribute.isEmail())
            cleanString = validateContact(cleanString);

//        if (modelAttribute.isSemantic()) {
//            cleanString = modelAttribute.getOntologyWorksheet().getLabel(cleanString);
//        }

        return cleanString;
    }


    public static String cleanAmpersands(boolean isURL, String originalString) {
        // if the string is a URL
        if (isURL) {
//            if(!urlExists(originalString))brokenLinks.add(originalString);
            return originalString.replaceAll("&", "&amp;").replaceAll("&amp;amp;", "&amp;");
        }
        // if the string isn't a URL, but contains "&" (an illegal character), clean it
        else if (originalString.contains("&")) {
            return originalString.replaceAll("&", " and ").replaceAll(" +", " ");
        }

        // if the string is neither a URL nor contains ampersands, just return the original.
        else return originalString;
    }

    public static boolean urlExists(String URLName) {
        try {
            HttpURLConnection.setFollowRedirects(false);
            // note : you may also need
            //        HttpURLConnection.setInstanceFollowRedirects(false)
            HttpURLConnection con =
                    (HttpURLConnection) new URL(URLName).openConnection();
            con.setRequestMethod("HEAD");
            return (con.getResponseCode() == HttpURLConnection.HTTP_OK);
        } catch (Exception e) {
            log.error(URLName + " is broken.");
            return false;
        }
    }

    public static StringBuilder initalizeXml(String groupName) {

        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" ?>");
        sb.append("<" + groupName);
        //todo: parameterise this
//        sb.append(" xmlns=\"http://wwwdev.ebi.ac.uk/fgpt/toolsui/schema\"\n" +
//                "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n");
        sb.append(" xmlns=\"http://wwwdev.ebi.ac.uk/fgpt/toolsui/schema\"\n" +
                "       xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                "        >\n" +
                "    xsi:schemaLocation=\"\n" +
                "    http://www.ebi.ac.uk/fgpt/toolsui/schema\n" +
                "    http://www.ebi.ac.uk/fgpt/toolsui/2014/10/29/schema.xsd\n" +
                "    \">");
        return sb;
    }

    public static void initialiseSearchTermsCache(String filename)  {

        BufferedReader in = null;
        try {
            in = new BufferedReader(new FileReader(new File(filename)));
        } catch (FileNotFoundException e) {
            log.error(filename + " could not be found." );
            e.printStackTrace();  //todo:
        }

        try {
            assert in != null;
            while (in.ready()) {
                String line = in.readLine();
                searchTermsCache.add("<Term><TermName>"+line+"</TermName></Term>");
            }
            in.close();

        } catch (IOException e) {
            e.printStackTrace();  //todo:
        }
    }

    public static StringBuilder finaliseXml(String groupName) {
        StringBuilder sb = new StringBuilder();
        sb.append(closeTag(groupName));
        return sb;
    }

    public static Set<String> getBrokenLinks() {
        return brokenLinks;
    }

}
