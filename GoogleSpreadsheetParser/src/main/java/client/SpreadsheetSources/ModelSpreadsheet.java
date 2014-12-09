package client.SpreadsheetSources;

import client.Utils.GDataUtils;
import client.Utils.TextUtils;
import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.spreadsheet.CellEntry;
import com.google.gdata.data.spreadsheet.WorksheetEntry;
import com.google.gdata.util.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URISyntaxException;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: jmcmurry
 * Date: 14/02/2014
 * Time: 09:25
 */
public class ModelSpreadsheet extends EnhancedSpreadsheet {

    //used to populate the data model attributes
    private HashMap<String, OntologyWorksheet> ontologyModelCache = new HashMap<String, OntologyWorksheet>();

    //
    private HashMap<String, ModelWorksheet.ModelAttribute> dataModel = new HashMap<String, ModelWorksheet.ModelAttribute>();

    private final String pathToSerialisedOntologies;

    //synonyms for ontology terms, not data model attributes
    private boolean addSynonyms = false;
    protected ModelWorksheet modelWorksheet = null;

    private Logger log = LoggerFactory.getLogger(getClass());

    public ModelSpreadsheet(boolean loadOntologiesFromLocalFile, boolean addSynonyms, String pathToSerialisedOntologies, SpreadsheetService authenticatedService, HashMap<String, String> row) throws IOException, ServiceException, URISyntaxException {
        super(authenticatedService, row);

        this.addSynonyms = addSynonyms;
        this.pathToSerialisedOntologies = pathToSerialisedOntologies;
        this.modelWorksheet = setDataModelWorksheet();

        getLog().info("Loading ontologies required by data model.");

        if (loadOntologiesFromLocalFile) {
            ontologyModelCache = modelWorksheet.deserializeOntologies();
        } else {
            ontologyModelCache = modelWorksheet.fillOntologiesCacheFromSpreadsheet();
        }

        modelWorksheet.loadDataModel();
        getLog().info("Data model loaded. Ready to begin parsing content.\n");

    }


    private ModelWorksheet setDataModelWorksheet() throws IOException, ServiceException, URISyntaxException {


        ModelWorksheet modelWorksheet;

        getLog().info("Loading DATA MODEL worksheet.");

        if (indicesOfTabsToParse.size() > 1) {
            getLog().error("At this time only one tab supported for data model");
            System.exit(0);
        }

        int parentTabIndex = indicesOfTabsToParse.get(0);

        getLog().info("Loading DATA MODEL from worksheet...");

        modelWorksheet = new ModelWorksheet(GDataUtils.getWorksheetFromIndex(getSpreadsheetEntry(), parentTabIndex));

        return modelWorksheet;
    }


    public HashMap<String, ModelWorksheet.ModelAttribute> getDataModel() {
        return dataModel;
    }


    public class ModelWorksheet extends EnhancedWorksheet {

        ModelWorksheet(WorksheetEntry worksheetEntry) throws ServiceException, IOException, URISyntaxException {
            super(worksheetEntry);
        }

        public void loadDataModel() throws IOException, ServiceException, URISyntaxException {

            checkOntologiesCache();

            for (int rowIndex = getIndexOfFirstDataRow(); rowIndex <= getRowCount(); rowIndex++) {

                HashMap<String, String> attributeRow = GDataUtils.getRowAsHashMap(authenticatedService, this.getWorksheetEntry(), rowIndex, correspondingHeaders);
                ModelAttribute attribute = new ModelAttribute(attributeRow);

                dataModel.put(attribute.attributeName.toLowerCase(), attribute);

                if (attribute.attributeNameSynonymList != null && attribute.attributeNameSynonymList.size() > 0) {
                    for (String synonym : attribute.attributeNameSynonymList) {
                        dataModel.put(synonym.toLowerCase(), attribute);
                    }
                }
            }
        }

