package de.cebitec.mgx.client.upload;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.ClientResponse.Status;
import de.cebitec.mgx.client.exception.MGXServerException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sj
 */
public abstract class UploadBase {

    private CallbackI cb = null;
    private String error_message = "";

    public String getErrorMessage() {
        return error_message;
    }

    protected void setErrorMessage(String msg) {
        error_message = msg;
    }

    public void setProgressCallback(CallbackI cb) {
        this.cb = cb;
    }

    protected CallbackI getProgressCallback() {
        return cb != null ? cb
                : new NullCallBack();
    }

    public abstract boolean upload();

    private final class NullCallBack implements CallbackI {

        @Override
        public void callback(int i) {
        }
    }

    protected void catchException(ClientResponse res) throws MGXServerException {
        if (res.getClientResponseStatus() != Status.OK) {
            InputStreamReader isr = new InputStreamReader(res.getEntityInputStream());
            BufferedReader r = new BufferedReader(isr);
            StringBuilder msg = new StringBuilder();
            String buf;
            try {
                while ((buf = r.readLine()) != null) {
                    msg.append(buf);
                }
            } catch (IOException ex) {
            } finally {
                try {
                    r.close();
                    isr.close();
                } catch (IOException ex) {
                    Logger.getLogger(UploadBase.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            throw new MGXServerException(msg.toString());
        }
    }
}
