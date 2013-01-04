package de.cebitec.mgx.client.datatransfer;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.dto.dto.MGXString;
import de.cebitec.mgx.dto.dto.SequenceDTO;
import de.cebitec.mgx.dto.dto.SequenceDTOList;
import de.cebitec.mgx.seqstorage.DNASequence;
import de.cebitec.mgx.sequence.DNASequenceI;
import de.cebitec.mgx.sequence.SeqWriterI;
import java.io.IOException;

/**
 *
 * @author sjaenick
 */
public class SeqDownloader extends DownloadBase {

    private final WebResource wr;
    private final long seqrun_id;
    private final SeqWriterI<DNASequenceI> writer;
    private long total_elements = 0;

    public SeqDownloader(WebResource wr, long seqrun_id, SeqWriterI<DNASequenceI> writer) {
        super();
        this.wr = wr;
        this.seqrun_id = seqrun_id;
        this.writer = writer;
    }

    @Override
    public boolean download() {
        CallbackI cb = getProgressCallback();

        String session_uuid;
        try {
            session_uuid = initTransfer(seqrun_id);
        } catch (MGXServerException ex) {
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
                    abortTransfer(ex.getMessage(), total_elements + current_num_elements);
                    return false;
                }
                current_num_elements++;
            }
            total_elements += current_num_elements;
            cb.callback(total_elements);
            fireTaskChange(total_elements);
        }
        
        
        // finish the transfer
        
        try {
            finishTransfer(session_uuid);
        } catch (MGXServerException ex) {
            abortTransfer(ex.getMessage(), total_elements);
            return false;
        }
        return true;
    }

    private String initTransfer(long seqrun_id) throws MGXServerException {
        ClientResponse res = wr.path("/Sequence/initDownload/" + seqrun_id).accept("application/x-protobuf").get(ClientResponse.class);
        catchException(res);
        fireTaskChange(total_elements);
        MGXString session_uuid = res.<MGXString>getEntity(MGXString.class);
        return session_uuid.getValue();
    }

    private void finishTransfer(String uuid) throws MGXServerException {
        ClientResponse res = wr.path("/Sequence/closeDownload/" + uuid).get(ClientResponse.class);
        catchException(res);
        fireTaskChange(total_elements);
    }

    private SequenceDTOList fetchChunk(String session_uuid) throws MGXServerException {
        ClientResponse res = wr.path("/Sequence/fetchSequences/" + session_uuid).type("application/x-protobuf").get(ClientResponse.class);
        catchException(res);
        SequenceDTOList entity = res.<SequenceDTOList>getEntity(SequenceDTOList.class);
        fireTaskChange(total_elements);
        return entity;
    }
}
