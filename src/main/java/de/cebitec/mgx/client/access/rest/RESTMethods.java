package de.cebitec.mgx.client.access.rest;

import de.cebitec.mgx.client.MGXMaster;
import de.cebitec.mgx.client.exception.MGXServerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.ClientResponse.Status;
import com.sun.jersey.api.client.WebResource.Builder;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 *
 * @author sjaenick
 */
public abstract class RESTMethods {

    protected MGXMaster master;

//    protected <U> U put(String path, Object obj, Class c) throws MGXServerException {
//        System.err.println("PUT uri: " +master.getResource().path(path).getURI().toASCIIString());
//        ClientResponse res = master.getResource().path(path).type("application/x-protobuf").accept("application/x-protobuf").put(ClientResponse.class, obj);
//        catchException(res);
//        return (U) res.getEntity(c);
//    }

    protected final <U> U put(String path, Object obj, Class<U> c) throws MGXServerException {
        ClientResponse res; // = master.getResource().path(path).type("application/x-protobuf").accept("application/x-protobuf").head();
        Builder accept = master.getResource().path(path).type("application/x-protobuf").accept("application/x-protobuf");
        res = accept.put(ClientResponse.class, obj);
        catchException(res);
        return res.<U>getEntity(c);
    }

    protected final <U> U get(String path, Class<U> c) throws MGXServerException {
        //System.err.println("GET uri: " +master.getResource().path(path).getURI().toASCIIString());
        ClientResponse res = master.getResource().path(path).type("application/x-protobuf").get(ClientResponse.class);
        catchException(res);
        return res.<U>getEntity(c);
    }

    protected final void delete(String path) throws MGXServerException {
        ClientResponse res = master.getResource().path(path).type("application/x-protobuf").delete(ClientResponse.class);
        catchException(res);
    }

    protected final <U> void post(String path, U obj) throws MGXServerException {
        ClientResponse res = master.getResource().path(path).type("application/x-protobuf").post(ClientResponse.class, obj);
        catchException(res);
    }

    protected final void catchException(ClientResponse res) throws MGXServerException {
        if (res.getClientResponseStatus() != Status.OK) {
            BufferedReader r = new BufferedReader(new InputStreamReader(res.getEntityInputStream()));
            StringBuilder msg = new StringBuilder();
            String buf;
            try {
                while ((buf = r.readLine()) != null) {
                    msg.append(buf);
                }
            } catch (IOException ex) {
            }
            throw new MGXServerException(msg.toString());
        }
    }
}
