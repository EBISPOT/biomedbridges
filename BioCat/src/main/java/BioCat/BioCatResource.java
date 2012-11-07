package BioCat;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.io.UnsupportedEncodingException;

/**
 * @author Julie McMurry
 * @version 2012-11-01
 * @time 7:41 PM
 * This class is not very useful at the moment as it currently doesn't do anything more than the BioCatalogue API, however,
 * it could be expanded in the future.
 */
@Path("biocatalogue")
@Produces(MediaType.APPLICATION_XML)
public final class BioCatResource {

    private final BioCatService service;

    //NB: For JAX-RS
    public BioCatResource() {
        this(new BioCatServiceImpl());
    }

    public BioCatResource(final BioCatService service) {
        this.service = service;
    }

    //my-app.war
    //cp my-app.war $TOMCAT_HOME/webapps/
    //host:8080/my-app/biocatalogue/lookupRecord?record=foo
    //host:8080/my-other-app/foo/blah
    @GET
    @Path("lookupRecord")
    public String getBioCatRecordXML(@QueryParam("record") final String recordNo) {
        try {
            return service.getBioCatRecord(recordNo);
        } catch (UnsupportedEncodingException e) {
            //TODO: real error handling
            throw new RuntimeException(e);
        }
    }

    ////host:8080/my-app/biocatalogue/lookupWithYear?title=foo
    @GET
    @Path("lookupSubmitter")
    public String getBioCatSubmitterXML(@QueryParam("submitterURI") final String submitterURI) {
        try {
            return service.getBioCatSubmitter(submitterURI);
        } catch (UnsupportedEncodingException e) {
            //TODO: real error handling
            throw new RuntimeException(e);
        }
    }
}
