package client;

import client.SpreadsheetSources.ContentSpreadsheet;
import client.SpreadsheetSources.ModelSpreadsheet;
import client.Utils.GDataUtils;
import client.Utils.XmlParseUtils;
import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.spreadsheet.SpreadsheetEntry;
import com.google.gdata.data.spreadsheet.WorksheetEntry;
import com.google.gdata.util.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: jmcmurry
 * Date: 12/02/2014
 * Time: 17:45
 */
public class SourcesManifest {

    private static final Logger log = LoggerFactory.getLogger(ParserDriver.class);
    private final String rootNodeName;
    private final boolean omitIncompleteRows;
    private final boolean addSynonyms;
    private final boolean writeUrisInXml;
    private final int configIndexForModel;
    private final int configIndexForDataSource;

    private SpreadsheetService authenticatedService;

    private SpreadsheetEntry configSpreadsheet;
    private HashMap<String, ModelSpreadsheet.ModelWorksheet.ModelAttribute> dataModel;
    private ArrayList<ContentSpreadsheet> contentSpreadsheets;
    private int currentNumberForRowNodes = 1;

    private String cccKeyForParserConfiguration;
    private String outFilePath;
    private ArrayList<ArrayList<String>> logOfIgnoredRows = new ArrayList<ArrayList<String>>();
    private Map<String, Set<String>> logOfIgnoredCols = new HashMap<String, Set<String>>();

    public SourcesManifest(SpreadsheetService authenticatedService, String cccKeyForParserConfiguration, int configIndexForModel, int configIndexForDataSource, boolean writeUrisInXml, boolean addSynonyms, int startNumberForRowNodes, boolean omitIncompleteRows, String rootNodeName, String outFilePath) throws IOException, ServiceException, URISyntaxException {

        System.out.println();
        this.cccKeyForParserConfiguration = cccKeyForParserConfiguration;
        this.authenticatedService = authenticatedService;
        this.addSynonyms = addSynonyms;
        this.writeUrisInXml = writeUrisInXml;
        this.configIndexForModel = configIndexForModel;
        this.configIndexForDataSource = configIndexForDataSource;
        this.rootNodeName = rootNodeName;
        this.omitIncompleteRows = omitIncompleteRows;
        this.outFilePath = outFilePath;
    }

    public void loadSources() {
        try {
            this.configSpreadsheet = GDataUtils.setSpreadsheet(authenticatedService, cccKeyForParserConfiguration);
            this.dataModel = loadDataModelSource(false, addSynonyms, configIndexForModel, "");
        } catch (IOException e) {
            e.printStackTrace();  //todo:
        } catch (ServiceException e) {
            e.printStackTrace();  //todo:
        } catch (URISyntaxException e) {
            e.printStackTrace();  //todo:
        }
        this.contentSpreadsheets = loadContentSpreadsheets(configIndexForDataSource, omitIncompleteRows, writeUrisInXml);
    }

    private ArrayList<ContentSpreadsheet> loadContentSpreadsheets(int configIndexForDataSource, boolean omitIncompleteRows, boolean writeUrisInXml) {

        getLog().info("Configuring DATA CONTENT Sources From Manifest...");

        ArrayList<ContentSpreadsheet> datasources = new ArrayList<ContentSpreadsheet>();

        try {
            WorksheetEntry dataSourceManifestWorksheet = GDataUtils.getWorksheetFromIndex(configSpreadsheet, configIndexForDataSource);
            String[] headers = GDataUtils.getWorksheetColumnTitles(authenticatedService, dataSourceManifestWorksheet);

            for (int rowNum = 1; rowNum < dataSourceManifestWorksheet.getRowCount(); rowNum++) {
                HashMap<String, String> configRow = GDataUtils.getRowAsHashMap(authenticatedService, dataSourceManifestWorksheet, rowNum, headers);

                if (configRow.get("Include") != null && configRow.get("Include").equals("TRUE")) {
                    ContentSpreadsheet contentSpreadsheet = new ContentSpreadsheet(authenticatedService, configRow, dataModel, currentNumberForRowNodes, omitIncompleteRows, writeUrisInXml);
                    datasources.add(contentSpreadsheet);

                    logOfIgnoredRows.add(contentSpreadsheet.getLogOfIncompleteRows());
                    logOfIgnoredCols.put(contentSpreadsheet.getTitle(), contentSpreadsheet.getColumnsToIgnore());
                }
            }

        } catch (IOException e) {
            e.printStackTrace();  //todo:
        } catch (ServiceException e) {
            e.printStackTrace();  //todo:
        } catch (URISyntaxException e) {
            e.printStackTrace();  //todo:
        }

        return datasources;
    }                                                                              //loadLocalOntology, addSynonyms, configIndexForModel, pathToSerialisedTermUriMap, pathToSerialisedSynonymLabelMap

    private HashMap<String, ModelSpreadsheet.ModelWorksheet.ModelAttribute> loadDataModelSource(boolean loadLocalDataModel, boolean addSynonyms, int configIndexForModel, String pathToSerialisedOntologies) throws IOException, ServiceException, URISyntaxException {

        getLog().info("Configuring DATA MODEL Source From Manifest...");

        WorksheetEntry dataModelManifestWorksheet = GDataUtils.getWorksheetFromIndex(configSpreadsheet, configIndexForModel);
        String[] headers = GDataUtils.getWorksheetColumnTitles(authenticatedService, dataModelManifestWorksheet);
        HashMap<String, String> dataModelConfigRow = GDataUtils.getRowAsHashMap(authenticatedService, dataModelManifestWorksheet, 3, headers);
        ModelSpreadsheet modelSpreadsheet = new ModelSpreadsheet(loadLocalDataModel, addSynonyms, pathToSerialisedOntologies, authenticatedService, dataModelConfigRow);
        HashMap<String, ModelSpreadsheet.ModelWorksheet.ModelAttribute> dataModel = modelSpreadsheet.getDataModel();

        if (dataModel == null) {
            getLog().error("Data model must not be null.");
        }

        return dataModel;
    }


    public HashMap<String, ModelSpreadsheet.ModelWorksheet.ModelAttribute> getDataModel() {
        return dataModel;
    }

    public ArrayList<ContentSpreadsheet> getContentSpreadsheets() {
        return contentSpreadsheets;
    }

    private static Logger getLog() {
        return log;
    }

    public String getCccKeyForParserConfiguration() {
        return cccKeyForParserConfiguration;
    }


    public ArrayList<ArrayList<String>> getLogOfIgnoredRows() {
        return logOfIgnoredRows;
    }


    public StringBuilder parseSpreadsheets() {

        StringBuilder xml = XmlParseUtils.initalizeXml(rootNodeName);

        for (ContentSpreadsheet contentSpreadsheet : contentSpreadsheets) {
            xml.append(contentSpreadsheet.parseSpreadsheet());
        }

        xml.append(XmlParseUtils.finaliseXml(rootNodeName));

        return xml;

    }


    public String getRootNodeName() {
        return rootNodeName;
    }

    public String getOutFilePath() {
        return outFilePath;
    }

    public Map<String, Set<String>> getLogOfIgnoredCols() {
        return logOfIgnoredCols;
    }
}
