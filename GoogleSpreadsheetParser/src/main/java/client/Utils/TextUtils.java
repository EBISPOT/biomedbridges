package client.Utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 * Created with IntelliJ IDEA.
 * User: jmcmurry
 * Date: 23/05/2013
 * Time: 18:13
 * To change this template use File | Settings | File Templates.
 */
public class TextUtils {

    private static Logger log = LoggerFactory.getLogger(TextUtils.class);

    // determines whether the two strings are acceptably fuzzy matched based on the edit distance between them
    // is case-insensitive
    public static boolean isFuzzyMatch(String entity1, String entity2, int maxNumDiffs, double maxPctDiffs) {

        if (entity1 == null || entity2 == null || entity1.equals("") || (entity2.equals(""))) return false;

        entity1 = entity1.toLowerCase().trim();
        entity2 = entity2.toLowerCase().trim();

        if (entity1.charAt(0) != entity2.charAt(0)) return false;

        int diffs = new DamerauLevenshtein(entity1, entity2).getNumDiffs();

        if (diffs == 0) return true;
        if (diffs / entity1.length() < .5)
            log.debug("Comparing \"" + entity1 + "\" with \"" + entity2 + "\": " + diffs + " differences out of " + +entity1.length() + " chars in original string.");

        boolean isFuzzyMatch = (diffs < maxNumDiffs) && ((double) diffs / entity1.length() <= maxPctDiffs);

        if (!isFuzzyMatch && diffs < 6 && entity1.length() > 20)
            log.debug("isFuzzyMatch result:" + isFuzzyMatch + "\t\tentity: \t" + entity1 + "\t\ttext: " + entity2 + "\tdiffs:" + diffs + "\tpctDiffs:" + (double) diffs / entity1.length());

        return isFuzzyMatch;
    }

    public static boolean isTrue(String option) {

        if (option == null) return false;

        return option.startsWith("t") || option.startsWith("y") ||
                option.startsWith("T") || option.startsWith("Y");
    }

    public static String arrayListToString(ArrayList list) {
        String outstring = "";
        for (Object o : list) {
            outstring += o.toString() + ", ";
        }
        return outstring;
    }

    public static String[] splitWithMultipleDelims(String stringToSplit) {
        String[] regularDelims = {",", ";"};
        String[] escapedDelims = {"\\|", "\n"};
        String allDelims = "[";
        for (String delim : regularDelims) {
            allDelims += ("|" + delim);
        }
        for (String delim : escapedDelims) {
            allDelims += ("|\\" + delim);
        }
        allDelims += "]";

        return stringToSplit.split(allDelims);
    }

    private static String stringToSingular(String pluralString) {

        try {
            if (pluralString.endsWith("sses")) return pluralString.substring(0, pluralString.length() - 3);
            if (pluralString.endsWith("ies")) return pluralString.substring(0, pluralString.length() - 3) + "y";
            if (pluralString.endsWith("es")) return pluralString.substring(0, pluralString.length() - 2);
            if (pluralString.endsWith("s")) return pluralString.substring(0, pluralString.length() - 1);
            return pluralString;
        } catch (NullPointerException e) {
            if (pluralString == null) {
                log.error("Worksheet name is null.");
                Throwable t = new Throwable();
                t.printStackTrace();
            }
        }

        return "Foo";
    }

    public static String[] stringArrayToLowercase(String[] stringArray) {

        for (int i = 0; i < stringArray.length; i++) {
            if (stringArray[i] != null) stringArray[i] = stringArray[i].toLowerCase();
        }

        return stringArray;
    }

    public static List<String> stringArrayToLowercase(List<String> list) {

        ListIterator<String> iterator = list.listIterator();
        while (iterator.hasNext()) {
            iterator.set(iterator.next().toLowerCase());
        }
        return list;
    }
}
