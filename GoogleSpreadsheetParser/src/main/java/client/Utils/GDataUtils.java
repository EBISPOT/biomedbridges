package client.Utils;

import client.SpreadsheetSources.EnhancedSpreadsheet;
import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.spreadsheet.*;
import com.google.gdata.util.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

/**
 * User: jmcmurry
 * Date: 15/05/2013
 * Time: 16:44
 */
public class GDataUtils {

    private static Logger log = LoggerFactory.getLogger(GDataUtils.class);

    private static Map<Integer, String> worksheetMapByIndex = fillMapByIndex();
    private static Map<String, Integer> worksheetMapByID = fillMapByID();
    private static final String ids = "od6,od7,od4,od5,oda,odb,od8,od9,ocy,ocz,ocw,ocx,od2,od3,od0,od1,ocq,ocr,oco,ocp,ocu,ocv,ocs,oct,oci,ocj,ocg,och,ocm,ocn,ock,ocl,oe2,oe3,oe0,oe1,oe6,oe7,oe4,oe5,odu,odv,ods,odt,ody,odz,odw,odx,odm,odn,odk,odl,odq,odr,odo,odp,ode,odf,odc,odd,odi,odj,odg,odh,obe,obf,obc,obd,obi,obj,obg,obh,ob6,ob7,ob4,ob5,oba,obb,ob8,ob9,oay,oaz,oaw,oax,ob2,ob3,ob0,ob1,oaq,oar,oao,oap,oau,oav,oas,oat,oca,ocb,oc8,oc9";


//
//    public static SSpreadsheet makeSpreadsheetToParse(GDataSelectionParams params) {
//
//        boolean isOntSource = params.isOntologySource();
//
//        try {
//            SpreadsheetService service = authenticate(params.getUsername(), params.getPassword());
//            SpreadsheetEntry spreadsheetToParse = setSpreadsheet(service, params.getCccSpreadsheetKey());
//
//            if (isOntSource && (params.getOntMapSource() == null || params.getOntMapSource().size() == 0)) {
//                log.warn("Warning, OntMapSource was not generated; it is missing or empty.");
//            } else {
//                return new SSpreadsheet(service, spreadsheetToParse, params);
//            }
//
//        } catch (IOException e) {
//            e.printStackTrace();  //todo:
//        } catch (ServiceException e) {
//            e.printStackTrace();  //todo:
//        } catch (URISyntaxException e) {
//            e.printStackTrace();  //todo:
//        }
//        return null;
//    }

    public static SpreadsheetService authenticate(String username, String password) throws ServiceException, IOException {
        //todo: rename this
        SpreadsheetService service = new SpreadsheetService("MySpreadsheetIntegration-v1");

        service.setUserCredentials(username, password);

        return service;
    }

    public static SpreadsheetEntry setSpreadsheet(SpreadsheetService service, String keyOfSpreadsheet) throws IOException, ServiceException {

        URL SPREADSHEET_FEED_URL = new URL(
                "https://spreadsheets.google.com/feeds/spreadsheets/private/full");
        // Make a request to the API and getLabel all spreadsheets.
        SpreadsheetFeed feed = service.getFeed(SPREADSHEET_FEED_URL, SpreadsheetFeed.class);
        List<SpreadsheetEntry> spreadsheets = feed.getEntries();

        if (spreadsheets.size() == 0) {
            log.error("No spreadsheets are specifically shared with this user.");
            // TODO: There were no spreadsheets, act accordingly.
        }


        // https://developers.google.com/google-apps/spreadsheets/#spreadsheets_api_urls_visibilities_and_projections
        // As of 16 May 2013, the documentation for developers is either spurious or inadequate: a 400 error is returned
        //        That is, if the following request is made to the API:
        //
        //        GET https://spreadsheets.google.com/feeds/worksheets/key/private/basic
        //        Then the cells feed URLs in each worksheet entry are of the form:
        //
        //        https://spreadsheets.google.com/feeds/cells/key/worksheetId/private/basic
        //        And the list feed URLs in each worksheet entry are of the form:
        //
        //        https://spreadsheets.google.com/feeds/list/key/worksheetId/private/basic

        for (SpreadsheetEntry spreadsheetEntry : spreadsheets) {

            String link = spreadsheetEntry.getSpreadsheetLink().getHref();
            String linkKey = link.substring(link.lastIndexOf("=") + 1);

            if (linkKey.equals(keyOfSpreadsheet)) {
                return spreadsheetEntry;
            }
        }

        log.error("Google is lame: the variable returned by spreadsheetEntry.getKey() is NOT the same as \n" +
                "the key you see in the document url (eg: what follows ccc?key=). \n" +
                "Frustratingly, the latter is accessed via spreadsheetEntry.getSpreadsheetLink().getHref().\n" +
                "spreadsheetEntry.getKey() returns a result that looks similar to the key used in a URL \n" +
                "but is arbitrarily different.");


        log.error("\n\nPlease go to https://docs.google.com/spreadsheet/ccc?key=" + keyOfSpreadsheet + " and share the spreadsheet with this program's user.\t" +
                "\n\nAvailable spreadsheets for this user are: \n");

        for (SpreadsheetEntry spreadsheetEntry : spreadsheets) {

            String title = spreadsheetEntry.getTitle().getPlainText();
            String feedURL = spreadsheetEntry.getId();
            String feedURLKey = spreadsheetEntry.getKey();
            String link = spreadsheetEntry.getSpreadsheetLink().getHref();
            String linkKey = link.substring(link.lastIndexOf("=") + 1);

            System.out.println(
                    "Title:\t\t" + title + "\n" +
                            "FeedURL:\t" + feedURL + "\n" +
                            "Feed key:\t" + feedURLKey + "\n" +
                            "Link:\t\t" + link + "\n" +
                            "Link key:\t" + linkKey + "\n");
        }

        System.exit(1);
        return null;
    }


