package de.cebitec.mgx.client.datatransfer;

import de.cebitec.gpms.rest.RESTAccessI;
import de.cebitec.mgx.client.MGXDTOMaster;

/**
 *
 * @author sj
 */
public abstract class UploadBase extends TransferBase {

    private CallbackI cb = null;
    protected static int DEFAULT_CHUNK_SIZE = 2048;
    protected int chunk_size = DEFAULT_CHUNK_SIZE;

    public UploadBase(MGXDTOMaster dtomaster, RESTAccessI rab) {
        super(dtomaster, rab);
    }

    public void setChunkSize(int i) {
        chunk_size = i;
    }
    
    public int getChunkSize() {
        return chunk_size;
    }

    @Override
    protected void abortTransfer(String reason) {
        setErrorMessage(reason);
        fireTaskChange(TransferBase.TRANSFER_FAILED, reason);
    }

    public void setProgressCallback(CallbackI cb) {
        this.cb = cb;
    }

    protected CallbackI getProgressCallback() {
        return cb != null ? cb
                : new NullCallBack();
    }

    public abstract boolean upload();

    private final static class NullCallBack implements CallbackI {

        @Override
        public void callback(long i) {
        }
    }
}
