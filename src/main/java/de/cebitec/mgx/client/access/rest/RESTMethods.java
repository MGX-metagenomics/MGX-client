package de.cebitec.mgx.client.access.rest;

import com.sun.jersey.api.client.Client;
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
        //System.err.println("PUT uri: " + getWebResource().path(path).getURI().toASCIIString());
        assert !EventQueue.isDispatchThread();
        ClientResponse res = getWebResource().path(path).type(PROTOBUF_TYPE).accept(PROTOBUF_TYPE).put(ClientResponse.class, obj);
        catchException(res);
        return res.<U>getEntity(c);
    }

    protected final <U> U get(final String path, Class<U> c) throws MGXServerException {
        //System.err.println("GET uri: " +getWebResource().path(path).getURI().toASCIIString());
        assert !EventQueue.isDispatchThread();
        ClientResponse res = getWebResource().path(path).type(PROTOBUF_TYPE).accept(PROTOBUF_TYPE).get(ClientResponse.class);
        catchException(res);
        return res.<U>getEntity(c);
    }

    protected final String delete(final String path) throws MGXServerException {
        //System.err.println("DELETE uri: " +getWebResource().path(path).getURI().toASCIIString());
        assert !EventQueue.isDispatchThread();
        ClientResponse res = getWebResource().path(path).type(PROTOBUF_TYPE).accept(PROTOBUF_TYPE).delete(ClientResponse.class);
        catchException(res);
        return res.<MGXString>getEntity(MGXString.class).getValue();
    }

    protected final <U> void post(final String path, U obj) throws MGXServerException {
        assert !EventQueue.isDispatchThread();
        ClientResponse res = getWebResource().path(path).type(PROTOBUF_TYPE).accept(PROTOBUF_TYPE).post(ClientResponse.class, obj);
        catchException(res);
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
            throw new MGXServerException(msg.toString());
        }
    }
}
