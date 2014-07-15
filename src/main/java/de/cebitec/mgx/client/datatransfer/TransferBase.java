package de.cebitec.mgx.client.datatransfer;

import com.sun.jersey.api.client.ClientResponse;
import de.cebitec.mgx.client.access.rest.RESTMethods;
import de.cebitec.mgx.client.exception.MGXServerException;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 *
 * @author sj
 */
public abstract class TransferBase {

    private final PropertyChangeSupport pcs;
    public static final String NUM_ELEMENTS_SENT = "numElementsSent";
    public static final String NUM_ELEMENTS_RECEIVED = "numElementsReceived";
    public static final String TRANSFER_FAILED = "transferFailed";
    public static final String TRANSFER_COMPLETED = "transferCompleted";

    public TransferBase() {
        this.pcs = new PropertyChangeSupport(this);
    }

    protected void catchException(ClientResponse res) throws MGXServerException {
        RESTMethods.catchException(res);
    }

    protected void fireTaskChange(String propName, long total_elements) {
        pcs.firePropertyChange(propName, 0, total_elements);
    }

    public void addPropertyChangeListener(PropertyChangeListener p) {
        pcs.addPropertyChangeListener(p);
    }

    public void removePropertyChangeListener(PropertyChangeListener p) {
        pcs.removePropertyChangeListener(p);
    }
}
