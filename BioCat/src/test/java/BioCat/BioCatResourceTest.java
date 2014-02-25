package BioCat;

import junit.framework.TestCase;
import org.junit.Test;

/**
 * @author Julie McMurry
 * @version 2012-11-01
 * @time 6:56 PM
 * This class test the functionality of the methods in BioCatMain independent of whether the BioCat service is operational.
 */

public class BioCatResourceTest extends TestCase {

    @Test   // must return void even though the method really returns a string
    public void testLookup() {

        //todo: new mock that always throws an exception then test to ensure it is handled as expected
        MockBioCatService service = new MockBioCatService();

        BioCatResource resource = new BioCatResource(service);

        String recordXML = resource.getBioCatRecordXML("2");
        BioCatMain main = new BioCatMain();
        String submitterID = main.getSubmitterURI(recordXML);

        assertEquals("http://www.biocatalogue.org/users/3", submitterID);

        String submitterXML = resource.getBioCatSubmitterXML(submitterID);
        String submitterEmail = main.getSubmitterEmail(submitterXML);
        String submitterAffil = main.getSubmitterAffil(submitterXML);

        assertEquals("", submitterEmail);

        assertEquals("EBI", submitterAffil);

    }
}
