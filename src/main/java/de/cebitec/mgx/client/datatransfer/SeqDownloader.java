package de.cebitec.mgx.client.datatransfer;

import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.dto.dto.MGXString;
import de.cebitec.mgx.dto.dto.SequenceDTO;
import de.cebitec.mgx.dto.dto.SequenceDTOList;
import de.cebitec.mgx.seqstorage.DNASequence;
import de.cebitec.mgx.sequence.DNASequenceI;
import de.cebitec.mgx.sequence.SeqWriterI;
import java.awt.EventQueue;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.SSLHandshakeException;

/**
 *
 * @author sjaenick
 */
public class SeqDownloader extends DownloadBase {

    protected final WebResource wr;
    private long seqrun_id = -1;
    private final SeqWriterI<DNASequenceI> writer;
    protected long total_elements = 0;

    public SeqDownloader(WebResource wr, long seqrun_id, SeqWriterI<DNASequenceI> writer) {
        this(wr, writer);
        this.seqrun_id = seqrun_id;

    }

    protected SeqDownloader(WebResource wr, SeqWriterI<DNASequenceI> writer) {
        super();
        this.wr = wr;
        this.writer = writer;
    }

    @Override
    public boolean download() {
        CallbackI cb = getProgressCallback();

        String session_uuid;
        try {
            session_uuid = initTransfer();
        } catch (MGXServerException ex) {
            Logger.getLogger(SeqDownloader.class.getName()).log(Level.SEVERE, null, ex);
            abortTransfer(ex.getMessage(), total_elements);
            return false;
        }

        cb.callback(total_elements);

        boolean need_refetch = true;
        while (need_refetch) {
            int current_num_elements = 0;
            SequenceDTOList chunk = null;
            try {
                chunk = fetchChunk(session_uuid);
            } catch (MGXServerException ex) {
                Logger.getLogger(SeqDownloader.class.getName()).log(Level.SEVERE, null, ex);
                try {
                    writer.close();
                } catch (Exception ex1) {
                }
                abortTransfer(ex.getMessage(), total_elements);
                return false;
            }

            // empty sequence list indicates end of download
            need_refetch = chunk.getSeqCount() > 0;

            for (SequenceDTO dto : chunk.getSeqList()) {
                DNASequenceI seq = new DNASequence();
                if (dto.hasId()) {
                    seq.setId(dto.getId());
                }
                seq.setName(dto.getName().getBytes());
                seq.setSequence(dto.getSequence().getBytes());
                try {
                    writer.addSequence(seq);
                } catch (IOException ex) {
                    Logger.getLogger(SeqDownloader.class.getName()).log(Level.SEVERE, null, ex);
                    abortTransfer(ex.getMessage(), total_elements + current_num_elements);
                    return false;
                }
                current_num_elements++;
            }
            total_elements += current_num_elements;
            cb.callback(total_elements);
            fireTaskChange(TransferBase.NUM_ELEMENTS_RECEIVED, total_elements);
        }

        // finish the transfer
        try {
            finishTransfer(session_uuid);
        } catch (MGXServerException ex) {
            Logger.getLogger(SeqDownloader.class.getName()).log(Level.SEVERE, null, ex);
            abortTransfer(ex.getMessage(), total_elements);
            return false;
        }
        try {
            writer.close();
        } catch (Exception ex) {
            Logger.getLogger(SeqDownloader.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }

    protected String initTransfer() throws MGXServerException {
        assert !EventQueue.isDispatchThread();
        try {
            ClientResponse res = wr.path("/Sequence/initDownload/" + seqrun_id).accept("application/x-protobuf").get(ClientResponse.class);
            catchException(res);
            fireTaskChange(TransferBase.NUM_ELEMENTS_RECEIVED, total_elements);
            MGXString session_uuid = res.<MGXString>getEntity(MGXString.class);
            return session_uuid.getValue();
        } catch (ClientHandlerException ex) {
            if (ex.getCause() != null && ex.getCause() instanceof SSLHandshakeException) {
                Logger.getLogger(SeqDownloader.class.getName()).log(Level.SEVERE, null, ex);
                return initTransfer();
            }
        }
        return null;
    }

    protected void finishTransfer(String uuid) throws MGXServerException {
        assert !EventQueue.isDispatchThread();
        try {
            ClientResponse res = wr.path("/Sequence/closeDownload/" + uuid).get(ClientResponse.class);
            catchException(res);
            fireTaskChange(TransferBase.NUM_ELEMENTS_RECEIVED, total_elements);
        } catch (ClientHandlerException ex) {
            if (ex.getCause() != null && ex.getCause() instanceof SSLHandshakeException) {
                Logger.getLogger(SeqDownloader.class.getName()).log(Level.SEVERE, null, ex);
                finishTransfer(uuid);
            }
        }
    }

    protected SequenceDTOList fetchChunk(String session_uuid) throws MGXServerException {
        assert !EventQueue.isDispatchThread();
        try {
            ClientResponse res = wr.path("/Sequence/fetchSequences/" + session_uuid).type("application/x-protobuf").get(ClientResponse.class);
            catchException(res);
            SequenceDTOList entity = res.<SequenceDTOList>getEntity(SequenceDTOList.class);
            fireTaskChange(TransferBase.NUM_ELEMENTS_RECEIVED, total_elements);
            return entity;
        } catch (ClientHandlerException ex) {
            if (ex.getCause() != null && ex.getCause() instanceof SSLHandshakeException) {
                Logger.getLogger(SeqDownloader.class.getName()).log(Level.SEVERE, null, ex);
                return fetchChunk(session_uuid);
            }
        }
        return null;
    }

    public long getProgress() {
        return total_elements;
    }
}
