package de.cebitec.mgx.client.datatransfer;

import de.cebitec.gpms.rest.RESTAccessI;
import de.cebitec.mgx.client.MGXDTOMaster;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.dto.dto.MGXString;
import de.cebitec.mgx.dto.dto.SequenceDTO;
import de.cebitec.mgx.dto.dto.SequenceDTOList;
import de.cebitec.mgx.seqstorage.DNASequence;
import de.cebitec.mgx.seqstorage.QualityDNASequence;
import de.cebitec.mgx.sequence.DNAQualitySequenceI;
import de.cebitec.mgx.sequence.DNASequenceI;
import de.cebitec.mgx.sequence.SeqStoreException;
import de.cebitec.mgx.sequence.SeqWriterI;
import java.awt.EventQueue;
import javax.net.ssl.SSLHandshakeException;
import javax.ws.rs.ProcessingException;

/**
 *
 * @author sjaenick
 */
public class SeqDownloader extends DownloadBase {

    private long seqrun_id = -1;
    protected final SeqWriterI<DNASequenceI> writer;
    protected volatile long total_elements = 0;
    protected final boolean closeWriter;

    public SeqDownloader(MGXDTOMaster dtomaster, RESTAccessI rab, long seqrun_id, SeqWriterI<? extends DNASequenceI> writer, boolean closeWriter) {
        this(dtomaster, rab, writer, closeWriter);
        this.seqrun_id = seqrun_id;
    }

    @SuppressWarnings("unchecked")
    protected SeqDownloader(MGXDTOMaster dtomaster, RESTAccessI rab, SeqWriterI<? extends DNASequenceI> writer, boolean closeWriter) {
        super(dtomaster, rab);
        this.writer = (SeqWriterI<DNASequenceI>) writer;
        this.closeWriter = closeWriter;
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
                    seq.setName(dto.getName().getBytes());
                    seq.setSequence(dto.getSequence().getBytes());
                    writer.addSequence(seq);
                    current_num_elements++;
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

    protected String initTransfer() throws MGXServerException {
        try {
            MGXString session_uuid = super.get(MGXString.class, "Sequence", "initDownload", String.valueOf(seqrun_id));
            fireTaskChange(TransferBase.NUM_ELEMENTS_TRANSFERRED, total_elements);
            return session_uuid.getValue();
        } catch (ProcessingException ex) {
            if (ex.getCause() != null && ex.getCause() instanceof SSLHandshakeException) {
                return initTransfer(); // retry
            }
        }
        return null;
    }

    protected void finishTransfer(String uuid) throws MGXServerException {
        assert !EventQueue.isDispatchThread();
        try {
            super.get("Sequence", "closeDownload", uuid);
            fireTaskChange(TransferBase.NUM_ELEMENTS_TRANSFERRED, total_elements);
        } catch (ProcessingException ex) {
            if (ex.getCause() != null && ex.getCause() instanceof SSLHandshakeException) {
                finishTransfer(uuid);
            }
        }
    }

    protected SequenceDTOList fetchChunk(String session_uuid) throws MGXServerException {
        assert !EventQueue.isDispatchThread();
        try {
            SequenceDTOList entity = super.get(SequenceDTOList.class, "Sequence", "fetchSequences", session_uuid);
            return entity;
        } catch (ProcessingException ex) {
            if (ex.getCause() != null && ex.getCause() instanceof SSLHandshakeException) {
                return fetchChunk(session_uuid);
            }
        }
        return null;
    }

    @Override
    public long getProgress() {
        return total_elements;
    }
}
