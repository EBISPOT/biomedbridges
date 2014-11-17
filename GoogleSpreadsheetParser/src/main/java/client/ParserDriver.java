package client;

import client.Utils.GDataUtils;
import client.Utils.XmlParseUtils;
import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.util.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URISyntaxException;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: jmcmurry
 * Date: 13/11/2014
 * Time: 15:31
 */
public class ParserDriver {

    private static final Logger log = LoggerFactory.getLogger(ParserDriver.class);
    private static SpreadsheetService authenticatedService;


    private OptionsParser optionsParser;
    private final String programName = "parser";
    private static String outfileBasePath;

    private SourcesManifest sourcesManifest;

    public static void main(String[] args) {

        ParserDriver parserDriver = new ParserDriver();
        parserDriver.initialise(args);
        parserDriver.run();
    }

    private void run() {

        sourcesManifest.loadSources();
        StringBuilder xml = sourcesManifest.parseSpreadsheets();
        writeXmlToFile(xml, outfileBasePath + "src/main/Outputs/tools.xml");

        logToFile(sourcesManifest.getLogOfIgnoredRows(), outfileBasePath + "src/main/Outputs/IgnoredRows.txt");
        logToFile(sourcesManifest.getLogOfIgnoredCols(), outfileBasePath + "src/main/Outputs/IgnoredColumns.txt");
        logToFile(XmlParseUtils.getTermRequests(), outfileBasePath + "src/main/Outputs/TermRequests.txt");

        logAutocompleteTerms(XmlParseUtils.getSearchTermsCache());

        getLog().info("Parsing completed successfully.");
    }

    private static void logToFile(ArrayList<ArrayList<String>> logOfItems, String outFilePath) {

        StringBuilder lines = new StringBuilder();

        for (ArrayList<String> spreadsheetItems : logOfItems) {

            lines.append("\n");

            for (String item : spreadsheetItems) {
                lines.append("\n" + item);
            }
        }

        PrintWriter out = null;
        try {
            out = new PrintWriter(new BufferedWriter(new FileWriter(outFilePath, false)));
        } catch (IOException e) {
            e.printStackTrace();  //todo:
        }

        out.println(lines);
        out.close();
    }

    private static void logToFile(Map<String, Set<String>> logOfItems, String outFilePath) {
        StringBuilder lines = new StringBuilder();

        for (Map.Entry<String, Set<String>> spreadsheetItems : logOfItems.entrySet()) {
            lines.append("\n\n" + spreadsheetItems.getKey().toUpperCase());
            lines.append("\n------------------");

            for (String item : spreadsheetItems.getValue()) {
                lines.append("\n" + item);
            }
        }

        PrintWriter out = null;
        try {
            out = new PrintWriter(new BufferedWriter(new FileWriter(outFilePath, false)));
        } catch (IOException e) {
            e.printStackTrace();  //todo:
        }

        out.println(lines);
        out.close();
    }

    private static void logAutocompleteTerms(Set<String> searchTermsCache) {

        StringBuilder searchTermsLog = new StringBuilder();

        searchTermsLog.append("<Terms>");
        for (String line : searchTermsCache) {
            searchTermsLog.append("\n" + line);
        }
        searchTermsLog.append("\n</Terms>");


        try {
            File file = new File(outfileBasePath + "src/main/Outputs/AutocompleteTerms.xml");
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.append(searchTermsLog);
            writer.flush();
        } catch (IOException e) {
            log.error("Error writing term requests to file.");
            e.printStackTrace();  //todo:
        }
    }

    public static void writeXmlToFile(StringBuilder xml, String filePath) {

        PrintWriter out = null;
        try {
            out = new PrintWriter(new BufferedWriter(new FileWriter(filePath, false)));
        } catch (IOException e) {
            e.printStackTrace();  //todo:
        }

//        System.out.println(xml);

        out.println(xml);
        out.close();
    }


