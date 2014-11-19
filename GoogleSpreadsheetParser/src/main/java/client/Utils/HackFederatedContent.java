package client.Utils;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Hack to get search terms into and from federated RDB-generated XML
 * This has nothing to do with spreadsheet.
 * Created with IntelliJ IDEA.
 * User: jmcmurry
 * Date: 17/11/2014
 * Time: 22:27
 */
public class HackFederatedContent {

    private static HashSet<String> finalLines = new HashSet<String>();
    private static HashMap<String, ArrayList<String>> termsMap = new HashMap<String, ArrayList<String>>();
    private static HashSet<String> autocompleteTerms = new HashSet<>();

    private static String[] openTags = {
            "<Description>",
            "<FunctionDescription>",
            "<Functions>",
            "<Infrastrutures>",
            "<InputFormats>",
            "<InputTypes>",
            "<Institutions>",
            "<Interfaces>",
            "<Languages>",
            "<License>",
            "<Name>",
            "<OutputFormats>",
            "<OutputTypes>",
            "<Platforms>",
            "<Tags>",
            "<Topics>",
            "<Type>",
            "<WorkPackages>",
    };

    private static String[] closeTags = {
            "</Description>",
            "</FunctionDescription>",
            "</Functions>",
            "</Infrastrutures>",
            "</InputFormats>",
            "</InputTypes>",
            "</Institutions>",
            "</Interfaces>",
            "</Languages>",
            "</License>",
            "</Name>",
            "</OutputFormats>",
            "</OutputTypes>",
            "</Platforms>",
            "</Tags>",
            "</Topics>",
            "</Type>",
            "</WorkPackages>",
    };


    public static void main(String[] args) {
        fixFederatedContent("/Users/jmcmurry/code/bmb-registry/GoogleSpreadsheetParser/src/main/resources/FlattenedCuratedContentRaw.txt");
    }

    public static void fixFederatedContent(String filename) {

        BufferedReader in = null;
        try {
            in = new BufferedReader(new FileReader(new File(filename)));
        } catch (FileNotFoundException e) {
            System.out.println(filename + " could not be found.");
            e.printStackTrace();  //todo:
        }

        try {
            assert in != null;
            while (in.ready()) {
                String line = in.readLine();
                String concatenatedTerms = parseLine(line);
                line = line.replace("</Tool>", concatenatedTerms + "</Tool>");
//                line = line.replaceAll("<[^/]", "\n<");
                finalLines.add(line);
            }

            printAutocompleteTerms("/Users/jmcmurry/code/bmb-registry/GoogleSpreadsheetParser/src/main/resources/AutocompleteFederated.txt");
            printResult("/Users/jmcmurry/code/bmb-registry/GoogleSpreadsheetParser/src/main/resources/FixedFederatedContent.xml");
            in.close();

        } catch (IOException e) {
            e.printStackTrace();  //todo:
        }
    }


    private static String parseLine(String line) {

        String concatenatedSearchTerms = "<SearchTerms>";

        ArrayList<String> terms = new ArrayList<>();
        String id = parseField(line, "<Name>", "</Name>", true);


        for (int i = 0; i < openTags.length; i++) {

            HashSet<String> results = parseField(line, i);

            terms.addAll(results);

            if (i != 0)
                autocompleteTerms.addAll(results);

        }

        termsMap.put(id, terms);

        if (terms.isEmpty()) return "";

        for (String term : terms) {
            concatenatedSearchTerms += term + " | ";
        }
        return concatenatedSearchTerms + "</SearchTerms>";
    }

    private static String parseField(String textToSearch, String openToken, String closeToken, boolean b) {
        int tokenIndex = textToSearch.indexOf(openToken);
        if (tokenIndex == -1) return "";
        int tokenCloseIndex = textToSearch.indexOf(closeToken);

        String result = textToSearch.substring(tokenIndex + openToken.length(), tokenCloseIndex);
        return result;
    }


    private static HashSet<String> parseField(String textToSearch, int indexOfToken) {
        return parseField(textToSearch, openTags[indexOfToken], closeTags[indexOfToken], indexOfToken);
    }

    private static HashSet<String> parseField(String textToSearch, String openToken, String closeToken, int fieldIndex) {

        HashSet<String> terms = new HashSet<>();
        int tokenIndex = textToSearch.indexOf(openToken);
        if (tokenIndex == -1) return terms;
        int tokenCloseIndex = textToSearch.indexOf(closeToken);

        String result = textToSearch.substring(tokenIndex + openToken.length(), tokenCloseIndex);

        if (fieldIndex == 10) {
            int pipeIndex = result.indexOf("|");
            if (pipeIndex == -1) {
                System.out.println("Error, no homepage for " + textToSearch.substring(0, 100));
                System.exit(1);
            }
            result = result.substring(0, pipeIndex);
        }
        String[] resultArray = result.split(",");

        for (String singleResult : resultArray) {
            terms.add(singleResult.trim());
        }

        return terms;

    }

    private static void printAutocompleteTerms(String filename) {

        StringBuilder searchTermsLog = new StringBuilder();

        for (String line : autocompleteTerms) {
            searchTermsLog.append("\n" + line);
        }


        try {
            File file = new File(filename);
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.append(searchTermsLog);
            writer.flush();
        } catch (IOException e) {
            System.out.println("Error writing term requests to file.");
            e.printStackTrace();  //todo:
        }
    }

    private static void printResult(String filename) {
        StringBuilder finalResults = new StringBuilder();
        finalResults.append("<Tools xmlns=\"http://wwwdev.ebi.ac.uk/fgpt/toolsui/schema\"\n" +
                "       xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                "        >\n" +
                "    xsi:schemaLocation=\"\n" +
                "    http://www.ebi.ac.uk/fgpt/toolsui/schema\n" +
                "    http://www.ebi.ac.uk/fgpt/toolsui/2014/10/29/schema.xsd\n" +
                "    \">");


        for (String line : finalLines) {
            finalResults.append("\n" + line);
        }

        finalResults.append("</Tools>");


        try {
            File file = new File(filename);
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.append(finalResults);
            writer.flush();
        } catch (IOException e) {
            System.out.println("Error writing term requests to file.");
            e.printStackTrace();  //todo:
        }
    }
}
