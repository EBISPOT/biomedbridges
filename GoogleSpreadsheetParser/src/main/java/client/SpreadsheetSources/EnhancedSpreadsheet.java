package client.SpreadsheetSources;

import client.Utils.GDataUtils;
import client.Utils.TextUtils;
import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.spreadsheet.CellEntry;
import com.google.gdata.data.spreadsheet.SpreadsheetEntry;
import com.google.gdata.data.spreadsheet.WorksheetEntry;
import com.google.gdata.util.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;

/**
 * User: jmcmurry
 * Date: 16/05/2013
 */
public abstract class EnhancedSpreadsheet {

    protected Logger log = LoggerFactory.getLogger(getClass());

    protected static SpreadsheetService authenticatedService;
    protected String spreadsheetName;
    protected String cccKey;
    protected int indexOfFirstDataRow;
    protected ArrayList<Integer> indicesOfTabsToIgnore = new ArrayList<Integer>();
    protected ArrayList<Integer> indicesOfTabsToParse;
    protected int indexOfTabInWhichToLogIssues = -1;
    protected boolean overwriteValues;
    protected String nameOfCollection;
    protected SpreadsheetEntry spreadsheet;

    protected HashMap<String, HashMap<String, String>> labelURIMapCollection = new HashMap<String, HashMap<String, String>>();
    protected HashMap<String, HashMap<String, String>> synonymLabelMapCollection = new HashMap<String, HashMap<String, String>>();


    public EnhancedSpreadsheet(SpreadsheetService authenticatedService, HashMap<String, String> configRow)
            throws IOException, ServiceException, URISyntaxException {


        this.authenticatedService = authenticatedService;
        this.spreadsheetName = configRow.get("spreadsheetName");
        this.cccKey = configRow.get("cccKey");
        this.indexOfFirstDataRow = Integer.parseInt(configRow.get("indexOfFirstDataRow"));
        this.indicesOfTabsToParse = parseCommaDelimitedInts(configRow.get("indicesOfTabsToParse"));
        if (indicesOfTabsToParse == null)
            getLog().error("Sources manifest must have a column titled indicesOfTabsToParse");
        this.indicesOfTabsToIgnore = parseCommaDelimitedInts(configRow.get("indicesOfTabsToIgnore"));

        boolean logIssues = configRow.get("indexOfTabInWhichToLogIssues") != null;

        if (logIssues)
            this.indexOfTabInWhichToLogIssues = Integer.parseInt(configRow.get("indexOfTabInWhichToLogIssues"));
        this.overwriteValues = TextUtils.isTrue(configRow.get("overwriteValues"));
        this.nameOfCollection = configRow.get("nameOfCollection");
        this.spreadsheet = GDataUtils.setSpreadsheet(authenticatedService, cccKey);

        System.out.println();
    }


    public SpreadsheetEntry getSpreadsheetEntry() {
        return spreadsheet;
    }

    public SpreadsheetService getAuthenticatedService() {
        return authenticatedService;
    }


    public void setLabelURIMapCollection(HashMap<String, HashMap<String, String>> ontMapCollection) {
        this.labelURIMapCollection = ontMapCollection;
    }

    public HashMap<String, HashMap<String, String>> getLabelURIMapCollection() {
        return labelURIMapCollection;
    }

    public void setSynonymLabelMapCollection(HashMap<String, HashMap<String, String>> synonymLabelMapCollection) {
        this.synonymLabelMapCollection = synonymLabelMapCollection;
    }

    public HashMap<String, HashMap<String, String>> getSynonymLabelMapCollection() {
        return synonymLabelMapCollection;
    }

    public SpreadsheetEntry getSpreadsheet() {
        return spreadsheet;
    }


    public String getTitle() {
        return spreadsheet.getTitle().getPlainText();
    }

    private ArrayList<Integer> parseCommaDelimitedInts(String value) {

        ArrayList<Integer> integers = null;

        if (value != null) {

            if (value.contains("-")) integers = parseRangeInts(value);
            else if (value.contains("all")) integers = parseAllAvailableIndices();
            else if (value.contains("none")) integers = new ArrayList<Integer>();

            else {
                integers = new ArrayList<Integer>();
                String[] intsAsStrings = value.split(",");
                for (String str : intsAsStrings) {
                    integers.add(Integer.parseInt(str.trim()));
                }
            }
        }

        return integers;
    }

    private ArrayList<Integer> parseAllAvailableIndices() {
        ArrayList<Integer> allIndices = new ArrayList<Integer>();

        try {
            SpreadsheetEntry spreadsheetEntry = GDataUtils.setSpreadsheet(authenticatedService, cccKey);
            for (WorksheetEntry worksheetEntry : spreadsheetEntry.getWorksheets()) {

                int index = GDataUtils.getIndex(worksheetEntry);

                if (!indicesOfTabsToParse.contains(index) && !indicesOfTabsToIgnore.contains(index))
                    allIndices.add(index);
            }
        } catch (IOException e) {
            e.printStackTrace();  //todo:
        } catch (ServiceException e) {
            e.printStackTrace();  //todo:
        }

        return allIndices;
    }

    private ArrayList<Integer> parseRangeInts(String value) {
//        todo
        return null;  //To change body of created methods use File | Settings | File Templates.
    }

    public boolean validateSpreadsheetOfSources(List<CellEntry> row) {
        //todo
        return true;
    }

