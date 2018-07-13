package de.cebitec.mgx.client.datatransfer;

import de.cebitec.gpms.rest.RESTAccessI;
import de.cebitec.gpms.rest.RESTException;
import de.cebitec.mgx.client.MGXDTOMaster;
import de.cebitec.mgx.client.exception.MGXServerException;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 *
 * @author sj
 */
public abstract class TransferBase implements PropertyChangeListener {

    private final RESTAccessI restAccess;
    private final MGXDTOMaster dtomaster;
    private final PropertyChangeSupport pcs;
    private boolean disposed = false;
    private volatile String error_message = null;
    //
    public static final String MESSAGE = "TransferBase_message";
    public static final String NUM_ELEMENTS_TRANSFERRED = "numElementsTransferred";
    public static final String TRANSFER_FAILED = "transferFailed";
    public static final String TRANSFER_COMPLETED = "transferCompleted";

    public TransferBase(final MGXDTOMaster dtomaster, final RESTAccessI rab) {
        this.restAccess = rab;
        this.dtomaster = dtomaster;
        this.pcs = new PropertyChangeSupport(this);
        dtomaster.addPropertyChangeListener(this);
    }

    @Override
    public final synchronized void propertyChange(PropertyChangeEvent evt) {
        if (evt.getSource().equals(dtomaster) && evt.getPropertyName().equals(MGXDTOMaster.PROP_LOGGEDIN)) {
            Boolean newVal = (Boolean) evt.getNewValue();
            if (!newVal) {
                dtomaster.removePropertyChangeListener(this);
                abortTransfer("Disconnected from server");
            }
        }
    }

    protected synchronized final void dispose() {
        if (!disposed) {
            dtomaster.removePropertyChangeListener(this);
            disposed = true;
        }
    }

    public final String getErrorMessage() {
        return error_message;
    }

    protected final void setErrorMessage(String msg) {
        // we only keep the first error message
        if (error_message == null) {
            error_message = msg;
        }
    }

    protected abstract void abortTransfer(String reason);

    protected void fireTaskChange(String propName, Object newVal) {
        pcs.firePropertyChange(propName, 0, newVal);
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
