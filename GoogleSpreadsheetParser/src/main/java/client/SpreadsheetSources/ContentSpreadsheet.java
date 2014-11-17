package client.SpreadsheetSources;

import client.Utils.GDataUtils;
import client.Utils.TextUtils;
import client.Utils.XmlParseUtils;
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
 * User: jmcmurry
 * Date: 30/05/2013
 */
public class ContentSpreadsheet extends EnhancedSpreadsheet {

    private final boolean writeUrisInXml;
    private Logger log = LoggerFactory.getLogger(getClass());
    private ArrayList<ContentWorksheet> contentWorksheets;
    private HashMap<String, ModelSpreadsheet.ModelWorksheet.ModelAttribute> dataModel;
    private ArrayList<String> logOfIncompleteRows = new ArrayList<String>();
    private boolean omitIncompleteRows;
    private Set<String> columnsToIgnore = new HashSet<String>();
    protected String xmlNodeNameForEachRow;
    protected String xmlNodeNameForSetOfRows;
    protected String nodeNumberingPrefix;
    private int xmlNodeNumberForRow;

    public ContentSpreadsheet(SpreadsheetService spreadsheetService, HashMap<String, String> configRow, HashMap<String, ModelSpreadsheet.ModelWorksheet.ModelAttribute> dataModel, int xmlNodeNumberForRow, boolean omitIncompleteRows, boolean writeUrisInXml) throws IOException, ServiceException, URISyntaxException {

        super(spreadsheetService, configRow);
        this.dataModel = dataModel;
        if (dataModel == null) {
            getLog().error("Data model must not be null.");
            System.exit(0);
        }
        this.xmlNodeNameForEachRow = configRow.get("xmlNodeNameForEachRow");
        this.xmlNodeNameForSetOfRows = configRow.get("xmlNodeNameForSetOfRows");
        this.nodeNumberingPrefix = configRow.get("nodeNumberingPrefix");
        this.indexOfFirstDataRow = Integer.parseInt(configRow.get("indexOfFirstDataRow"));
        this.xmlNodeNumberForRow = xmlNodeNumberForRow;
        this.omitIncompleteRows = omitIncompleteRows;
        this.writeUrisInXml = writeUrisInXml;
        contentWorksheets = loadDataSourceWorksheets();
    }

    public StringBuilder parseSpreadsheet() {

        System.out.println();
        log.info("Parsing xml for spreadsheet " + spreadsheet.getTitle().getPlainText());

        StringBuilder spreadsheetXml = new StringBuilder();
        for (ContentWorksheet contentWorksheet : contentWorksheets) {
            StringBuilder worksheetXml = contentWorksheet.parseWorksheet();
            spreadsheetXml.append(worksheetXml);
        }

        return spreadsheetXml;
    }

    private ArrayList<ContentWorksheet> loadDataSourceWorksheets() throws IOException, ServiceException, URISyntaxException {
        ArrayList<ContentWorksheet> contentWorksheets = new ArrayList<ContentWorksheet>();
        List<WorksheetEntry> worksheets = spreadsheet.getWorksheets();
        getLog().info("Loading worksheets for " + spreadsheetName + ":" + indicesOfTabsToParse);
        for (WorksheetEntry currentWorksheet : worksheets) {
            int currentIndex = GDataUtils.getIndex(currentWorksheet);
            if (indicesOfTabsToParse.contains(currentIndex)) {
                WorksheetEntry worksheetEntry = GDataUtils.getWorksheetFromIndex(this.getSpreadsheetEntry(), currentIndex);
                ContentWorksheet contentWorksheet = new ContentWorksheet(worksheetEntry);
                contentWorksheets.add(contentWorksheet);
            }
        }
        return contentWorksheets;
    }


    public ArrayList<String> getLogOfIncompleteRows() {
        return logOfIncompleteRows;
    }

    public Set<String> getColumnsToIgnore() {
        return columnsToIgnore;
    }


    /**
     * User: jmcmurry
     * Date: 30/05/2013
     */
    public class ContentWorksheet extends EnhancedWorksheet {

        private String[] correspondingHeaders;
        private Logger log = LoggerFactory.getLogger(getClass());
        private ArrayList<ContentRow> contentRows;
        StringBuilder worksheetXml;


        ContentWorksheet(WorksheetEntry worksheetEntry) throws ServiceException, IOException, URISyntaxException {

            super(worksheetEntry);

            // storing corresponding headers in lowercase will simplify lookup in datamodel
            correspondingHeaders = TextUtils.stringArrayToLowercase(tableOfStrings[1]);

            validateHeaders();

            contentRows = loadDataSourceRows();

        }

        public boolean validateHeaders() throws IOException, ServiceException, URISyntaxException {

            log.info(spreadsheetName + "," + getWorksheetName() + ": Validating headers...");

            boolean isValid = false;

            for (String header : correspondingHeaders) {
                if (dataModel.get(header) == null) {
                    // zero is an illegal column index in goolge spreadsheets, so when converting to a datatable array in java, column 0 is blank
                    // if it isn't column 0, we do expect there to be a correspoinding data model attribute
//                    if (Arrays.asList(correspondingHeaders).indexOf(header) != 0)
//                        log.warn("No data model attribute found for header " + header + ". Contents of this column will be ignored.");
                } else log.trace("Data model attribute found for header " + header);
            }

            for (ModelSpreadsheet.ModelWorksheet.ModelAttribute attribute : dataModel.values()) {
                String nameOfRequiredColumn = attribute.getAttributeName().toLowerCase();
                List<String> attributeNameSynonyms = attribute.getAttributeNameSynonyms();
                if (attribute.isRequired()) {
                    boolean isFound = false;
                    for (String header : correspondingHeaders) {
                        if (header != null)
                            if (header.equals(nameOfRequiredColumn) || attributeNameSynonyms.contains(header)) {
                                isFound = true;
                            }
                    }

                    if (!isFound) {
                        isValid = false;

                        log.error("Column " + nameOfRequiredColumn + " is required. Column names in this worksheet are" +
                                Arrays.toString(correspondingHeaders) + ".");

                        if (attribute.getAttributeNameSynonyms() != null && attribute.getAttributeNameSynonyms().size() > 0) {
                            String synonymString = "";
                            for (String synonym : attribute.getAttributeNameSynonyms()) {
                                synonymString += ", " + synonym;
                            }
                            log.error("Column " + nameOfRequiredColumn + " recognized synonyms include " + synonymString);
                        }

//                        System.exit(0);
                    }
                }
            }

            return isValid;
        }