    private void initialise(String[] args) {
        String propertiesFilePath = getAppResourcesPath(args, "-1");
        if (propertiesFilePath == null || propertiesFilePath.equals(""))
            propertiesFilePath = "parser_defaults.properties";

        // instantiate a new parser using the program arguments, the properties file and the name of the program
        this.optionsParser = new OptionsParser(args, programName, propertiesFilePath);

        // if errors were encountered
        if (optionsParser.getStatusCode() != 0 && optionsParser.getStatusCode() != 1) {
            getLog().error("Parser could not be created with the supplied information. " + Arrays.asList(args));
            System.exit(1);
        }

        // if these loaded successfully, get each of the options and assign them to variables.
        else {

            bindOptions(optionsParser);

            // finalise the parser so that the args can be rechecked and the help can be printed if needed
            optionsParser.finalise("", args);

            // if the user just wanted to print the help, discontinue the program.
            if (optionsParser.getStatusCode() == 1) {
                getLog().info("Exiting Parser.");
                System.exit(0);
            } else if (optionsParser.getStatusCode() != 0) {
                getLog().error("Errors were encountered when loading args and defaults." + optionsParser.getStatusCode());
                throw new IllegalArgumentException("Errors were encountered when loading args and defaults.");
            }
        }
    }


    public ParserDriver() {

    }

    private void bindOptions(OptionsParser optionsParser) {

        String username = optionsParser.processStringOption("username", true, true, "u", "Google username");
        String password = optionsParser.processStringOption("password", true, true, "p", "Google password");

        if (!optionsParser.printTheHelp()) setAuthenticatedService(username, password);

        String cccKeyForParserConfiguration = optionsParser.processStringOption("cccKeyForParserConfiguration", false, true, "c", "key for Google Spreadsheet with configuration detail");

        int configIndexForModel = optionsParser.processIntOption("configIndexForModel", false, false, "m", "Within the parser configuration spreadsheet, index of the tab containing the location of the DATA MODEL source spreadsheet.");
        int configIndexForDataSource = optionsParser.processIntOption("configIndexForDataSource", false, false, "d", "Within the parser configuration spreadsheet, index of the tab containing the location etc. of the DATA source spreadsheet.");

        boolean addSynonyms = optionsParser.processBooleanOption("addSynonyms", false, false, "y", "Look up semantic tags by synonyms as well as ontology labels");
        boolean writeUrisInXml = optionsParser.processBooleanOption("writeUrisInXml", false, false, "x", "Write to xml the corresponding uris for ontology terms. If false, only the labels will be added.");
        String rootNodeName = optionsParser.processStringOption("rootNodeName", false, false, "r", "Root node name for xml output.");
        boolean omitIncompleteRows = optionsParser.processBooleanOption("omitIncompleteRows", false, false, "o", "Omit from xml output those rows which do not contain a value at required columns.");

        outfileBasePath = optionsParser.processStringOption("outfileBasePath", false, false, "b", "Directory path where output should be written.");
        int startNumberForRowNodes = optionsParser.processIntOption("startNumberForRowNodes", false, false, "i", "Start number for ids of xml nodes.");


        // bundle the configuration into a single object
        if (optionsParser.getStatusCode() == 0)
            try {
                sourcesManifest = new SourcesManifest(authenticatedService, cccKeyForParserConfiguration, configIndexForModel, configIndexForDataSource, writeUrisInXml, addSynonyms, startNumberForRowNodes, omitIncompleteRows, rootNodeName, outfileBasePath);
            } catch (IOException | ServiceException | URISyntaxException e) {
                getLog().error("Failed to configure spreadsheet sources.");
                e.printStackTrace();
            }
    }

    public void setAuthenticatedService(String username, String password) {
        if (username == null || password == null) {
            String message = "Authenticate using commandline arguments google username (-u) and google password (-p)";
            getLog().error(message);
            System.exit(1);
        } else try {
            authenticatedService = GDataUtils.authenticate(username, password);
            if (authenticatedService == null) {
                getLog().error("Authentication failed using username '" + username + "' and password '" + password + "'.");
                System.exit(1);
            }

        } catch (ServiceException | IOException e) {
            getLog().error("Authentication failed using username '" + username + "' and password '" + password + "'.");
            System.exit(1);
        }
    }

    public Logger getLog() {
        return log;
    }

    /**
     * @param args   commandline args passed when running the program
     * @param prefix the prefix used
     * @return
     */
    private String getAppResourcesPath(String[] args, String prefix) {

        String appResourcesPath = "";

        for (int i = 0; i < args.length; i++) {
            if (args[i].equals(prefix)) {
                if (args.length > i) {
                    appResourcesPath = args[i + 1];
                    if (!appResourcesPath.isEmpty() && !appResourcesPath.endsWith("/")) {
                        appResourcesPath += "/";
                    }

                }
            }
        }

        return appResourcesPath;
    }

}
