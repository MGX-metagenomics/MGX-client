package de.cebitec.mgx.client.datatransfer;

import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.dto.dto.AttributeDTOList;
import de.cebitec.mgx.dto.dto.MGXString;
import de.cebitec.mgx.sequence.DNASequenceI;
import de.cebitec.mgx.sequence.SeqWriterI;
import java.awt.EventQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.SSLHandshakeException;

/**
 *
 * @author sjaenick
 */
public class SeqByAttributeDownloader extends SeqDownloader {

    private final AttributeDTOList attrs;

    public SeqByAttributeDownloader(WebResource wr, AttributeDTOList attrs, SeqWriterI<DNASequenceI> writer, boolean closeWriter) {
        super(wr, writer, closeWriter);
        this.attrs = attrs;
    }

    @Override
    protected String initTransfer() throws MGXServerException {
        assert !EventQueue.isDispatchThread();
        try {
            ClientResponse res = wr.path("/Sequence/initDownloadforAttributes/").accept("application/x-protobuf").post(ClientResponse.class, attrs);
            catchException(res);
            fireTaskChange(TransferBase.NUM_ELEMENTS_TRANSFERRED, total_elements);
            MGXString session_uuid = res.<MGXString>getEntity(MGXString.class);
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