    public static List<String> getWorksheetIdsFromIndices(ArrayList<Integer> worksheetIndices) {

        // todo: error handling

        if (worksheetIndices != null && worksheetIndices.size() > 0) {

            int length = worksheetIndices.size();

            String[] worksheetIDs = new String[length];

            for (int i = 0; i < length; i++) worksheetIDs[i] = getSingleIdFromIndex(worksheetIndices.get(i));

            return Arrays.asList(worksheetIDs);
        } else {
            log.warn("No worksheet indices specified.");
            return Arrays.asList();
        }

    }


    public static int[] getIndicesFromIds(String[] worksheetIDs) {
//        // todo: error handling

        int length = worksheetIDs.length;

        int[] worksheetIndices = new int[length];

        for (int i = 0; i < length; i++) worksheetIndices[i] = getSingleIndexFromID(worksheetIDs[i]);

        return worksheetIndices;
    }

    public static int getSingleIndexFromID(String worksheetId) {
        return worksheetMapByID.get(worksheetId);
    }

    public static String getSingleIdFromIndex(int worksheetIndex) {
        String id = worksheetMapByIndex.get(worksheetIndex);
        if (id.equals("")) log.error("Illegal worksheet index: " + worksheetIndex);
        return id;
    }

    private static Map<Integer, String> fillMapByIndex() {
        // It is reprehensible that Google's API doesn't allow you to fetch these, but Google will be Google.
        Map<Integer, String> indexMap = new HashMap<Integer, String>();
        String[] idArray = ids.split(",");
        for (int i = 0; i < idArray.length; i++) indexMap.put(i, idArray[i]);
        return indexMap;
    }

    private static Map<String, Integer> fillMapByID() {
        // It is reprehensible that Google's API doesn't allow you to fetch these, but Google will be Google.
        Map<String, Integer> indexMap = new HashMap<String, Integer>();
        String[] idArray = ids.split(",");
        for (int i = 0; i < idArray.length; i++) indexMap.put(idArray[i], i);
        return indexMap;
    }

    public static int[] stringToIntArr(String delimString) {

        String[] strArr = delimString.split(",");
        int[] intArr = new int[strArr.length];

        for (int i = 0; i < strArr.length; i++) {
            intArr[i] = Integer.parseInt(strArr[i]);
        }

        return intArr;
    }


    public static List<CellEntry> getRange(EnhancedSpreadsheet.EnhancedWorksheet enhancedWorksheet, int minRow, int maxRow, int minCol, int maxCol) throws URISyntaxException, IOException, ServiceException {

        SpreadsheetService spreadsheetService = enhancedWorksheet.getSpreadsheetService();
        WorksheetEntry worksheet = enhancedWorksheet.getWorksheetEntry();

        if (minRow == 0 || maxRow == 0 || minCol == 0 || maxCol == 0)
            log.error("Row and Col numbers in the gdata API start with 1.");

        URL rowFeedURL = new URI(worksheet.getCellFeedUrl().toString() + "?min-row=" + minRow + "&max-row=" + maxRow + "&min-col=" + minCol + "&max-col=" + maxCol).toURL();
        CellFeed rowFeed = spreadsheetService.getFeed(rowFeedURL, CellFeed.class);
        return rowFeed.getEntries();
    }

//    public static List<CellEntry> getCellEntriesFromColIndex(SWorksheet sWorksheet, int colNum) throws URISyntaxException, IOException, ServiceException {
//
//        SpreadsheetService authenticatedService = sWorksheet.getCorrespondingSSpreadsheet().getAuthenticatedService();
//        WorksheetEntry worksheet = sWorksheet.getWorksheetEntry();
//        return getCellEntriesFromColIndex(authenticatedService, worksheet, colNum);
//    }

