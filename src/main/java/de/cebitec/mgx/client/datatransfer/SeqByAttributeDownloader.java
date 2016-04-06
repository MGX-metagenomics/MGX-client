package de.cebitec.mgx.client.datatransfer;

import com.sun.jersey.api.client.ClientHandlerException;
import de.cebitec.gpms.rest.RESTAccessI;
import de.cebitec.mgx.client.MGXDTOMaster;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.dto.dto.AttributeDTOList;
import de.cebitec.mgx.dto.dto.MGXString;
import de.cebitec.mgx.sequence.DNASequenceI;
import de.cebitec.mgx.sequence.SeqWriterI;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.SSLHandshakeException;

/**
 *
 * @author sjaenick
 */
public class SeqByAttributeDownloader extends SeqDownloader {

    private final AttributeDTOList attrs;

    public SeqByAttributeDownloader(MGXDTOMaster dtomaster, RESTAccessI rab, AttributeDTOList attrs, SeqWriterI<DNASequenceI> writer, boolean closeWriter) {
        super(dtomaster, rab, writer, closeWriter);
        this.attrs = attrs;
    }

    @Override
    protected String initTransfer() throws MGXServerException {
        try {
            MGXString session_uuid = super.put(attrs, MGXString.class, "Sequence", "initDownloadforAttributes");
            fireTaskChange(TransferBase.NUM_ELEMENTS_TRANSFERRED, total_elements);
            String uuid = session_uuid.getValue();
            if (uuid == null || "".equals(uuid)) {
                throw new MGXServerException("Could not initialize transfer");
            }
            return uuid;
        } catch (ClientHandlerException ex) {
            if (ex.getCause() != null && ex.getCause() instanceof SSLHandshakeException) {
                Logger.getLogger(SeqByAttributeDownloader.class.getName()).log(Level.SEVERE, null, ex);
                return initTransfer();
            }
        }
        return null;
    }
}
