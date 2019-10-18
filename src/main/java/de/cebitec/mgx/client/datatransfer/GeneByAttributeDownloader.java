package de.cebitec.mgx.client.datatransfer;

import de.cebitec.gpms.rest.RESTAccessI;
import de.cebitec.mgx.client.MGXDTOMaster;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.dto.dto.AttributeDTOList;
import de.cebitec.mgx.dto.dto.MGXString;
import de.cebitec.mgx.dto.dto.SequenceDTOList;
import de.cebitec.mgx.sequence.DNASequenceI;
import de.cebitec.mgx.sequence.SeqWriterI;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.SSLHandshakeException;
import javax.ws.rs.ProcessingException;

/**
 *
 * @author sjaenick
 */
public class GeneByAttributeDownloader extends SeqDownloader {

    private final AttributeDTOList attrs;

    public GeneByAttributeDownloader(MGXDTOMaster dtomaster, RESTAccessI rab, AttributeDTOList attrs, SeqWriterI<? extends DNASequenceI> writer, boolean closeWriter) {
        super(dtomaster, rab, writer, closeWriter);
        this.attrs = attrs;
    }

    @Override
    protected String initTransfer() throws MGXServerException {
        try {
            MGXString session_uuid = super.put(attrs, MGXString.class, "Gene", "initDownloadforAttributes");
            fireTaskChange(TransferBase.NUM_ELEMENTS_TRANSFERRED, total_elements);
            String uuid = session_uuid.getValue();
            if (uuid == null || "".equals(uuid)) {
                throw new MGXServerException("Could not initialize transfer");
            }
            return uuid;
        } catch (ProcessingException ex) {
            if (ex.getCause() != null && ex.getCause() instanceof SSLHandshakeException) {
                Logger.getLogger(GeneByAttributeDownloader.class.getName()).log(Level.SEVERE, null, ex);
                return initTransfer();
            }
        }
        return null;
    }

    @Override
    protected void finishTransfer(String uuid) throws MGXServerException {
        try {
            super.get("Gene", "closeDownload", uuid);
            fireTaskChange(TransferBase.NUM_ELEMENTS_TRANSFERRED, total_elements);
        } catch (ProcessingException ex) {
            if (ex.getCause() != null && ex.getCause() instanceof SSLHandshakeException) {
                finishTransfer(uuid);
            }
        }
    }

    @Override
    protected SequenceDTOList fetchChunk(String session_uuid) throws MGXServerException {
        try {
            SequenceDTOList entity = super.get(SequenceDTOList.class, "Gene", "fetchSequences", session_uuid);
            return entity;
        } catch (ProcessingException ex) {
            if (ex.getCause() != null && ex.getCause() instanceof SSLHandshakeException) {
                return fetchChunk(session_uuid);
            }
        }
        return null;
    }
}
