package BioCat;

import java.io.UnsupportedEncodingException;

/**
 * @author Julie McMurry
 * @version 2012-11-01
 * @time 7:42 PM
 * This class provides a mock service
 */

public class MockBioCatService implements BioCatService{

    @Override
    public String getBioCatSubmitter(String submitterURI) throws UnsupportedEncodingException {
        return "<user xmlns:xlink=\"http://www.w3.org/1999/xlink\" xmlns:dcterms=\"http://purl.org/dc/terms/\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"http://www.biocatalogue.org/2009/xml/rest\" resourceType=\"User\" xlink:href=\"http://www.biocatalogue.org/users/3\" xsi:schemaLocation=\"http://www.biocatalogue.org/2009/xml/rest http://www.biocatalogue.org/2009/xml/rest/schema-v1.xsd\" resourceName=\"jenny\" xlink:title=\"Member - jenny\">\n" +
                "<dc:title>Member - jenny</dc:title>\n" +
                "<name>jenny</name>\n" +
                "<affiliation>EBI</affiliation>\n" +
                "<location>\n" +
                "<city/>\n" +
                "<country>United Kingdom</country>\n" +
                "<iso3166CountryCode>GB</iso3166CountryCode>\n" +
                "<flag xlink:href=\"http://www.biocatalogue.org/images/flags/gb.png\" xlink:title=\"Flag icon for this location\"/>\n" +
                "</location>\n" +
                "<publicEmail/>\n" +
                "<joined>2008-11-11T19:02:21Z</joined>\n" +
                "<related>\n" +
                "<annotationsBy resourceType=\"Annotations\" xlink:href=\"http://www.biocatalogue.org/users/3/annotations_by\" xlink:title=\"All annotations by this User\"/>\n" +
                "<services resourceType=\"Services\" xlink:href=\"http://www.biocatalogue.org/users/3/services\" xlink:title=\"All services that this User has submitted\"/>\n" +
                "</related>\n" +
                "</user>";
    }

