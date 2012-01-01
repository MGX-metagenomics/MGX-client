package de.cebitec.mgx.client.access.rest;

import com.sun.jersey.api.client.Client;
import de.cebitec.mgx.client.exception.MGXServerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.ClientResponse.Status;
import com.sun.jersey.api.client.WebResource;
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
    protected final <U> U put(String path, Object obj, Class<U> c) throws MGXServerException {
        // System.err.println("PUT uri: " +wr.path(path).getURI().toASCIIString());
        ClientResponse res = getWebResource().path(path).type(PROTOBUF_TYPE).accept(PROTOBUF_TYPE).put(ClientResponse.class, obj);
        catchException(res);
        return res.<U>getEntity(c);
    }

    protected <U> U get(String path, Class<U> c) throws MGXServerException {
        //System.err.println("GET uri: " +master.getResource().path(path).getURI().toASCIIString());
        ClientResponse res = getWebResource().path(path).type(PROTOBUF_TYPE).accept(PROTOBUF_TYPE).get(ClientResponse.class);
        catchException(res);
        return res.<U>getEntity(c);
    }

    protected final void delete(String path) throws MGXServerException {
        ClientResponse res = getWebResource().path(path).type(PROTOBUF_TYPE).accept(PROTOBUF_TYPE).delete(ClientResponse.class);
        catchException(res);
    }

    protected final <U> void post(String path, U obj) throws MGXServerException {
        ClientResponse res = getWebResource().path(path).type(PROTOBUF_TYPE).accept(PROTOBUF_TYPE).post(ClientResponse.class, obj);
        catchException(res);
    }

    protected final void catchException(final ClientResponse res) throws MGXServerException {
        if (res.getClientResponseStatus() != Status.OK) {
            InputStreamReader isr = new InputStreamReader(res.getEntityInputStream());
            BufferedReader r = new BufferedReader(isr);
            StringBuilder msg = new StringBuilder();
            String buf;
            try {
                while ((buf = r.readLine()) != null) {
                    msg.append(buf);
                }
                r.close();
                isr.close();
            } catch (IOException ex) {
            }
            throw new MGXServerException(msg.toString());
        }
    }
}
