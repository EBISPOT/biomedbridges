package BioCat;

import org.apache.log4j.Logger;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * @author Julie McMurry
 * @version 2012-10-30
 *          This class contacts the BioCatalogue API to fetch the submitter info corresponding to the BioCat record numbers.
 */

public class BioCatMain {
    public static final Logger log = Logger.getLogger(BioCatMain.class);

    public static void main(String[] args) throws IOException {

        if (args.length == 2) {
            final String allRecords = args[0];
            final String outFileName = args[1];
            final List<String> recordNos = stringToList(allRecords, ",");
            new BioCatMain().processRecord(recordNos, outFileName);
        } else {
            System.out.println("USAGE: java -cp . " +
                    "\n<String of comma separated numbers of the BioCat records> " +
                    "\n<fully qualified file path for delimited output>");
        }
    }

    /**
     * Takes the list of record numbers, fetches the corresponding contact information,
     * @param recordNos
     * @param outFileName
     * @throws java.io.IOException
     */
    private void processRecord(List<String> recordNos, String outFileName) throws IOException {

        // Create a file to store the delimited results
        FileWriter fstream = new FileWriter(outFileName.replace(" ", "-"), true);
        BufferedWriter out = new BufferedWriter(fstream);

        for (String recNo : recordNos) {
            BioCatServiceImpl impl = new BioCatServiceImpl();   //todo: see if it could go
            String recXml = impl.getBioCatRecord(recNo);
            try {
                String submitterURI = getSubmitterURI(recXml);
                String submitterXML = impl.getBioCatSubmitter(submitterURI);
                String submitterName = getSubmitterName(submitterXML);
                String submitterEmail = getSubmitterEmail(submitterXML);
                String submitterAffil = getSubmitterAffil(submitterXML);
                String submitterHomepage = getSubmitterHomepage(submitterXML);
                out.write("\n"+recNo + "|" + submitterEmail + "|" + submitterHomepage +
                        "|" + submitterURI + "|" + submitterName + "|" + submitterAffil);

            } catch (Exception e) {
                log.error(recNo + "|Error");
                e.printStackTrace();

            }
        }

        out.flush();
        out.close();
    }


    public String getSubmitterHomepage(String submitterXML) {
        if (submitterXML.contains("<homepage/>")) return "";
        return parseFieldStingy("<homepage>", "</homepage>", submitterXML);
    }

    public String getSubmitterEmail(String submitterXML) {
        if (submitterXML.contains("<publicEmail/>")) return "";
        return parseFieldStingy("<publicEmail>", "</publicEmail>", submitterXML);
    }

    public String getSubmitterAffil(String submitterXML) {
        if (submitterXML.contains("<affiliation/>")) return "";
        return parseFieldStingy("<affiliation>", "</affiliation>", submitterXML);
    }

    public String getSubmitterName(String submitterXML) {
        if (submitterXML.contains("<name/>")) return "";
        return parseFieldStingy("<name>", "</name>", submitterXML);
    }

    /**
     * Submitters can be users or registries.
     * @param recXML BioCat service Record
     * @return  This will return the  URI (sans the extension .xls)
     * corresponding to the XML record passed in
     */
    public String getSubmitterURI(String recXML) {

        String usersTag = "http://www.biocatalogue.org/users/";
        String regTag = "http://www.biocatalogue.org/registries/";

        int startIndex = recXML.indexOf(usersTag);
        int endIndex = startIndex + usersTag.length() + 6;

        if (startIndex == -1) {
            startIndex = recXML.indexOf(regTag);
            endIndex = startIndex + regTag.length() + 6;
        }

        if (startIndex == -1) {
            log.error(recXML);
        }

        String submitterID = recXML.substring(startIndex, endIndex);
        submitterID = submitterID.substring(0, submitterID.indexOf("\" "));
        return submitterID;
    }

    //Takes a delimited string and converts it to a List
    public static List<String> stringToList(String string, String delim) {

        String[] array = string.split(delim);
        return Arrays.asList(array);
    }

    //

    /**
     * Finds the last index of the startTag and the first index of the endTag from within the text, returning the text between those tags
     * @param startTag
     * @param endTag
     * @param text to parse
     * @return field between the start and end tags, exclusive
     * @throws IndexOutOfBoundsException
     */
    public static String parseFieldStingy(String startTag, String endTag, String text) throws IndexOutOfBoundsException {

        String field = "";

        int start = 0, end = 0;

        try {

            //get start & end positions
            start = (text.lastIndexOf(startTag) >= 0) ? text.lastIndexOf(startTag) + startTag.length() : -1;

            // if end tag is blank, set end to end of string.
            if (endTag.equals("")) end = text.length();
            else end = (text.indexOf(endTag) >= 0) ? text.indexOf(endTag) : -1;

            //if missing start or end tags, just return empty field
            if (start >= 0 && end >= 0) {
                field = text.substring(start, end).trim();
            }

        } catch (Exception e) {
            if (start > end) {
                return parseFieldStingy(startTag, endTag, text.substring(end + endTag.length()));
            }
            String msg = "Can not parse (" + start + "-" + end + ")(" + startTag + "-" + endTag + ") of " + text;
            log.warn(msg);
            throw new IndexOutOfBoundsException(msg);
        }

        return field;
    }
}
