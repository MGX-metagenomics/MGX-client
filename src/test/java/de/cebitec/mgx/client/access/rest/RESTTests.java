package de.cebitec.mgx.client.access.rest;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author sj
 */
public class RESTTests {

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testGetAttribute() throws Exception {
        System.out.println("testRESTRegression");
        String baseURI = "http://foo/";
        Client c = Client.create();
        WebResource wr = c.resource(baseURI);
        assertNotNull(wr);
        String req = wr.path("bar").path("baz").getURI().toASCIIString();
        assertEquals("http://foo/bar/baz", req);
        //
        req = wr.path(URLEncoder.encode("bar/baz", "UTF-8")).getURI().toASCIIString();
        assertEquals("http://foo/bar%2Fbaz", req);
        //
        req = wr.path(URLEncoder.encode("bar|baz", "UTF-8")).getURI().toASCIIString();
        assertEquals("http://foo/bar%7Cbaz", req);
    }

}