    public static List<CellEntry> getCellEntriesFromColIndex(SpreadsheetService spreadsheetService, WorksheetEntry worksheet, int colNum) throws IOException, ServiceException, URISyntaxException {

        if (colNum == 0) log.error("Col numbers in the gdata API start with 1.");

        URL rowFeedURL = new URI(worksheet.getCellFeedUrl().toString() + "?min-col=" + colNum + "&max-col=" + colNum).toURL();
        CellFeed rowFeed = spreadsheetService.getFeed(rowFeedURL, CellFeed.class);
        return rowFeed.getEntries();
    }

    public static List<CellEntry> getCellEntriesFromRowIndex(SpreadsheetService spreadsheetService, WorksheetEntry worksheet, int rowNum) throws IOException, ServiceException, URISyntaxException {

        if (rowNum == 0) log.error("Row numbers in the gdata API start with 1.");

        URL rowFeedURL = new URI(worksheet.getCellFeedUrl().toString() + "?min-row=" + rowNum + "&max-row=" + rowNum).toURL();
        CellFeed rowFeed = spreadsheetService.getFeed(rowFeedURL, CellFeed.class);
        List<CellEntry> rowFeedEntries = rowFeed.getEntries();
        if (rowFeedEntries == null || rowFeedEntries.size() == 0)
            log.warn("Row " + rowNum + " is null in " + worksheet.getSelfLink());
        return rowFeedEntries;
    }

//    public static ArrayList<List<CellEntry>> getAllRows(SpreadsheetService spreadsheetService, WorksheetEntry worksheet) throws ServiceException, IOException, URISyntaxException {
//
//        ArrayList<List<CellEntry>> rowCollection = new ArrayList<List<CellEntry>>();
//
//        int numRows = worksheet.getRowCount();
//
//        for (int i = 1; i <= numRows; i++) {
//            List<CellEntry> row = getCellEntriesFromRowIndex(spreadsheetService, worksheet, i);
//            rowCollection.add(row);
//        }
//
//        return rowCollection;
//    }

    public static CellEntry getCellEntry(WorksheetEntry worksheet, SpreadsheetService spreadsheetService, int rowNum, int colNum) throws IOException, ServiceException, URISyntaxException {

        if (rowNum == 0 || colNum == 0) {
            log.error("0 is an illegal index; Row and Col numbers in the gdata API start with 1.");
        }

        if (rowNum > worksheet.getRowCount()) {
            log.error(rowNum + " is an illegal index; this sheet has only " + worksheet.getRowCount() + " rows.");
        }

        if (colNum > worksheet.getColCount()) {
            log.error(colNum + " is an illegal index; this sheet has only " + worksheet.getColCount() + " cols.");
        }


//        log.debug(worksheet.getTitle().getPlainText() + ": Row" + rowNum + ", Col" + colNum);
        URL rowFeedURL = new URI(worksheet.getCellFeedUrl().toString() + "?min-row=" + rowNum + "&max-row=" + rowNum + "&min-col=" + colNum + "&max-col=" + colNum).toURL();

        CellFeed rowFeed;

        try {
            rowFeed = spreadsheetService.getFeed(rowFeedURL, CellFeed.class);
            if (rowFeed.getEntries().size() != 0) return rowFeed.getEntries().get(0);
            else return null;
        } catch (IOException e) {
            throw new GDataException(e);

        } catch (ServiceException e) {
            throw new GDataException(e);
        }

    }


//    public static List<CellEntry> getCellEntries(EnhancedWorksheet sWorksheet) throws URISyntaxException, IOException, ServiceException {
//
//        SpreadsheetService authenticatedService = sWorksheet.getCorrespondingSSpreadsheet().getAuthenticatedService();
//        WorksheetEntry worksheet = sWorksheet.getWorksheetEntry();
//        int maxRow = worksheet.getRowCount();
//        int maxCol = worksheet.getColCount();
//
//        URL rowFeedURL = new URI(worksheet.getCellFeedUrl().toString() + "?max-row=" + maxRow + "&max-col=" + maxCol).toURL();
//        CellFeed rowFeed = authenticatedService.getFeed(rowFeedURL, CellFeed.class);
//        return rowFeed.getEntries();
//    }

//    public static List<CellEntry> getCellEntries(EnhancedWorksheet enhancedWorksheet) throws URISyntaxException, IOException, ServiceException {
//
//
//    }


