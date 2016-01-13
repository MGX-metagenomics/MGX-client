
package de.cebitec.mgx.client.datatransfer;

import de.cebitec.gpms.rest.RESTAccessI;

/**
 *
 * @author sjaenick
 */
public abstract class DownloadBase extends TransferBase {
    
    private CallbackI cb = null;
    private String error_message = "";

    public DownloadBase(RESTAccessI rab) {
        super(rab);
    }

    protected void abortTransfer(String reason, long total) {
        setErrorMessage(reason);
        fireTaskChange(TransferBase.TRANSFER_FAILED, 1);
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

    protected final static class NullCallBack implements CallbackI {

        @Override
        public void callback(long i) {
        }
    }
}
