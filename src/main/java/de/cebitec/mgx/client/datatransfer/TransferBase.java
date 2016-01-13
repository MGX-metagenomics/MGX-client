package de.cebitec.mgx.client.datatransfer;

import de.cebitec.gpms.rest.RESTAccessI;
import de.cebitec.gpms.rest.RESTException;
import de.cebitec.mgx.client.exception.MGXServerException;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 *
 * @author sj
 */
public abstract class TransferBase {

    private final RESTAccessI restAccess;
    private final PropertyChangeSupport pcs;
    public static final String NUM_ELEMENTS_TRANSFERRED = "numElementsTransferred";
    public static final String TRANSFER_FAILED = "transferFailed";
    public static final String TRANSFER_COMPLETED = "transferCompleted";

    public TransferBase(RESTAccessI rab) {
        this.restAccess = rab;
        this.pcs = new PropertyChangeSupport(this);
    }

//    protected void catchException(ClientResponse res) throws MGXServerException {
//        RESTMethods.catchException(res);
//    }
    protected void fireTaskChange(String propName, long total_elements) {
        pcs.firePropertyChange(propName, 0, total_elements);
    }

    public void addPropertyChangeListener(PropertyChangeListener p) {
        pcs.addPropertyChangeListener(p);
    }

    public void removePropertyChangeListener(PropertyChangeListener p) {
        pcs.removePropertyChangeListener(p);
    }

    public abstract long getProgress();

    protected final <U> U put(Object obj, Class<U> c, String... path) throws MGXServerException {
        try {
            return restAccess.put(obj, c, path);
        } catch (RESTException ex) {
            throw new MGXServerException(ex.getMessage());
        }
    }

    protected final void put(Object obj, String... path) throws MGXServerException {
        try {
            restAccess.put(obj, path);
        } catch (RESTException ex) {
            throw new MGXServerException(ex.getMessage());
        }
    }

    protected final <U> U get(Class<U> c, String... path) throws MGXServerException {
        try {
            return restAccess.get(c, path);
        } catch (RESTException ex) {
            throw new MGXServerException(ex.getMessage());
        }
    }

    protected final void get(String... path) throws MGXServerException {
        try {
            restAccess.get(path);
        } catch (RESTException ex) {
            throw new MGXServerException(ex.getMessage());
        }
    }

    protected final <U> U delete(Class<U> clazz, String... path) throws MGXServerException {
        try {
            return restAccess.delete(clazz, path);
        } catch (RESTException ex) {
            throw new MGXServerException(ex.getMessage());
        }
    }

    protected final void delete(String... path) throws MGXServerException {
        try {
            restAccess.delete(path);
        } catch (RESTException ex) {
            throw new MGXServerException(ex.getMessage());
        }
    }
    
    protected final <U> void post(U obj, String... path) throws MGXServerException {
        try {
            restAccess.post(obj, path);
        } catch (RESTException ex) {
            throw new MGXServerException(ex.getMessage());
        }
    }

}
