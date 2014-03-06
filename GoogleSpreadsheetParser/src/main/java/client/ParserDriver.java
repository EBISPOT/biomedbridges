package client;

import client.Utils.GDataUtils;
import client.Utils.TextUtils;
import client.Utils.XmlParseUtils;
import com.google.gdata.client.spreadsheet.SpreadsheetService;
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;

/**
 * A command line client for the GoogleSpreadsheetParser that takes a list of properties (values, constrained by an optional type) and
 * produces a list of ontology mappings for those properties.
 *
 * @author Julie McMurry adapted from Tony Burdett
 * @date 7 October 2013
 */
public class ParserDriver {

    private static final Logger log = LoggerFactory.getLogger(ParserDriver.class);
    private static SpreadsheetService authenticatedService;

    private static ArrayList<ParserDriverVariables> vars = new ArrayList<ParserDriverVariables>();
    private static ArrayList<String> usedPrefixes = new ArrayList<String>();
    private static String outfileBasePath;

    public static void main(String[] args) {

        SourcesManifest manifest = initialise(args);

        StringBuilder xml = manifest.parseSpreadsheets();
        writeXmlToFile(xml, outfileBasePath + "RegistryContent.xml");

        logToFile(manifest.getLogOfIgnoredRows(), outfileBasePath + "IgnoredRows.txt");
        logToFile(manifest.getLogOfIgnoredCols(), outfileBasePath + "IgnoredColumns.txt");
        logToFile(XmlParseUtils.getTermRequests(), outfileBasePath + "TermRequests.txt");

        logAutocompleteTerms(XmlParseUtils.getSearchTermsCache());

        getLog().info("Parsing completed successfully.");
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

//    private static void logTermRequests(Map<String, Set<String>> termRequests) {
//        //To change body of created methods use File | Settings | File Templates.
//    }

//    private static void logSkippedRowsToFile(HashMap<String, ArrayList<String>> logOfIgnoredRows) {
//
//        StringBuilder rowsSkippedInAllSpreadsheets = new StringBuilder();
//
//        for (ArrayList<String> entry : logOfIgnoredRows.values()) {
//
//            ArrayList<String> rowsSkippedInSpreadsheet = entry;
//
//            for (String rowXml : rowsSkippedInSpreadsheet) {
//
//                rowsSkippedInAllSpreadsheets.append("<?xml version=\"1.0\" ?>\n<SkippedRows>");
//                rowsSkippedInAllSpreadsheets.append(rowXml);
//                rowsSkippedInAllSpreadsheets.append("</SkippedRows>");
//            }
//        }
//
//        PrintWriter out = null;
//        try {
//            out = new PrintWriter(new BufferedWriter(new FileWriter(outfileBasePath + "SkippedRows.txt", false)));
//        } catch (IOException e) {
//            e.printStackTrace();  //todo:
//        }
//
//        out.println(rowsSkippedInAllSpreadsheets);
//        out.close();
//    }

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

    private static void logAutocompleteTerms(Set<String> searchTermsCache) {

        StringBuilder searchTermsLog = new StringBuilder();

        searchTermsLog.append("<Terms>");
        for (String line : searchTermsCache) {
            searchTermsLog.append("\n" + line);
        }
        searchTermsLog.append("\n</Terms>");


        try {
            File file = new File(outfileBasePath + "AutocompleteTerms.xml");
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.append(searchTermsLog);
            writer.flush();
        } catch (IOException e) {
            log.error("Error writing term requests to file.");
            e.printStackTrace();  //todo:
        }
    }

    private static SourcesManifest initialise(String[] args) {

        getLog().info("Loading configurations ... ");

        SourcesManifest sourcesManifest = null;

        addVar("username", "String", false, "u", "Google Username");
        addVar("password", "String", false, "w", "Google Password");

        addVar("cccKeyForParserConfiguration", "String", false, "c", "key for Google Spreadsheet with configuration detail");
        addVar("configIndexForModel", "Integer", false, "m", "Within the parser configuration spreadsheet, index of the tab containing the location of the DATA MODEL source spreadsheet.");
        addVar("configIndexForDataSource", "Integer", false, "d", "Within the parser configuration spreadsheet, index of the tab containing the location etc. of the DATA source spreadsheet.");

        addVar("loadLocalOntology", "boolean", false, "l", "Load ontology from local serialised source instead of spreadsheet");
        addVar("addSynonyms", "boolean", false, "y", "Look up semantic tags by synonyms as well as ontology labels");
        addVar("writeUrisInXml", "boolean", false, "x", "Write to xml the corresponding uris for ontology terms. If false, only the labels will be added.");
        addVar("pathToSerializedOntologies", "String", false, "t", "Full validated path to serialised ontology source; used in both serialising and deserialising.");
        addVar("rootNodeName", "String", false, "r", "Root node name for xml output.");
        addVar("omitIncompleteRows", "boolean", false, "o", "Omit from xml output those rows which do not contain a value at required columns.");

        addVar("outfileBasePath", "String", false, "s", "Directory path where output should be written.");
        addVar("startNumberForRowNodes", "Integer", false, "i", "Start number for ids of xml nodes.");
        addVar("acceptableDelims", "String", false, "a", "Acceptable delimiters for multiple values in a field. Separate with spaces.");

        try {
            int statusCode = parseArguments(args);
            if (statusCode == 0) {
                String username = getVarValue("username");
                String password = getVarValue("password");
                getLog().info("Authenticating ... ");
                authenticatedService = GDataUtils.authenticate(username, password);
                if (authenticatedService == null) {

                    getLog().info("Authentication failed using username and password above.");
                    System.exit(0 );
                }

                String cccKeyForParserConfiguration = getVarValue("cccKeyForParserConfiguration");
                int configIndexForModel = getVarInt("configIndexForModel");
                int configIndexForDataSource = getVarInt("configIndexForDataSource");

                boolean loadLocalOntology = TextUtils.isTrue(getVarValue("loadLocalOntology"));
                boolean addSynonyms = TextUtils.isTrue(getVarValue("addSynonyms"));
                boolean writeUrisInXml = TextUtils.isTrue(getVarValue("writeUrisInXml"));
                String pathToSerializedOntologies = getVarValue("pathToSerializedOntologies");

                outfileBasePath = getVarValue("outfileBasePath");

                int startNumberForRowNodes = getVarInt("startNumberForRowNodes");
                String rootNodeName = getVarValue("rootNodeName");
                boolean omitIncompleteRows = TextUtils.isTrue(getVarValue("omitIncompleteRows"));
                String[] acceptableDelims = getVarValue("acceptableDelims").split(" ");

                sourcesManifest = new SourcesManifest(
                        authenticatedService,

                        cccKeyForParserConfiguration,
                        configIndexForModel,
                        configIndexForDataSource,

                        loadLocalOntology,
                        pathToSerializedOntologies,
                        writeUrisInXml,
                        addSynonyms,

                        startNumberForRowNodes,
                        omitIncompleteRows,
                        rootNodeName,
                        acceptableDelims,
                        outfileBasePath
                );


            } else {
                System.exit(statusCode);
            }
        } catch (IOException e)

        {
            System.err.println("A read/write problem occurred: " + e.getMessage());
            System.exit(1);
        } catch (Exception e)

        {
            System.err.println("Spreadsheet parser did not complete successfully: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }

        return sourcesManifest;
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

    private static String getVarValue(String varName) {

        try {
            for (ParserDriverVariables var : vars) {
                if (var.varName.equals(varName)) return var.value;
            }
        } catch (Exception e) {
            getLog().error("Unable to load a value for variable " + varName);
        }
        System.exit(0);
        return null;
    }

    private static int getVarInt(String varName) {

        String strValue = getVarValue(varName);
        int intValue = -1;

        for (ParserDriverVariables var : vars) {
            if (var.varName.equals(varName)) strValue = var.value;
            if (strValue == null) {
                getLog().error("Value for " + varName + " is null.");
                System.exit(0);
            }
        }
        if (strValue.equals("initial placeholder value")) {
            getLog().error("Unable to load a value for variable " + varName);
            System.exit(0);
        } else try {
            intValue = Integer.parseInt(strValue);
        } catch (NumberFormatException e) {
            getLog().error("Number could not be parsed for '" + strValue + "'");
            System.exit(0);
        }

        return intValue;
    }

    private static void addVar(String varName, String varType, boolean varRequired, String optionPrefix, String message) {
        vars.add(new ParserDriverVariables(varName, varType, varRequired, optionPrefix, message));
    }

    //parse commandline arguments
    private static int parseArguments(String[] args) throws IOException {

        CommandLineParser parser = new GnuParser();
        HelpFormatter help = new HelpFormatter();
        Options options = bindOptions();

        int statusCode = 0;
        try {
            Properties defaults = new Properties();
            InputStream in = ParserDriver.class.getClassLoader().getResourceAsStream("parser-defaults.properties");
            if (in == null) {
                throw new IOException("Failed to read default options from config/parser-defaults.properties");
            }
            defaults.load(in);

            CommandLine commandlineParser = parser.parse(options, args, true);

            // check for mode help option
            if (commandlineParser.hasOption("")) {
                // print out mode help
                help.printHelp("GoogleSpreadsheetParser", options, true);
                statusCode += 1;
            } else {

                System.out.println("Your parser will be run with the following supplied options...");

                // print the supplied options
                for (Option opt : commandlineParser.getOptions()) {
                    System.out.println("\t" + opt.getLongOpt() +
                            (opt.hasArg() ? ": " + opt.getValue() : "") +
                            " (" + opt.getArgName() + ")");
                }

                // now that we have the user-supplied options, assign them to the class vars
                assignInputsToVariables(defaults, commandlineParser);
            }
        } catch (ParseException e) {
            System.out.println("Failed to read supplied arguments (" + e.getMessage() + ")");
            help.printHelp("GoogleSpreadsheetParser", options, true);
            statusCode += 4;
        } catch (FileNotFoundException e) {
            System.out.println("Failed to read supplied arguments - file not found (" + e.getMessage() + ")");
            help.printHelp("parser", options, true);
            statusCode += 5;
        }
        return statusCode;
    }

    private static void assignInputsToVariables(Properties defaults, CommandLine cl) {

        for (ParserDriverVariables var : vars) {
            // what base path to use for magetab files.
            if (cl.hasOption(var.optionPrefix)) {
                var.value = cl.getOptionValue(var.optionPrefix);
            } else {
                var.value = defaults.getProperty("parser." + var.varName);
                getLog().info("Using default property for '" + var.varName + "': " + var.value);
            }
        }

    }


    private static Options bindOptions() {

        Options options = new Options();

        // help
        Option helpOption = new Option("h", "help", false, "Print the help");
        options.addOption(helpOption);

        for (ParserDriverVariables var : vars) {
            if (usedPrefixes.contains(var.optionPrefix)) {
                getLog().error("Prefix " + var.optionPrefix + " has already been used.");
                System.exit(1);
            }
            Option option = new Option(var.optionPrefix, var.varName, var.varrequired, var.message);
            option.setArgName(var.vartype);
            option.setRequired(var.varrequired);
            options.addOption(option);
        }

        return options;
    }

    protected static Logger getLog() {
        return log;
    }


    /**
     * Created with IntelliJ IDEA.
     * User: jmcmurry
     * Date: 13/02/2014
     * Time: 10:11
     * To change this template use File | Settings | File Templates.
     */
    public static class ParserDriverVariables {

        protected final String varName;
        protected final String vartype;
        protected final boolean varrequired;
        protected final String optionPrefix;
        protected final String message;
        protected String value;


        public ParserDriverVariables(String varName, String vartype, boolean varrequired, String optionPrefix, String message) {
            this.varName = varName;
            this.vartype = vartype;
            this.varrequired = varrequired;
            this.optionPrefix = optionPrefix;
            this.message = message;
        }

    }
}