    public static WorksheetEntry getWorksheetFromIndex(SpreadsheetEntry spreadsheetEntry, int indexOfWorksheetInSpreadsheet) throws IOException, ServiceException {
        String worksheetID = getSingleIdFromIndex(indexOfWorksheetInSpreadsheet);
        List<WorksheetEntry> worksheetEntries = spreadsheetEntry.getWorksheets();
        for (WorksheetEntry worksheetEntry : worksheetEntries) {
            String shortID = getShortID(worksheetEntry);
            if (shortID.equals(worksheetID)) return worksheetEntry;
        }

        log.error("Requested worksheet index " + indexOfWorksheetInSpreadsheet + " does not exist in " + spreadsheetEntry.getId());
        System.exit(0);
        return null;
    }

    public static String getShortID(WorksheetEntry worksheetEntry) {
        String longID = worksheetEntry.getId();
        String shortID = longID.substring(longID.lastIndexOf("/") + 1);
        return shortID;
    }

    public static int getIndex(WorksheetEntry worksheetEntry) {
        String shortId = getShortID(worksheetEntry);
        return getSingleIndexFromID(shortId);

    }

    public static String[] getStringArrayFromCells(CellEntry[] cellEntries) {
        String[] cellContents = new String[cellEntries.length + 1];
        for (int i = 0; i < cellEntries.length; i++) {

            if (cellEntries[i] != null) {
                cellContents[i] = cellEntries[i].getPlainTextContent();
            }

        }
        return cellContents;
    }

    public static String[] getStringArrayFromCells(List<CellEntry> cellEntries) {
        String[] cellContents = new String[cellEntries.size() + 1];
        for (int i = 0; i < cellEntries.size(); i++) {

            if (cellEntries.get(i) != null) {
                int colNum = cellEntries.get(i).getCell().getCol();
                cellContents[colNum] = cellEntries.get(i).getPlainTextContent();
            }

        }
        return cellContents;
    }

    public static CellEntry[][] getTableOfCellEntries(WorksheetEntry worksheetEntry, SpreadsheetService authenticatedService) throws ServiceException, IOException, URISyntaxException {

        List<CellEntry> entries = getCellEntries(worksheetEntry, authenticatedService);

        CellEntry[][] dataTable = new CellEntry[worksheetEntry.getRowCount() + 1][worksheetEntry.getColCount() + 1];

        for (CellEntry entry : entries) {
            int rowIndex = entry.getCell().getRow();
            int colIndex = entry.getCell().getCol();
            dataTable[rowIndex][colIndex] = entry;
        }

        return dataTable;
    }

    public static String[][] getTableOfStrings(WorksheetEntry worksheetEntry, SpreadsheetService authenticatedService) {

        List<CellEntry> entries = getCellEntries(worksheetEntry, authenticatedService);

        String[][] dataTable = new String[worksheetEntry.getRowCount() + 1][worksheetEntry.getColCount() + 1];

        for (CellEntry entry : entries) {
            int rowIndex = entry.getCell().getRow();
            int colIndex = entry.getCell().getCol();
            dataTable[rowIndex][colIndex] = entry.getPlainTextContent().trim();
        }

        return dataTable;
    }

    public static ArrayList<HashMap<String, String>> getWorksheetAsMap(WorksheetEntry worksheetEntry, SpreadsheetService authenticatedService) throws ServiceException, IOException, URISyntaxException {

        ArrayList<HashMap<String, String>> worksheetAsMap = new ArrayList<HashMap<String, String>>();


        String[] colTitles = getWorksheetColumnTitles(authenticatedService, worksheetEntry);

        int rowcount = worksheetEntry.getRowCount();

        for (int i = 2; i <= rowcount; i++) {

            List<CellEntry> row = getCellEntriesFromRowIndex(authenticatedService, worksheetEntry, i);

            HashMap<String, String> rowAsMap = new HashMap<String, String>();

            for (CellEntry cellEntry : row) {
                int colNum = cellEntry.getCell().getCol();
                String cellContents = cellEntry.getPlainTextContent();
                String correspondingColumnTitle = colTitles[colNum];
                rowAsMap.put(correspondingColumnTitle, cellContents);
            }

            worksheetAsMap.add(rowAsMap);
        }

        return worksheetAsMap;
    }

