package de.cebitec.mgx.client.access.rest;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.ClientResponse.Status;
import com.sun.jersey.api.client.WebResource;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.dto.dto.MGXString;
import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.SSLHandshakeException;

/**
 *
 * @author sjaenick
 */
public abstract class RESTMethods  {

    protected final static String PROTOBUF_TYPE = "application/x-protobuf";
    private Client client;
    private URI resource;

    public final void setClient(Client c, String res) {
        client = c;
        try {
            resource = new URI(res);
        } catch (URISyntaxException ex) {
            Logger.getLogger(RESTMethods.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    protected final WebResource getWebResource() {
        return client.resource(resource);
    }
    /**
     *
     * @param <U>
     * @param path REST URI
     * @param obj object to send
     * @param c class of U
     * @return
     * @throws MGXServerException
     */
    protected final <U> U put(Object obj, Class<U> c, final String... path) throws MGXServerException {
        assert !EventQueue.isDispatchThread();
        try {
            ClientResponse res = buildPath(path).put(ClientResponse.class, obj);
            catchException(res);
            return res.<U>getEntity(c);
        } catch (ClientHandlerException ex) {
            if (ex.getCause() != null && ex.getCause() instanceof SSLHandshakeException) {
                return put(obj, c, path); // retry
            } else {
                throw ex; // rethrow
            }
        }
    }

    protected final <U> U get(Class<U> c, final String... path) throws MGXServerException {
        //System.err.println("GET uri: " +getWebResource().path(path).getURI().toASCIIString());
        assert !EventQueue.isDispatchThread();
        try {
            ClientResponse res = buildPath(path).get(ClientResponse.class);
            catchException(res);
            return res.<U>getEntity(c);
        } catch (ClientHandlerException ex) {
            if (ex.getCause() != null && ex.getCause() instanceof SSLHandshakeException) {
                return get(c, path); // retry
            } else {
                throw ex; // rethrow
            }
        }
    }

    protected final String delete(final String... path) throws MGXServerException {
        //System.err.println("DELETE uri: " +getWebResource().path(path).getURI().toASCIIString());
        assert !EventQueue.isDispatchThread();
        try {
            ClientResponse res = buildPath(path).delete(ClientResponse.class);
            catchException(res);
            return res.<MGXString>getEntity(MGXString.class).getValue();
        } catch (ClientHandlerException ex) {
            if (ex.getCause() != null && ex.getCause() instanceof SSLHandshakeException) {
                return delete(path); // retry
            } else {
                throw ex; // rethrow
            }
        }
    }

    protected final <U> void post(U obj, final String... path) throws MGXServerException {
        assert !EventQueue.isDispatchThread();
        try {
            ClientResponse res = buildPath(path).post(ClientResponse.class, obj);
            catchException(res);
        } catch (ClientHandlerException ex) {
            if (ex.getCause() != null && ex.getCause() instanceof SSLHandshakeException) {
                post(obj, path);
            } else {
                throw ex; // rethrow
            }
        }
    }

    private WebResource.Builder buildPath(String... pathComponents) {
        WebResource wr = getWebResource();
        try {
            for (String s : pathComponents) {
                wr = wr.path(URLEncoder.encode(s, "UTF-8"));
            }
            return wr.type(PROTOBUF_TYPE).accept(PROTOBUF_TYPE);
        } catch (UnsupportedEncodingException ex) {
            throw new ClientHandlerException(ex);
        }
    }

    public static void catchException(final ClientResponse res) throws MGXServerException {
        if (Status.fromStatusCode(res.getStatus()) != Status.OK) {
            StringBuilder msg = new StringBuilder();
            try (BufferedReader r = new BufferedReader(new InputStreamReader(res.getEntityInputStream()))) {
                String buf;
                while ((buf = r.readLine()) != null) {
                    msg.append(buf);
                    msg.append(System.lineSeparator());
                }
            } catch (IOException ex) {
                Logger.getLogger(RESTMethods.class.getName()).log(Level.SEVERE, null, ex);
            }
            throw new MGXServerException(msg.toString().trim());
        }
    }
}