        public HashMap<String, OntologyWorksheet> deserializeOntologies() {

            getLog().info("Loading ontologies from local serialized source at " + pathToSerialisedOntologies);

            HashMap<String, OntologyWorksheet> ontSourceMap = null;
            try {
                FileInputStream fileIn = new FileInputStream(pathToSerialisedOntologies);
                ObjectInputStream in = new ObjectInputStream(fileIn);
                ontSourceMap = (HashMap<String, OntologyWorksheet>) in.readObject();
                in.close();
                fileIn.close();
                return ontSourceMap;

            } catch (IOException i) {
                i.printStackTrace();
                return null;
            } catch (ClassNotFoundException c) {
                System.out.println("HashMap<String, OntologyWorksheet> class not found");
                c.printStackTrace();
                return null;
            }
        }

        private HashMap<String, OntologyWorksheet> fillOntologiesCacheFromSpreadsheet() {


            HashMap<String, OntologyWorksheet> ontologiesCache = new HashMap<String, OntologyWorksheet>();

            int indexOfColumnOfOntUrls = Arrays.asList(correspondingHeaders).indexOf("ontologySourceUrl");

            getLog().info("Loading ontologies from worksheet sources listed in DATA MODEL worksheet column " + indexOfColumnOfOntUrls);

            for (int row = 2; row < getRowCount(); row++) {

                String url = tableOfStrings[row][indexOfColumnOfOntUrls];

                if (url != null && !url.equals("")) {

                    OntologyWorksheet ontologyWorksheet = null;
                    try {
                        ontologyWorksheet = new OntologyWorksheet(url, authenticatedService, 2, addSynonyms);
                    } catch (ServiceException e) {
                        e.printStackTrace();  //todo:
                    } catch (IOException e) {
                        e.printStackTrace();  //todo:
                    } catch (URISyntaxException e) {
                        e.printStackTrace();  //todo:
                    }
                    ontologiesCache.put(url, ontologyWorksheet);
                }
            }
            return ontologiesCache;
        }


        private boolean checkOntologiesCache() {
            boolean ok = false;

            System.out.println();

            if (ontologyModelCache != null && !ontologyModelCache.isEmpty()) {

                getLog().info("Ontologies successfully loaded");
                ok = true;

            } else {
                log.error("Ontologies are null or empty.");
                System.exit(1);
            }

            System.out.println();

            return ok;
        }

//        private HashMap<String, OntologyWorksheet.OntologyEntry> loadOntologyFromUrl(String ontologySourceUrl) {
//
//            HashMap<String, OntologyWorksheet.OntologyEntry> ontologyForAttribute = null;
//
//            if (ontologyModelCache.containsKey(ontologySourceUrl)) {
//                WorksheetEntry ontWorksheet = GDataUtils.getWorksheetFromUrl(authenticatedService, ontologySourceUrl);
//                ontologyForAttribute = loadOntologyFromUrl(ontWorksheet, ontologySourceUrl);
//            }
//
//            return ontologyForAttribute;
//        }


//        private HashMap<String, OntologyWorksheet.OntologyEntry> loadOntologyFromUrl(WorksheetEntry worksheetEntry, String ontologyWorksheetURL) {
//
////            HashMap<String, String> termUriMap = new HashMap<String, String>();
////            HashMap<String, String> synonymLabelMap = new HashMap<String, String>();
//
//            HashMap<String, OntologyWorksheet.OntologyEntry> ontologyEntriesFromWorksheet = new HashMap<String, OntologyWorksheet.OntologyEntry>();
//
//            getLog().info("Filling terms from " + worksheetEntry.getTitle().getPlainText());
//
//            String[][] ontologyWorksheetTable = GDataUtils.getTableOfStrings(worksheetEntry, authenticatedService);
//
//            for (int row = 2; row <= worksheetEntry.getRowCount(); row++) {
//
//
//                String ontLabel = ontologyWorksheetTable[row][1];
//                String uri = ontologyWorksheetTable[row][2];
//                String synonymString = "";
//                if (addSynonyms) synonymString = ontologyWorksheetTable[row][3];
//
//                OntologyWorksheet.OntologyEntry ontologyEntry = null;
//
//                getLog().debug("Parsing row " + row + ". " + ontLabel + ": " + uri);
//
////                if (addSynonyms) {
////                    String synonymString = ontologyWorksheetTable[row][3];
////                    if (synonymString != null) {
////                        String delim = "\\|";
////                        String[] synonyms = synonymString.split(delim);
////
////                        String label = ontLabel;
////
////                        for (String synonym : synonyms) {
////                            synonymLabelMap.put(synonym, label);
////                        }
////                    }
////                }
//                if (!ontLabel.equals("")) {
//                    if (uri.equals("")) {
//                        getLog().warn("URI is blank at " + worksheetEntry + "at row " + row + " column " + 2
//                                + "\n" + ontologyWorksheetURL);
//                    }
//
//                    ontologyEntry = new OntologyWorksheet.OntologyEntry(ontologyWorksheetURL, ontLabel, synonymString, uri);
//
//                    ArrayList<String> queryStrings = ontologyEntry.getQueryStringsInLowercase();
//
//                    // so that URI can be fetched from termLabel and from synonyms
//
//                    for (String queryString : queryStrings) {
//                        ontologyEntriesFromWorksheet.put(queryString, ontologyEntry);
//                    }
//                }
//            }
//
//            return ontologyEntriesFromWorksheet;
//        }


