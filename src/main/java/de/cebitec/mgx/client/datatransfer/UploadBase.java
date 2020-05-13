package de.cebitec.mgx.client.datatransfer;

import de.cebitec.gpms.rest.RESTAccessI;
import de.cebitec.mgx.client.MGXDTOMaster;

/**
 *
 * @author sj
 */
public abstract class UploadBase extends TransferBase {

//    private CallbackI cb = null;
    protected static final int DEFAULT_CHUNK_SIZE = 2048;
    private int chunk_size = DEFAULT_CHUNK_SIZE;

    public UploadBase(final MGXDTOMaster dtomaster, final RESTAccessI rab) {
        super(dtomaster, rab);
    }

    public final void setChunkSize(int i) {
        chunk_size = i;
    }
    
    public final int getChunkSize() {
        return chunk_size;
    }

    @Override
    protected void abortTransfer(String reason) {
        setErrorMessage(reason);
        fireTaskChange(TransferBase.TRANSFER_FAILED, reason);
        super.dispose();
    }

    public abstract boolean upload();

}
