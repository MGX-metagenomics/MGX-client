package de.cebitec.mgx.client.access.rest;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import de.cebitec.mgx.osgiutils.MGXOptions;
import java.net.URLEncoder;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import static org.ops4j.pax.exam.CoreOptions.bundle;
import static org.ops4j.pax.exam.CoreOptions.junitBundles;
import static org.ops4j.pax.exam.CoreOptions.options;
import static org.ops4j.pax.exam.CoreOptions.systemProperty;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;

/**
 *
 * @author sj
 */
@RunWith(PaxExam.class)
public class RESTTests {

    @Configuration
    public static Option[] configuration() {
        return options(
                junitBundles(),
                MGXOptions.clientBundles(),
                systemProperty("org.ops4j.pax.logging.DefaultServiceLog.level").value("WARN"),
                bundle("reference:file:target/classes")
        );
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
