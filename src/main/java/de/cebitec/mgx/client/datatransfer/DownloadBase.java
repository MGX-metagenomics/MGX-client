package de.cebitec.mgx.client.datatransfer;

import de.cebitec.gpms.rest.RESTAccessI;
import de.cebitec.mgx.client.MGXDTOMaster;

/**
 *
 * @author sjaenick
 */
public abstract class DownloadBase extends TransferBase {

    private volatile boolean aborted = false;

    public DownloadBase(MGXDTOMaster dtomaster, RESTAccessI rab) {
        super(dtomaster, rab);
    }

    @Override
    protected final void abortTransfer(String reason) {
        if (!aborted) {
            aborted = true;
            setErrorMessage(reason);
            fireTaskChange(TransferBase.TRANSFER_FAILED, reason);
        }
    }

    public abstract boolean download();

}