        private void serializeOntologies() {
            try {
                FileOutputStream fileOut =
                        new FileOutputStream(pathToSerialisedOntologies);
                ObjectOutputStream out = new ObjectOutputStream(fileOut);
                out.writeObject(ontologyModelCache);
                out.close();
                fileOut.close();
                getLog().info("Serialized data is saved in " + pathToSerialisedOntologies);
            } catch (IOException i) {
                i.printStackTrace();
            }
        }

//        // to save time when in debug mode. All those REST calls take ages when retrieving a column cell by cell.
//
//        public void serializeOntology(HashMap<String, HashMap<String, String>> ontSourceMap, String pathToOntMap) {
//            try {
//                FileOutputStream fileOut =
//                        new FileOutputStream(pathToOntMap);
//                ObjectOutputStream out = new ObjectOutputStream(fileOut);
//                out.writeObject(ontSourceMap);
//                out.close();
//                fileOut.close();
//                getLog().info("Serialized data is saved in " + pathToOntMap);
//            } catch (IOException i) {
//                i.printStackTrace();
//            }
//        }

        /**
         * User: jmcmurry
         * Date: 17/05/2013
         * Time: 11:11
         */
        public class ModelAttribute extends CellEntry implements Serializable {

            private final List<String> attributeNameSynonymList;
            private String delimiter = "|";

            private final String attributeName;
            private final String elementName;

            private final boolean isSemantic;

            private boolean isURL = false; //todo
            private boolean isEmail = false;
            private boolean isBoolean = false;

            private boolean allowsMultiVal = false;
            private boolean ignore = false;
            private boolean joinWithNext = false;
            private boolean joinWithPrevious = false;
            private boolean containsSearchTerms = false;
            private boolean defaultToNotSpecified = false;
            private boolean parseEnum = false;
            private boolean writeElementsAsBool = false;
            private boolean hidden = false;

            private final String parentElement;
            private final String group;
            private final String description;
            private final String xsdType;
            private final boolean required;
            private final String ontologySourceUrl;
            protected OntologyWorksheet ontologyWorksheet;
            private boolean firstInNode;
            private boolean lastInNode;

//            HashMap<String, String> termUriMap = null;
//            HashMap<String, String> synonymLabelMap = null;