        private StringBuilder parseWorksheet() {

            getLog().info("Parsing xml for worksheet: " + getWorksheetEntry().getTitle().getPlainText());

            StringBuilder sb = new StringBuilder();

            for (ContentRow contentRow : contentRows) {
                StringBuilder rowXml = contentRow.parseRow();
                sb.append(rowXml);
            }

            worksheetXml = sb;

            return sb;
        }

        private ArrayList<ContentRow> loadDataSourceRows() {

            System.out.println();
            getLog().info(spreadsheetName + "," + getWorksheetName() + ": Loading data source rows starting at " + getIndexofFirstDataRow());

            ArrayList<ContentRow> contentRows = new ArrayList<ContentRow>();

            for (int rowNumber = getIndexofFirstDataRow(); rowNumber <= getRowCount(); rowNumber++) {
                ContentRow contentRow = new ContentRow(rowNumber);
                contentRows.add(contentRow);
            }

            return contentRows;
        }

        public class ContentRow implements Serializable {

            private final int rowIndex;
            private String textForSearchTerms = "";
            private boolean rowIsIncomplete = false;

            public ContentRow(int rowIndex) {
                this.rowIndex = rowIndex;
            }

            public StringBuilder parseRow() {

                log.debug("Parsing xml for row " + rowIndex + " (" + nameOfCollection + "_" + xmlNodeNumberForRow + ")");

                StringBuilder rowXml = new StringBuilder();

                String tagName = nameOfCollection + "_" + getWorksheetName();

                rowXml.append(XmlParseUtils.openTagWithIncrement(tagName, xmlNodeNameForEachRow, rowIndex));

                CellEntry rowTitleCell = getCellEntry(rowIndex, 1);

                if (rowTitleCell == null || rowTitleCell.getPlainTextContent().equals("")) {
                    rowIsIncomplete = true;
                } else {
                    int numCols = ContentWorksheet.this.getColCount();
                    for (int colIndex = 1; colIndex < numCols; colIndex++) {
                        if (!columnsToIgnore.contains(correspondingHeaders[colIndex])) {
                            String cellXml = processToolRowAtColumn(colIndex, rowTitleCell.getPlainTextContent());
                            rowXml.append(cellXml);
                        }
                    }

                    // normalise the search terms string
                    textForSearchTerms = XmlParseUtils.cleanAmpersands(false, textForSearchTerms);
                    rowXml.append("\n<SearchTerms>" + textForSearchTerms + "</SearchTerms>");

                    // close the node.
                    rowXml.append("\n" + XmlParseUtils.closeTag(xmlNodeNameForEachRow));


                    xmlNodeNumberForRow++;

                    if (rowIsIncomplete || rowTitleCell.getPlainTextContent().equals("")) {
                        // if row is to be ignored for any reason, just log the row, and reset it to a blank before returning it
                        logOfIncompleteRows.add(rowXml.toString());
                        if (omitIncompleteRows) rowXml = new StringBuilder();
                    }
                }

                return rowXml;
            }

            public String processToolRowAtColumn(int col, String correspondingRowTitle) {

                // getLabel data model attribute using exact match on header name from data source
                ModelSpreadsheet.ModelWorksheet.ModelAttribute modelAttribute = dataModel.get(correspondingHeaders[col]);

                String cellXml = "";

                CellEntry entry = getCellEntry(rowIndex, col);

                if (entry == null && correspondingRowTitle != null) {
                    log.debug("Column: " + col + ", " + "null");
                    if (modelAttribute != null && modelAttribute.isRequired()) {
                        rowIsIncomplete = true;
                        log.warn("Column: " + col + ", " + modelAttribute.getAttributeName() + ", is required and is null at row " + rowIndex + "; this row will be logged.");
                    }
                }


                if (modelAttribute != null && entry != null) {

                    if (!modelAttribute.isIgnore()) {
                        log.debug(correspondingRowTitle + " @ column " + col + ":" + entry.getPlainTextContent());

                        if (modelAttribute.isContainsSearchTerms())
                            textForSearchTerms += entry.getPlainTextContent() + " | ";

                        cellXml = XmlParseUtils.bindCell(modelAttribute, getCellEntry(this.rowIndex, col), writeUrisInXml);

                    }
                    //
//                        else log.debug("Ignoring column " + modelAttribute.getAttributeName());
                } else if (modelAttribute == null) {
                    log.warn("Data model attribute '" + correspondingHeaders[col] + "'(column " + col + ") could not be loaded."
                            + "This column will be ignored");
                    columnsToIgnore.add(correspondingHeaders[col]);
                } else if (modelAttribute.isDefaultToNotSpecified())
                    cellXml = XmlParseUtils.bindEmpty(modelAttribute);

                return cellXml;
            }


        }

    }


}


