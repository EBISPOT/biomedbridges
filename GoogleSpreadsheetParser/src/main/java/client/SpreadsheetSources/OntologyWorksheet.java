package client.SpreadsheetSources;

import client.Utils.GDataUtils;
import client.Utils.TextUtils;
import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.spreadsheet.WorksheetEntry;
import com.google.gdata.util.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: jmcmurry
 * Date: 26/02/2014
 * Time: 15:15
 * To change this template use File | Settings | File Templates.
 */
public class OntologyWorksheet {

    private final String fullWorksheetUrl;
    String[][] ontWorksheetTable;
    int indexOfFirstDataRow = -1;
    boolean addSynonyms;

    HashMap<String, OntologyEntry> ontologyRowsCache;
    WorksheetEntry worksheetEntry;

    protected Logger log = LoggerFactory.getLogger(getClass());

    OntologyWorksheet(String fullWorksheetUrl, SpreadsheetService authenticatedService, int indexOfFirstDataRow, boolean addSynonyms) throws ServiceException, IOException, URISyntaxException {

        System.out.println();
        getLog().info("Loading ontology at " + fullWorksheetUrl);
        this.worksheetEntry = GDataUtils.getWorksheetFromUrl(authenticatedService, fullWorksheetUrl);
        this.fullWorksheetUrl = fullWorksheetUrl;
        this.ontWorksheetTable = GDataUtils.getTableOfStrings(worksheetEntry, authenticatedService);
        this.indexOfFirstDataRow = indexOfFirstDataRow;
        this.ontologyRowsCache = loadOntologyRows();
        this.addSynonyms = addSynonyms;
    }

    private HashMap<String, OntologyEntry> loadOntologyRows() {

        HashMap<String, OntologyEntry> queryStringOntEntryMap = new HashMap<String, OntologyEntry>();

        for (int row = indexOfFirstDataRow; row < worksheetEntry.getRowCount(); row++) {

            String ontLabel = ontWorksheetTable[row][1];
            String uri = ontWorksheetTable[row][2];
            String synonymString = "";
            if (addSynonyms) synonymString = ontWorksheetTable[row][3];

            OntologyEntry ontologyEntry = null;

//            getLog().debug("Parsing row " + row + ". " + ontLabel + ": " + uri);

//                if (addSynonyms) {
//                    String synonymString = ontologyWorksheetTable[row][3];
//                    if (synonymString != null) {
//                        String delim = "\\|";
//                        String[] synonyms = synonymString.split(delim);
//
//                        String label = ontLabel;
//
//                        for (String synonym : synonyms) {
//                            synonymLabelMap.put(synonym, label);
//                        }
//                    }
//                }
            if (!ontLabel.equals("")) {
                if (uri.equals("")) {
                    getLog().warn("URI is blank at " + worksheetEntry + "at row " + row + " column " + 2
                            + "\n" + fullWorksheetUrl);
                }

                ontologyEntry = new OntologyEntry(fullWorksheetUrl, ontLabel, synonymString, uri, row);

                ArrayList<String> queryStrings = ontologyEntry.getQueryStringsInLowercase();

                // so that URI can be fetched from termLabel and from synonyms

                for (String queryString : queryStrings) {
                    queryStringOntEntryMap.put(queryString, ontologyEntry);
                }
            }
        }
        return queryStringOntEntryMap;
    }

//    public String getLabel(String queryString) {
//
//        queryString = queryString.toLowerCase();
//
//        return null;  //To change body of created methods use File | Settings | File Templates.
//    }

    public boolean containsKey(String nodeContent) {
        return (ontologyRowsCache.containsKey(nodeContent));
    }

    public String getLabel(String queryString) {

        String ontologyLabel = null;

        if (queryString == null || queryString.equals("")) {
            log.warn("Query string is null or empty");
            Thread.dumpStack();
        } else queryString = queryString.toLowerCase();
        log.debug("Getting label from query string: '" + queryString + "'");
        if (ontologyRowsCache.get(queryString) != null) {
            ontologyLabel = ontologyRowsCache.get(queryString).label;
            log.debug("Query result from ontolgy entry cache" + ontologyLabel);
        }
        return ontologyLabel;
    }

    public String getUri(String queryString) {

        String uri = null;

        if (queryString == null || queryString.equals("")) {
            log.warn("Query string is null or empty");
            Thread.dumpStack();

        } else queryString = queryString.toLowerCase();

        if (ontologyRowsCache.get(queryString) != null) {
            uri = ontologyRowsCache.get(queryString).uri;
            log.debug("Query result from ontolgy entry cache" + uri);
        }
        return uri;
    }


    /**
     * Created with IntelliJ IDEA.
     * User: jmcmurry
     * Date: 29/05/2013
     * Time: 17:25
     * To change this template use File | Settings | File Templates.
     */
    public class OntologyEntry implements Serializable {

        private final String termGroup;
        private final String uri;
        private final String label;
        private final int row;
        private final List<String> synonymsInLowercase;

        public OntologyEntry(String termGroup, String label, String synonymString, String uri, int row) {

            if (termGroup == null || label == null || uri == null) {
                log.error("Null parameter in ontology entry instantiation.");
            }
            this.termGroup = termGroup.trim();
            this.label = label.trim();
            this.uri = uri.trim();
            this.row = row;
            this.synonymsInLowercase = TextUtils.stringArrayToLowercase(Arrays.asList(synonymString.split("\\|,")));
            getLog().debug(toString());
        }

        public String toString() {
            String result = "Ont entry row " + row + ": \t" + this.label + "\t" + this.uri;
            for (String synonym : synonymsInLowercase) {
                result += "\t" + synonym;
            }

//            result+="\t for "+ this.termGroup;
            return result;
        }

        public int compareTo(OntologyEntry that) {

            String thisTermGroup = this.termGroup.toLowerCase();
            String thatTermGroup = that.termGroup.toLowerCase();

            if (!thisTermGroup.equals(thatTermGroup)) {
                return thisTermGroup.compareTo(thatTermGroup);
            } else {

                String thisTerm = this.label.toLowerCase();
                String thatTerm = that.label.toLowerCase();
                return thisTerm.compareTo(thatTerm);
            }
        }

        public ArrayList<String> getQueryStringsInLowercase() {

            ArrayList<String> queryStrings = new ArrayList<String>();
            queryStrings.addAll(synonymsInLowercase);
            queryStrings.add(label.toLowerCase());
            return queryStrings;
        }


        public String getUri() {
            return uri;
        }


    }

    public Logger getLog() {
        return log;
    }


    public HashMap<String, OntologyEntry> getOntologyEntries() {
        return ontologyRowsCache;
    }


}
