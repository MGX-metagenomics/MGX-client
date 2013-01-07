
package de.cebitec.mgx.client.datatransfer;

import com.sun.jersey.api.client.ClientResponse;
import de.cebitec.mgx.client.exception.MGXServerException;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sjaenick
 */
public abstract class DownloadBase {
    
    private CallbackI cb = null;
    private String error_message = "";
    private final PropertyChangeSupport pcs;
    public static final String NUM_ELEMENTS_RECEIVED = "numElementsReceived";

    public DownloadBase() {
        pcs = new PropertyChangeSupport(this);
    }

    protected void abortTransfer(String reason, long total) {
        setErrorMessage(reason);
        fireTaskChange(total);
    }

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
                : new DownloadBase.NullCallBack();
    }

    public abstract boolean download();

    protected void fireTaskChange(long total_elements) {
        pcs.firePropertyChange(NUM_ELEMENTS_RECEIVED, 0, total_elements);
    }

    protected final static class NullCallBack implements CallbackI {

        @Override
        public void callback(long i) {
        }
    }

    protected void catchException(ClientResponse res) throws MGXServerException {
        if (res.getClientResponseStatus() != ClientResponse.Status.OK) {
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

    public void addPropertyChangeListener(PropertyChangeListener p) {
        pcs.addPropertyChangeListener(p);
    }

    public void removePropertyChangeListener(PropertyChangeListener p) {
        pcs.removePropertyChangeListener(p);
    }
}