            public ModelAttribute(HashMap<String, String> attributeFields) {

                this.attributeName = attributeFields.get("attributeName");
                this.elementName = attributeFields.get("elementName");
                String attributeNameSynonymString = attributeFields.get("attributeNameSynonyms");
                this.attributeNameSynonymList = setAttributeNameSynonymsLowercase(attributeNameSynonymString);

                //Process other fields
                this.parentElement = attributeFields.get("parentElement");
                this.group = attributeFields.get("group");
                this.description = attributeFields.get("description");
                this.xsdType = attributeFields.get("xsdType");
                this.required = attributeFields.get("required").toLowerCase().contains("mandatory");
                this.hidden = TextUtils.isTrue(attributeFields.get("hidden"));
                this.ontologySourceUrl = attributeFields.get("ontologySourceUrl");

                //Process representation
                this.isSemantic = (ontologySourceUrl != null);
                if (isSemantic) {
                    this.ontologyWorksheet = ontologyModelCache.get(ontologySourceUrl);
                    if (ontologyWorksheet == null)
                        log.error("Semantic maps could not be loaded for this attribute:" + attributeName);
                }

                this.isURL = attributeFields.get("representation").toLowerCase().contains("url");
                this.isBoolean = attributeFields.get("representation").toLowerCase().contains("boolean");
                this.isEmail = attributeFields.get("representation").toLowerCase().contains("email");

                //Process parsing instructions
                this.allowsMultiVal = TextUtils.isTrue(attributeFields.get("allowsMultiVal"));
                this.ignore = TextUtils.isTrue(attributeFields.get("ignore"));
                this.joinWithNext = TextUtils.isTrue(attributeFields.get("joinWithNext"));
                this.joinWithPrevious = TextUtils.isTrue(attributeFields.get("joinWithPrevious"));
                this.containsSearchTerms = TextUtils.isTrue(attributeFields.get("containsSearchTerms"));
                this.parseEnum = TextUtils.isTrue(attributeFields.get("parseEnum"));
                this.writeElementsAsBool = TextUtils.isTrue(attributeFields.get("writeElementsAsBool"));
                this.defaultToNotSpecified = TextUtils.isTrue(attributeFields.get("defaultToNotSpecified"));

            }

            public void setDelimiter(String delimiter) {
                this.delimiter = delimiter;
            }

            public String getAttributeName() {
                return attributeName;
            }

            public String getElementName() {
                return elementName;
            }

            public boolean isSemantic() {
//                if (isSemantic) {
//                    log.debug(attributeName + " determined to be a semantic attribute.");
//                }
                return isSemantic;
            }

            public boolean isURL() {
                return isURL;
            }

            public boolean isEmail() {
                return isEmail;
            }

            public boolean isBoolean() {
                return isBoolean;
            }

            public boolean isAllowsMultiVal() {
                return allowsMultiVal;
            }

            public boolean isIgnore() {
                return ignore;
            }

            public boolean isJoinWithNext() {
                return joinWithNext;
            }

            public boolean isJoinWithPrevious() {
                return joinWithPrevious;
            }

            public boolean isContainsSearchTerms() {
                return containsSearchTerms;
            }

            public boolean isDefaultToNotSpecified() {
                return defaultToNotSpecified;
            }

            public boolean isDoParseEnum() {
                return parseEnum;
            }

            public boolean isHidden() {
                return hidden;
            }

            public String getParentElement() {
                return parentElement;
            }

            public String getGroup() {
                return group;
            }

            public String getDescription() {
                return description;
            }

            public String getXsdType() {
                return xsdType;
            }

            public boolean isRequired() {
                return required;
            }

            public String getOntologySourceUrl() {
                return ontologySourceUrl;
            }


            public String getDelimiter() {
                return delimiter;
            }


            public List<String> getAttributeNameSynonyms() {

                return this.attributeNameSynonymList;
            }

            public List<String> setAttributeNameSynonymsLowercase(String attributeNameSynonymString) {

                List<String> synonymsList = new ArrayList<String>();
                if (attributeNameSynonymString != null && !attributeNameSynonymString.equals("")) {
                    log.debug("Parsing synonyms for " + attributeName + " ...");
                    String[] synonyms = attributeNameSynonymString.split(",");
                    for (int i = 0; i < synonyms.length; i++) {
                        synonyms[i] = synonyms[i].trim();
                    }
                    synonymsList = Arrays.asList(synonyms);

                }
                return synonymsList;
            }


            public boolean isWriteElementsAsBool() {
                return writeElementsAsBool;
            }


            public OntologyWorksheet getOntologyWorksheet() {
                return ontologyWorksheet;
            }

            public boolean isFirstInNode() {
                return firstInNode;
            }

            public boolean isLastInNode() {
                return lastInNode;
            }
        }
    }


}
