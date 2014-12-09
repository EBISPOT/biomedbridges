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
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: jmcmurry
 * Date: 26/02/2014
 * Time: 15:15
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
        this.addSynonyms = addSynonyms;
        this.ontologyRowsCache = loadOntologyRows();
    }

    private HashMap<String, OntologyEntry> loadOntologyRows() {

        HashMap<String, OntologyEntry> queryStringOntEntryMap = new HashMap<String, OntologyEntry>();

        for (int row = indexOfFirstDataRow; row <= worksheetEntry.getRowCount(); row++) {

            String ontLabel = ontWorksheetTable[row][1];
            String uri = ontWorksheetTable[row][2];
            String synonymString = "";
            if (addSynonyms) synonymString = ontWorksheetTable[row][3];

            OntologyEntry ontologyEntry = null;

            getLog().debug("Parsing row " + row + ". " + ontLabel + ": " + uri);

            if (addSynonyms) {
                synonymString = ontWorksheetTable[row][3];
            }

            if (!ontLabel.equals("")) {
                if (uri.equals("")) {
                    getLog().warn("URI is blank at " + worksheetEntry + "at row " + row + " column " + 2
                            + "\n" + fullWorksheetUrl);
                }

                ontologyEntry = new OntologyEntry(fullWorksheetUrl, ontLabel, synonymString, uri, row);

                Set<String> queryStrings = ontologyEntry.getQueryStringsInLowercase();

                // so that URI can be fetched from termLabel and from synonyms

                for (String queryString : queryStrings) {
                    queryStringOntEntryMap.put(queryString, ontologyEntry);
                }
            }
        }
        return queryStringOntEntryMap;
    }

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
            log.debug("Query result from ontolgy entry cache: " + ontologyLabel);
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
            log.debug("Query result from ontology entry cache: " + uri);
        }
        return uri;
    }

    public OntologyEntry getOntologyEntry(String queryString){
        OntologyEntry entry = null;

        if (queryString == null || queryString.equals("")) {
            log.warn("Query string is null or empty");
            Thread.dumpStack();

        } else queryString = queryString.toLowerCase();

        if (ontologyRowsCache.get(queryString) != null) {
            entry = ontologyRowsCache.get(queryString);
            log.debug("Query result from ontology entry cache: " + entry);
        }
        return entry;
    }


    /**
     * Created with IntelliJ IDEA.
     * User: jmcmurry
     * Date: 29/05/2013
     * Time: 17:25
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
            this.synonymsInLowercase = new ArrayList<String>();
            if (synonymString != null)
                synonymsInLowercase.addAll(TextUtils.stringArrayToLowercase(Arrays.asList(synonymString.split("[\\|,]+"))));
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

        public Set<String> getQueryStringsInLowercase() {

            Set<String> queryStrings = new HashSet<String>();
            queryStrings.addAll(synonymsInLowercase);
            queryStrings.add(label.toLowerCase());
            return queryStrings;
        }


        public String getUri() {
            return uri;
        }


        public String getLabel() {
            return label;
        }
    }

    public Logger getLog() {
        return log;
    }


    public HashMap<String, OntologyEntry> getOntologyEntries() {
        return ontologyRowsCache;
    }


}
