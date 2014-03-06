package client.Utils;

import client.SpreadsheetSources.ModelSpreadsheet;
import client.SpreadsheetSources.OntologyWorksheet;
import com.google.gdata.data.spreadsheet.CellEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private static String trailingspace = " ";


    public static Map<String, Set<String>> getTermRequests() {

        return termRequests;
    }

//    private static void checkDuplicateEntries(ContentSpreadsheet.ContentWorksheet worksheet) {
//
//        Set<String> duplicates = new HashSet<String>();
//
//        List<CellEntry> rowTitleCells = worksheet.getRowTitleCells();
//        List<String> rowTitles = new ArrayList<String>();
//
//        for (int i = 1; i < rowTitleCells.size(); i++) {
//            String rowTitle = rowTitleCells.getLabel(i).getPlainTextContent();
//            rowTitles.add(rowTitle);
//        }
//
//        for (String text : rowTitles) {
//            int occurrences = Collections.frequency(rowTitles, text);
//            if (occurrences > 1) duplicates.add(text + " occurs " + occurrences + " times in the worksheet.");
//
//        }
//
//        if (duplicates.size() > 0) for (String duplicate : duplicates) {
//            log.warn(duplicate);
//        }
//
//        else log.info("No duplicates found.");
//
//    }

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
                String[] multiVals = cellText.split("\\n|\\||,|;");

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

        // remove line breaks, clean double spaces, check urls, emails etc
        String normalisedText = normaliseText(modelAttribute, originalText);

        // clean ampersands
        normalisedText = cleanAmpersands(modelAttribute.isURL(), normalisedText);

        if (modelAttribute.isSemantic()) {
            String ontologyLabel = modelAttribute.getOntologyWorksheet().getLabel(normalisedText);
            if(ontologyLabel == null){
                storeTermRequest(modelAttribute.getOntologySourceUrl(),normalisedText);
            }
        }

        // fetch the string that will be used as the open xml tag
        String tagName = getTagName(modelAttribute, normalisedText);

        // build up the search term string for later
        buildSearchTerms(modelAttribute, normalisedText);

        String result = getElementResult(modelAttribute, openTag(tagName), normalisedText, closeTag(tagName), writeUrisInXml);

        // todo: this is a hack for jon as the only values that need to omit trailing spaces are the boolean ones
        return result.replace("true <", "true<");

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

    private static String getElementResult(ModelSpreadsheet.ModelWorksheet.ModelAttribute modelAttribute, String openTag, String normalisedText, String closeTag, boolean writeUrisInXml) {
        String nodeContent = "";
        String ontologyLabel = "";

        String elementURI = getElementURI(modelAttribute, normalisedText);

        // isWriteElementsAsBool is elements in the style of <interfaceWebUI>true</interfaceWebUI>
        if (modelAttribute.isWriteElementsAsBool()) {
            if (elementURI != null) {
                nodeContent = "true";
            } else nodeContent = "not found";
        }
        // if issemantic, check that normalised text corresponds to an ontology term
        // if it does, fetch the uri
        else if (modelAttribute.isSemantic()) {
            if (elementURI != null) {
                ontologyLabel = normalisedText;
            } else ontologyLabel = modelAttribute.getOntologyWorksheet().getLabel(normalisedText);

            nodeContent = ontologyLabel;
            if (writeUrisInXml)
                nodeContent = normalisedText + "|" + elementURI;
        }
        // if neither semantic nor writeasboolean
        else nodeContent = normalisedText;

        // pair node content with the appropriate tags

        if (modelAttribute.isJoinWithNext()) return openTag + nodeContent + xmlDelimiter;
        if (modelAttribute.isJoinWithPrevious()) return nodeContent + closeTag;
        else return openTag + nodeContent + closeTag;
    }