    @Override
    public String getBioCatRecord(String recordNo) throws UnsupportedEncodingException {
        return "<service xmlns:xlink=\"http://www.w3.org/1999/xlink\" xmlns:dcterms=\"http://purl.org/dc/terms/\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"http://www.biocatalogue.org/2009/xml/rest\" resourceType=\"Service\" xlink:href=\"http://www.biocatalogue.org/services/2\" xsi:schemaLocation=\"http://www.biocatalogue.org/2009/xml/rest http://www.biocatalogue.org/2009/xml/rest/schema-v1.xsd\" xlink:title=\"Service - eFetchPmcService\" resourceName=\"eFetchPmcService\">\n" +
                "<dc:title>Service - eFetchPmcService</dc:title>\n" +
                "<name>eFetchPmcService</name>\n" +
                "<originalSubmitter resourceType=\"User\" xlink:href=\"http://www.biocatalogue.org/users/3\" xlink:title=\"Member - jenny\" resourceName=\"jenny\"/>\n" +
                "<dc:description>\n" +
                "The EFetch services retrieve records in the requested format from several databases hosted by the NCBI.\n" +
                "</dc:description>\n" +
                "<serviceTechnologyTypes>\n" +
                "<type>SOAP</type>\n" +
                "</serviceTechnologyTypes>\n" +
                "<latestMonitoringStatus>\n" +
                "<label>PASSED</label>\n" +
                "<message>All tests were successful for this service</message>\n" +
                "<symbol xlink:href=\"http://www.biocatalogue.org/images/tick-sphere-50.png?1344931798\" xlink:title=\"Large status symbol icon for this monitoring status\"/>\n" +
                "<smallSymbol xlink:href=\"http://www.biocatalogue.org/images/small-tick-sphere-50.png?1344931798\" xlink:title=\"Small status symbol icon for this monitoring status\"/>\n" +
                "<lastChecked>2012-08-16T09:32:21Z</lastChecked>\n" +
                "</latestMonitoringStatus>\n" +
                "<dcterms:created>2008-11-13T19:23:32Z</dcterms:created>\n" +
                "<deployments resourceType=\"Service\" xlink:href=\"http://www.biocatalogue.org/services/2/deployments\">\n" +
                "<serviceDeployment resourceType=\"ServiceDeployment\" xlink:href=\"http://www.biocatalogue.org/service_deployments/2\">\n" +
                "<endpoint>\n" +
                "http://eutils.ncbi.nlm.nih.gov/entrez/eutils/soap/v2.0/soap_adapter_2_0.cgi?db=pmc\n" +
                "</endpoint>\n" +
                "<serviceProvider resourceType=\"ServiceProvider\" xlink:href=\"http://www.biocatalogue.org/service_providers/2\" xlink:title=\"Service Provider - National Center for Biotechnology Information (NCBI)\" resourceName=\"National Center for Biotechnology Information (NCBI)\">\n" +
                "<dc:title>\n" +
                "Service Provider - National Center for Biotechnology Information (NCBI)\n" +
                "</dc:title>\n" +
                "<name>\n" +
                "National Center for Biotechnology Information (NCBI)\n" +
                "</name>\n" +
                "<dc:description/>\n" +
                "<dcterms:created>2008-11-13T19:23:32Z</dcterms:created>\n" +
                "</serviceProvider>\n" +
                "<location>\n" +
                "<city>Gaithersburg</city>\n" +
                "<country>United States</country>\n" +
                "<iso3166CountryCode>US</iso3166CountryCode>\n" +
                "<flag xlink:href=\"http://www.biocatalogue.org/images/flags/us.png\" xlink:title=\"Flag icon for this location\"/>\n" +
                "</location>\n" +
                "<submitter resourceType=\"User\" xlink:href=\"http://www.biocatalogue.org/users/3\" xlink:title=\"Member - jenny\" resourceName=\"jenny\"/>\n" +
                "<dcterms:created>2008-11-13T19:23:32Z</dcterms:created>\n" +
                "</serviceDeployment>\n" +
                "</deployments>\n" +
                "<variants resourceType=\"Service\" xlink:href=\"http://www.biocatalogue.org/services/2/variants\">\n" +
                "<soapService resourceType=\"SoapService\" xlink:href=\"http://www.biocatalogue.org/soap_services/2\" xlink:title=\"Soap Service - eFetchPmcService\" resourceName=\"eFetchPmcService\">\n" +
                "<dc:title>Soap Service - eFetchPmcService</dc:title>\n" +
                "<name>eFetchPmcService</name>\n" +
                "<wsdlLocation>\n" +
                "http://www.ncbi.nlm.nih.gov/entrez/eutils/soap/v2.0/efetch_pmc.wsdl\n" +
                "</wsdlLocation>\n" +
                "<submitter resourceType=\"User\" xlink:href=\"http://www.biocatalogue.org/users/3\" xlink:title=\"Member - jenny\" resourceName=\"jenny\"/>\n" +
                "<dc:description>\n" +
                "The EFetch services retrieve records in the requested format from several databases hosted by the NCBI.\n" +
                "</dc:description>\n" +
                "<documentationUrl>http://www.ncbi.nlm.nih.gov/books/NBK25501/</documentationUrl>\n" +
                "<dcterms:created>2008-11-13T19:23:31Z</dcterms:created>\n" +
                "</soapService>\n" +
                "</variants>\n" +
                "<related>\n" +
                "<withSummary resourceType=\"Service\" xlink:href=\"http://www.biocatalogue.org/services/2?include=summary\" xlink:title=\"View of this Service including the summary section\"/>\n" +
                "<withMonitoring resourceType=\"Service\" xlink:href=\"http://www.biocatalogue.org/services/2?include=monitoring\" xlink:title=\"View of this Service including the monitoring section\"/>\n" +
                "<withAllSections resourceType=\"Service\" xlink:href=\"http://www.biocatalogue.org/services/2?include=all\" xlink:title=\"A complete view of this Service which includes the summary, deployments, variants and monitoring\"/>\n" +
                "<summary resourceType=\"Service\" xlink:href=\"http://www.biocatalogue.org/services/2/summary\" xlink:title=\"Just the summary of this Service\"/>\n" +
                "<deployments resourceType=\"Service\" xlink:href=\"http://www.biocatalogue.org/services/2/deployments\" xlink:title=\"Just the deployments for this Service\"/>\n" +
                "<variants resourceType=\"Service\" xlink:href=\"http://www.biocatalogue.org/services/2/variants\" xlink:title=\"Just the variants (i.e.: SOAP and/or REST) for this Service\"/>\n" +
                "<monitoring resourceType=\"Service\" xlink:href=\"http://www.biocatalogue.org/services/2/monitoring\" xlink:title=\"Just the monitoring info for this Service\"/>\n" +
                "<annotations resourceType=\"Annotations\" xlink:href=\"http://www.biocatalogue.org/services/2/annotations\" xlink:title=\"All annotations on this Service\"/>\n" +
                "</related>\n" +
                "</service>";
    }
}