    public String toString() {

        String outString = "";

        outString += "\nName: " + spreadsheetName;
        outString += "\nKey: " + cccKey;
        outString += "\nIndex of first data row: " + indexOfFirstDataRow;
        outString += "\nIndices to ignore: " + indicesOfTabsToIgnore;
        outString += "\nIndices of tabs to parse: " + indicesOfTabsToParse;
        outString += "\nIndex for issues: " + indexOfTabInWhichToLogIssues;
        outString += "\nOverwrite values: " + overwriteValues;

        return outString;

    }

    public int getIndexOfFirstDataRow() {
        return indexOfFirstDataRow;
    }


    public Logger getLog() {
        return log;
    }

    /**
     * User: jmcmurry
     * Date: 17/05/2013
     * Time: 11:11
     */
    public class EnhancedWorksheet {

        private Logger log = LoggerFactory.getLogger(getClass());
        String[][] tableOfStrings;
        CellEntry[][] tableOfCellEntries;

        //    int firstHeaderRowIndex;
        //    protected int firstDataRowIndex;

        protected String[] correspondingHeaders;
        private WorksheetEntry worksheetEntry;

        private String worksheetName;
        private String id;
        protected int indexofFirstDataRow;

        EnhancedWorksheet
                (WorksheetEntry worksheetEntry)
                throws ServiceException, IOException, URISyntaxException {

            this.worksheetEntry = worksheetEntry;
            this.worksheetName = worksheetEntry.getTitle().getPlainText();
            //        this.firstHeaderRowIndex = firstHeaderRowIndex;
            this.indexofFirstDataRow = EnhancedSpreadsheet.this.getIndexOfFirstDataRow();
            this.tableOfCellEntries = GDataUtils.getTableOfCellEntries(worksheetEntry, authenticatedService);
            this.tableOfStrings = GDataUtils.getTableOfStrings(worksheetEntry, authenticatedService);

            this.correspondingHeaders = tableOfStrings[1];

        }

        EnhancedWorksheet
                (WorksheetEntry worksheetEntry, int indexofFirstDataRow)
                throws ServiceException, IOException, URISyntaxException {

            this.worksheetEntry = worksheetEntry;
            this.worksheetName = worksheetEntry.getTitle().getPlainText();
            //        this.firstHeaderRowIndex = firstHeaderRowIndex;
            this.indexofFirstDataRow = indexofFirstDataRow;
            this.tableOfCellEntries = GDataUtils.getTableOfCellEntries(worksheetEntry, authenticatedService);
            this.tableOfStrings = GDataUtils.getTableOfStrings(worksheetEntry, authenticatedService);

            this.correspondingHeaders = tableOfStrings[1];

        }

        public CellEntry getCellEntry(int row, int col) {
            return tableOfCellEntries[row][col];
        }

        public String getPlainTextContent(int row, int col) {
            return tableOfStrings[row][col];
        }

        public WorksheetEntry getWorksheetEntry() {
            return worksheetEntry;
        }

        public String getWorksheetName() {
            return worksheetName;
        }

        public List<CellEntry> getRow(int rowIndex) {
            getLog().debug("Getting row " + rowIndex + " from " + getTitle());
            int numRows = worksheetEntry.getRowCount();
            if (rowIndex == 0 || rowIndex > numRows)
                throw new IllegalArgumentException("You requested row #" + rowIndex + "; Row index must be between 1 and last row (" + numRows + ").");
            return Arrays.asList(tableOfCellEntries[rowIndex]);
        }

        public CellEntry[] getCol(int colIndex) {

            int numCols = worksheetEntry.getColCount();
            if (colIndex == 0 || colIndex > numCols)
                throw new IllegalArgumentException("Row index must be between 1 and last col (" + numCols + ").");

            int numRows = worksheetEntry.getRowCount();

            CellEntry[] requestedCol = new CellEntry[numRows + 1];

            for (int row = 1; row < numRows + 1; row++) {
                requestedCol[row] = tableOfCellEntries[row][colIndex];
            }

            return requestedCol;
        }

        public List<CellEntry> getRowTitleCells() {

            return Arrays.asList(getCol(1));

        }


        public CellEntry[][] getSubRange(int minRow, int maxRow, int minCol, int maxCol) {

            int numCols = maxCol - minCol;
            int numRows = maxRow - minRow;

            CellEntry[][] selectedRange = new CellEntry[numRows][numCols];

            int selectedRow = minRow;
            int selectedCol = minCol;

            for (int i = 0; i < numRows; i++) {
                for (int j = 0; j < numCols; j++) {
                    selectedRange[i][j] = tableOfCellEntries[selectedRow][selectedCol];
                    selectedCol++;
                }
                selectedRow++;
            }

            return selectedRange;
        }


        public SpreadsheetService getSpreadsheetService() {
            return authenticatedService;
        }

        public int getRowCount() {
            return worksheetEntry.getRowCount();
        }

        public int getColCount() {
            return worksheetEntry.getColCount();
        }


        public String getId() {
            return worksheetEntry.getId();
        }

        public String getTitle() {
            return worksheetEntry.getTitle().getPlainText();
        }

        public int getIndexofFirstDataRow() {
            return indexofFirstDataRow;
        }

//        public String[] getCorrespondingHeaders() {
//            return correspondingHeaders;
//        }
    }
}
