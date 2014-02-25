package BioCat;

import com.sun.jersey.api.core.ClassNamesResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.test.framework.AppDescriptor;
import com.sun.jersey.test.framework.JerseyTest;
import com.sun.jersey.test.framework.LowLevelAppDescriptor;
import junit.framework.Assert;

/**
 * @author Julie McMurry
 * @version 9/13/12
 * @time 6:56 PM
 * This class....
 */

public class BioCatResourceJaxrsTest extends JerseyTest {


    @Override   // appdescriptor is some method that describes what annotated resource classes are being used.
    protected AppDescriptor configure() {

        ResourceConfig resourceConfig = new ClassNamesResourceConfig(
                BioCatResource.class
        );

        return new LowLevelAppDescriptor.Builder(resourceConfig).build();

    }

    @org.junit.Test
    public void testLookup() {

        final String recordXML = resource().path("lookupRecord").queryParam("record", "2").get(String.class);

        Assert.assertNotNull(recordXML);

        final String submitterXML = resource().path("lookupSubmitter").queryParam("submitterURI", "http://www.biocatalogue.org/users/3").get(String.class);

        Assert.assertNotNull(submitterXML);

    }


}
