package BioCat;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.WebResource;
import org.apache.log4j.Logger;

/**
 * @author Julie McMurry
 * @version 30 Oct 2012
 *          This is a utility that takes the URI for a webservice record or submitter in BioCatalog and issues a request
 *          to BioCatalog for the corresponding xml.
 */

public final class BioCatServiceImpl implements BioCatService {

    public static final Logger log = Logger.getLogger(BioCatServiceImpl.class);

    private static final String querySuffix = ".xml";

    @Override
    public String getBioCatRecord(String recordNo) {

        try {

            Client client = Client.create();  // client can access 0 or more web resources for you

            WebResource webResource = client.resource("http://www.biocatalogue.org/services/" + recordNo + querySuffix);

            return (webResource.get(String.class));   // returns the string that results from above invocation

        } catch (ClientHandlerException e) {
            log.warn("Can not reach BioCatalog.");
            return "";
        }
    }

    @Override
    public String getBioCatSubmitter(String submitterURI) {

        Client client = Client.create();  // client can access 0 or more web resources for you

        WebResource webResource = client.resource(submitterURI + querySuffix);

        return (webResource.get(String.class));   // returns the string that results from above invocation
    }


//    public static String encodeQueryStr(String queryString) {
//
//        String encodedString = null;
//
//        try {
//            encodedString = URLEncoder.encode(queryString, "UTF-8");
//        } catch (UnsupportedEncodingException e) {
//            log.error("Query string [" + queryString + "] was not properly encoded.");
//            e.printStackTrace();
//        }
//
//        log.debug(encodedString);
//
//        return encodedString;
//
//    }
}
