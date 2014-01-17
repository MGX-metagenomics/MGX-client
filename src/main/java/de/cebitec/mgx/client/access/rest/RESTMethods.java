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
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.SSLHandshakeException;

/**
 *
 * @author sjaenick
 */
public abstract class RESTMethods {

    private Client client;
    private URI resource;
    protected final static String PROTOBUF_TYPE = "application/x-protobuf";

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
    protected final <U> U put(final String path, Object obj, Class<U> c) throws MGXServerException {
        assert !EventQueue.isDispatchThread();
        try {
            ClientResponse res = getWebResource().path(path).type(PROTOBUF_TYPE).accept(PROTOBUF_TYPE).put(ClientResponse.class, obj);
            catchException(res);
            return res.<U>getEntity(c);
        } catch (ClientHandlerException ex) {
            if (ex.getCause() != null && ex.getCause() instanceof SSLHandshakeException) {
                return put(path, obj, c); // retry
            } else {
                throw ex; // rethrow
            }
        }
    }

    protected final <U> U get(final String path, Class<U> c) throws MGXServerException {
        //System.err.println("GET uri: " +getWebResource().path(path).getURI().toASCIIString());
        assert !EventQueue.isDispatchThread(); 
        try {
            ClientResponse res = getWebResource().path(path).type(PROTOBUF_TYPE).accept(PROTOBUF_TYPE).get(ClientResponse.class);
            catchException(res);
            return res.<U>getEntity(c);
        } catch (ClientHandlerException ex) {
            if (ex.getCause() != null && ex.getCause() instanceof SSLHandshakeException) {
                return get(path, c); // retry
            } else {
                throw ex; // rethrow
            }
        }
    }

    protected final String delete(final String path) throws MGXServerException {
        //System.err.println("DELETE uri: " +getWebResource().path(path).getURI().toASCIIString());
        assert !EventQueue.isDispatchThread();
        try {
            ClientResponse res = getWebResource().path(path).type(PROTOBUF_TYPE).accept(PROTOBUF_TYPE).delete(ClientResponse.class);
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

    protected final <U> void post(final String path, U obj) throws MGXServerException {
        assert !EventQueue.isDispatchThread();
        try {
            ClientResponse res = getWebResource().path(path).type(PROTOBUF_TYPE).accept(PROTOBUF_TYPE).post(ClientResponse.class, obj);
            catchException(res);
        } catch (ClientHandlerException ex) {
            if (ex.getCause() != null && ex.getCause() instanceof SSLHandshakeException) {
                post(path, obj);
            } else {
                throw ex; // rethrow
            }
        }
    }

    public static void catchException(final ClientResponse res) throws MGXServerException {
        if (res.getClientResponseStatus() != Status.OK) {
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