//
//    private static String getOntologyLabel(ModelSpreadsheet.ModelWorksheet.ModelAttribute modelAttribute, String synonym) {
//
//        if (modelAttribute == null) {
//            log.error("ModelAttribute corresponding to " + synonym + " is null.");
//            System.exit(1);
//        }
//
//        if (synonym == null || synonym.equals(""))
//            log.warn("Entered term is null for this column: " + modelAttribute.getTitle());
//
//        if (modelAttribute.getOntologyWorkbook() == null) {
//            log.error("The OntTermMap for ModelAttribute corresponding to \"" + synonym + "\" is null.");
//            System.exit(1);
//        }
//
//        String matchingLabel = modelAttribute.getOntologyWorksheet().getLabel();
//
//        // if there is no exact match for term
//        if (matchingLabel == null) {
//            log.warn("No exact match label found within: \"" + modelAttribute.getAttributeName() + "\" for \"" + synonym + "\""); //todo: link to spreadsheet
//
//            // getLabel fuzzy matches
//            ArrayList<String> matchedLabels = getFuzzyMatch(synonym, synonymLabelMap);
//
//            int numLabelsMached = matchedLabels.size();
//
//            // if numURIsMatched == 0, log the term request
//            if (numLabelsMached == 0) {
//                storeTermRequest(modelAttribute, synonym);
//                // so that the xml parsing can continue, just return a temporary value.
//                matchingLabel = synonym;
//            }
//
//            // if only one fuzzy match matchingURI, take it and warn
//            else if (numLabelsMached == 1) {
//                String matchedLabel = matchedLabels.getLabel(0);
//                log.warn("Fuzzy match accepted for \"" + synonym + "\":\"" + matchedLabel + "\"");
//                //todo: dynamically prompt user in each case?
//                matchingLabel = synonymLabelMap.getLabel(matchedLabel);
//            }
//
//
//            // if more than one fuzzy match matchingURI, don't make assumptions
//            else if (numLabelsMached >= 1) {
//                //todo: fix this
//                String matchedLabel = matchedLabels.getLabel(0);
//                matchingLabel = synonymLabelMap.getLabel(matchedLabel);
//                log.warn("Fuzzy match accepted for \"" + synonym + "\":\"" + matchedLabel + "\"");
////                return matchingURI;
//
////                int matchedTermsSize = matchedTerms.size();
////
////                String msg = "The following " + matchedTermsSize + " matches found: ";
////
////                for (int i = 0; i < matchedTermsSize; i++) {
////
////                    msg += "\n" + i + ": " + matchedTerms.getLabel(i);
////                }
////
////                log.warn("Please select the number corresponding to the desired match or enter -1 to indicate none of these: " + msg);
////
////                int selection = 0;
////                try {
////                    selection = getIntegerFromUser();
////                } catch (IOException e) {
////                    e.printStackTrace();  //todo:
////                }
////                if (selection > -1 && selection < matchedTermsSize) return matchedTerms.getLabel(selection);
////                else return termRequestNowIssued;
//
//            }
//
//
//        }
//
//        assert matchingLabel != null;
//
//        return matchingLabel;
//    }
//

    private static void buildSearchTerms(ModelSpreadsheet.ModelWorksheet.ModelAttribute modelAttribute, String text) {

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

//    private static String getCloseTag(ModelAttribute columnProfile, String text) {
//        if (columnProfile.isJoinWithPrevious()) {
//            return closeTag(tagTextForJoins);
//        }
//
//        // Else, use the column header name as the close tag
//        String tagName = columnProfile.getAttributeName();
//
//        // if the column has enumerated values in it
//        if (columnProfile.doParseEnum()) {
//
//            // and change the tag name by appending the value
//            tagName += text.replaceAll(" |-", "");
//        }
//
//        return closeTag(tagName);
//
//    }

    private static String getTagName(ModelSpreadsheet.ModelWorksheet.ModelAttribute modelAttribute, String nodeContent) {

        if (modelAttribute.isJoinWithPrevious()) return tagTextForJoins;

        // Use the column header name as the open tag
        String tagName = modelAttribute.getAttributeName();

        // if the column has enumerated values to be written in the style of <interfacesWebGUI>true</interfacesWebGUI>
        if (modelAttribute.isWriteElementsAsBool()) {
            String label = "";

            if (modelAttribute.getOntologyWorksheet().containsKey(nodeContent)) {
                label = modelAttribute.getOntologyWorksheet().getLabel(nodeContent);
                if (label == null) {
                    label = nodeContent;
                    log.warn("No ontology term label for " + nodeContent);
                }
            } else label = nodeContent;

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

    private static String getElementURI(ModelSpreadsheet.ModelWorksheet.ModelAttribute modelAttribute, String enteredTerm) {

        if (modelAttribute == null) {
            log.error("ModelAttribute corresponding to " + enteredTerm + " is null.");
            System.exit(1);
        }

        if (!modelAttribute.isSemantic()){
            log.warn("Model attribute "+modelAttribute.getAttributeName()+" does not call for ontology terms.");
            return null;
        }

        if (enteredTerm == null || enteredTerm.equals(""))
            log.warn("Entered term is null for this column: " + modelAttribute.getTitle());

        if (modelAttribute.getOntologyWorksheet() == null) {
            log.error("The OntTermMap for ModelAttribute corresponding to \"" + enteredTerm + "\" is null.");
            System.exit(1);
        }

        String matchingURI = modelAttribute.getOntologyWorksheet().getUri(enteredTerm);

        // if there is no exact match for term
        if (matchingURI == null) {
            log.warn("No exact match URI found within: \"" + modelAttribute.getAttributeName() + "\" for \"" + enteredTerm + "\""); //todo: link to spreadsheet

            // getLabel fuzzy matches
            HashMap<String, OntologyWorksheet.OntologyEntry> ontologyEntryHashMap = modelAttribute.getOntologyWorksheet().getOntologyEntries();
            ArrayList<String> matchedTerms = getFuzzyMatch(enteredTerm, ontologyEntryHashMap);

            int numURIsMatched = matchedTerms.size();

            // if numURIsMatched == 0, log the term request
            if (numURIsMatched == 0) {
                storeTermRequest(modelAttribute.getOntologySourceUrl(), enteredTerm);
                // so that the xml parsing can continue, just return a temporary value.
                matchingURI = termRequestNowIssued;
            }

            // if only one fuzzy match matchingURI, take it and warn
            else if (numURIsMatched == 1) {
                String matchedTerm = matchedTerms.get(0);
                log.warn("Fuzzy match accepted for \"" + enteredTerm + "\":\"" + matchedTerm + "\"");
                //todo: dynamically prompt user in each case?
                matchingURI = ontologyEntryHashMap.get(matchedTerm).getUri();
            }


            // if more than one fuzzy match matchingURI, don't make assumptions
            else if (numURIsMatched >= 1) {
                //todo: fix this
                String matchedTerm = matchedTerms.get(0);
                matchingURI = ontologyEntryHashMap.get(matchedTerm).getUri();
                log.warn("Fuzzy match accepted for \"" + enteredTerm + "\":\"" + matchedTerm + "\"");
//                return matchingURI;

//                int matchedTermsSize = matchedTerms.size();
//
//                String msg = "The following " + matchedTermsSize + " matches found: ";
//
//                for (int i = 0; i < matchedTermsSize; i++) {
//
//                    msg += "\n" + i + ": " + matchedTerms.getLabel(i);
//                }
//
//                log.warn("Please select the number corresponding to the desired match or enter -1 to indicate none of these: " + msg);
//
//                int selection = 0;
//                try {
//                    selection = getIntegerFromUser();
//                } catch (IOException e) {
//                    e.printStackTrace();  //todo:
//                }
//                if (selection > -1 && selection < matchedTermsSize) return matchedTerms.getLabel(selection);
//                else return termRequestNowIssued;

            }


        }

        assert matchingURI != null;
        if (matchingURI.equals("TBD")) matchingURI = termRequestAlreadyPending;

        matchingURI = cleanAmpersands(true, matchingURI);

        return matchingURI;
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
                matchedEntities.add(entry.getKey());
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

        String cleanString = originalString.replaceAll("\\r?\\n", " ").replaceAll(" +", " ").trim();


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
            return originalString.replaceAll("&", "&amp;").replaceAll("&amp;amp;", "&amp;");
        }
        // if the string isn't a URL, but contains "&" (an illegal character), clean it
        else if (originalString.contains("&")) {
            return originalString.replaceAll("&", " and ").replaceAll(" +", " ");
        }

        // if the string is neither a URL nor contains ampersands, just return the original.
        else return originalString;
    }

    public static StringBuilder initalizeXml(String groupName) {
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" ?>");
        sb.append(openTag(groupName));
        return sb;
    }

    public static StringBuilder finaliseXml(String groupName) {
        StringBuilder sb = new StringBuilder();
        sb.append(closeTag(groupName));
        return sb;
    }

}
