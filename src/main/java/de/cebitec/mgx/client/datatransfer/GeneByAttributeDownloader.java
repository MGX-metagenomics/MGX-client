package de.cebitec.mgx.client.datatransfer;

import de.cebitec.gpms.rest.RESTAccessI;
import de.cebitec.mgx.client.MGXDTOMaster;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.dto.dto.AttributeDTOList;
import de.cebitec.mgx.dto.dto.MGXString;
import de.cebitec.mgx.dto.dto.SequenceDTO;
import de.cebitec.mgx.dto.dto.SequenceDTOList;
import de.cebitec.mgx.seqstorage.DNASequence;
import de.cebitec.mgx.seqstorage.QualityDNASequence;
import de.cebitec.mgx.sequence.DNAQualitySequenceI;
import de.cebitec.mgx.sequence.DNASequenceI;
import de.cebitec.mgx.sequence.SeqStoreException;
import de.cebitec.mgx.sequence.SeqWriterI;
import java.util.Set;
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
    private final Set<String> seenGeneNames;

    public GeneByAttributeDownloader(MGXDTOMaster dtomaster, RESTAccessI rab, AttributeDTOList attrs, SeqWriterI<? extends DNASequenceI> writer, boolean closeWriter, Set<String> seenGeneNames) {
        super(dtomaster, rab, writer, closeWriter);
        this.attrs = attrs;
        this.seenGeneNames = seenGeneNames;
    }

    @Override
    public boolean download() {

        String session_uuid;
        try {
            session_uuid = initTransfer();
        } catch (MGXServerException ex) {
            abortTransfer(ex.getMessage());
            return false;
        }

        boolean need_refetch = true;
        while (need_refetch) {
            int current_num_elements = 0;
            SequenceDTOList chunk;
            try {
                chunk = fetchChunk(session_uuid);
            } catch (MGXServerException ex) {
                abortTransfer(ex.getMessage());
                return false;
            }

            try {
                for (SequenceDTO dto : chunk.getSeqList()) {
                    DNASequenceI seq;
                    if (!dto.getQuality().isEmpty()) {
                        DNAQualitySequenceI qseq = new QualityDNASequence();
                        qseq.setQuality(dto.getQuality().toByteArray());
                        seq = qseq;
                    } else {
                        seq = new DNASequence();
                    }
                    if (dto.getId() != 0) {
                        seq.setId(dto.getId());
                    }

                    if (!seenGeneNames.contains(dto.getName())) {
                        seq.setName(dto.getName().getBytes());
                        seq.setSequence(dto.getSequence().getBytes());
                        writer.addSequence(seq);
                        current_num_elements++;
                        
                        seenGeneNames.add(dto.getName());
                    }
                }

            } catch (SeqStoreException sse) {
                abortTransfer(sse.getMessage());
                return false;
            }
            total_elements += current_num_elements;
            fireTaskChange(TransferBase.NUM_ELEMENTS_TRANSFERRED, total_elements);

            //
            // completeness flag indicates last chunk
            //
            if (chunk.getComplete()) {
                need_refetch = false;
            }
        }

        // finish the transfer
        try {
            finishTransfer(session_uuid);
        } catch (MGXServerException ex) {
            //Logger.getLogger(SeqDownloader.class.getName()).log(Level.SEVERE, null, ex);
            abortTransfer(ex.getMessage());
            return false;
        }

        if (closeWriter) {
            try {
                writer.close();
            } catch (Exception ex) {
                abortTransfer(ex.getMessage());
                return false;
            }
        }
        fireTaskChange(TransferBase.TRANSFER_COMPLETED, 1);
        return true;
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
