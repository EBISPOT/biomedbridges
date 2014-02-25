package BioCat;

import java.io.UnsupportedEncodingException;

/**
 * @author Julie McMurry
 * @version 2012-11-01
 * @time 7:34 PM
 * This interface prescribes two methods to be implemented, both of which return individual XML records from BioCatalogue
 */

public interface BioCatService {

    /**
     * @param recordNo from BioCatalogue
     * @return XML corresponding to the record
     * @throws java.io.UnsupportedEncodingException
     */
    public String getBioCatRecord(String recordNo)throws UnsupportedEncodingException;

    /**
     * @param submitterURI from BioCatalogue
     * @return XML corresponding to the submitter
     * @throws java.io.UnsupportedEncodingException
     */
    public String getBioCatSubmitter(String submitterURI)throws UnsupportedEncodingException;

}