    private static List<CellEntry> getCellEntries(WorksheetEntry worksheetEntry, SpreadsheetService authenticatedService)  {
        int maxRow = worksheetEntry.getRowCount();
        int maxCol = worksheetEntry.getColCount();

        URL rowFeedURL = null;
        CellFeed rowFeed = null;

        try {
            rowFeedURL = new URI(worksheetEntry.getCellFeedUrl().toString() + "?max-row=" + maxRow + "&max-col=" + maxCol).toURL();
             rowFeed = authenticatedService.getFeed(rowFeedURL, CellFeed.class);
        } catch (MalformedURLException e) {
            e.printStackTrace();  //todo:
        } catch (URISyntaxException e) {
            e.printStackTrace();  //todo:
        } catch (ServiceException e) {
            e.printStackTrace();  //todo:
        } catch (IOException e) {
            e.printStackTrace();  //todo:
        }
        return rowFeed.getEntries();

    }

    public static String getCccKeyFromUrl(String fullWorksheetURL) {

        String cccKey = null;
        int startIndex = fullWorksheetURL.indexOf("ccc?key=") + 8;
        int hashIndex = fullWorksheetURL.indexOf("#");
        int ampIndex = fullWorksheetURL.indexOf("&");
        int endIndex = -1;
        endIndex = (ampIndex == -1) ? hashIndex : ampIndex;

        try {
            cccKey = fullWorksheetURL.substring(startIndex, endIndex);
        } catch (IndexOutOfBoundsException e) {
            log.error(fullWorksheetURL + ":" + startIndex + " hash index " + hashIndex + ", ampIndex" + ampIndex);
        }

        return cccKey;
    }

    public static WorksheetEntry getWorksheetFromUrl(SpreadsheetService authenticatedService, String fullWorksheetUrl) {

        if(fullWorksheetUrl==null||fullWorksheetUrl.equals("")){
            log.error("Full worksheet url must not be null or blank.");
            System.exit(0);
        }

        String cccKey = getCccKeyFromUrl(fullWorksheetUrl);
        int worksheetIndex = getWorksheetIndexFromUrl(fullWorksheetUrl);

        SpreadsheetEntry spreadsheetEntry = null;
        WorksheetEntry worksheetEntry = null;

        try {
            spreadsheetEntry = setSpreadsheet(authenticatedService, cccKey);
            worksheetEntry = getWorksheetFromIndex(spreadsheetEntry, worksheetIndex);
        } catch (IOException e) {
            e.printStackTrace();  //todo:
        } catch (ServiceException e) {
            e.printStackTrace();  //todo:
        }

        return worksheetEntry;
    }

    private static int getWorksheetIndexFromUrl(String fullWorksheetUrl) {
        int stringIndexofWorksheetIndex = fullWorksheetUrl.indexOf("id=") + 3;

        int worksheetIndex = Integer.parseInt(fullWorksheetUrl.substring(stringIndexofWorksheetIndex));

        return worksheetIndex;  //To change body of created methods use File | Settings | File Templates.
    }


    public static HashMap<String, String> getRowAsHashMap(SpreadsheetService authenticatedService, WorksheetEntry worksheetEntry, int rowIndex, String[] columnTitles) throws ServiceException, IOException, URISyntaxException {
        List<CellEntry> targetRow = getCellEntriesFromRowIndex(authenticatedService, worksheetEntry, rowIndex);
        HashMap<String, String> rowAsMap = new HashMap<String, String>();

        for (CellEntry cellEntry : targetRow) {
            int colNum = cellEntry.getCell().getCol();
            String cellContents = cellEntry.getPlainTextContent().trim();
            String correspondingColumnTitle = columnTitles[colNum];
            rowAsMap.put(correspondingColumnTitle, cellContents);
        }

        return rowAsMap;
    }

    public static String[] getWorksheetColumnTitles(SpreadsheetService authenticatedService, WorksheetEntry worksheetEntry) throws ServiceException, IOException, URISyntaxException {
        String[] colTitles = new String[worksheetEntry.getColCount() + 1];

        for (CellEntry entry : getCellEntriesFromRowIndex(authenticatedService, worksheetEntry, 1)) {
            int colIndex = entry.getCell().getCol();
            colTitles[colIndex] = entry.getPlainTextContent().trim();
        }

        return colTitles;
    }
}
