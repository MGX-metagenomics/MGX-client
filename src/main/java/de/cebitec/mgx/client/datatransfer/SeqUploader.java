package de.cebitec.mgx.client.datatransfer;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.dto.dto.MGXString;
import de.cebitec.mgx.dto.dto.SequenceDTO;
import de.cebitec.mgx.dto.dto.SequenceDTOList;
import de.cebitec.mgx.dto.dto.SequenceDTOList.Builder;
import de.cebitec.mgx.seqholder.ReadSequenceI;
import de.cebitec.mgx.sequence.SeqReaderI;

/**
 *
 * @author sj
 */
public class SeqUploader extends UploadBase {

    private final WebResource wr;
    private final long seqrun_id;
    private final SeqReaderI<? extends ReadSequenceI> reader;
    private long total_elements = 0;

    public SeqUploader(WebResource wr, long seqrun_id, SeqReaderI<? extends ReadSequenceI> reader) {
        super();
        this.wr = wr;
        this.seqrun_id = seqrun_id;
        this.reader = reader;
        // add in some randomness to make the numbers appear "nicer"
        int randomNess = (int) Math.round(Math.random()*20);
        setChunkSize(800 + randomNess);
    }

    @Override
    public boolean upload() {
        CallbackI cb = getProgressCallback();
        int current_num_elements = 0;

        String session_uuid;
        try {
            session_uuid = initTransfer(seqrun_id);
        } catch (MGXServerException ex) {
            abortTransfer(ex.getMessage(), total_elements);
            return false;
        }

        cb.callback(total_elements);
        Builder seqListBuilder = de.cebitec.mgx.dto.dto.SequenceDTOList.newBuilder();
        while (reader.hasMoreElements()) {
            ReadSequenceI nextElement = reader.nextElement();

            // ignore empty sequences
            if (nextElement.getSequence().getSequence().length == 0) {
                continue;
            }
            
            SequenceDTO seq = nextElement.toDTO();
            seqListBuilder.addSeq(seq);
            current_num_elements++;

            if (current_num_elements >= chunk_size) {
                total_elements += current_num_elements;
                cb.callback(total_elements);
                try {
                    sendChunk(seqListBuilder.build(), session_uuid);
                    cb.callback(total_elements);
                } catch (MGXServerException ex) {
                    abortTransfer(ex.getMessage(), total_elements);
                    return false;
                }
                current_num_elements = 0;
                seqListBuilder = de.cebitec.mgx.dto.dto.SequenceDTOList.newBuilder();
            }
        }
        if (current_num_elements > 0) {
            total_elements += current_num_elements;
            try {
                sendChunk(seqListBuilder.build(), session_uuid);
            } catch (MGXServerException ex) {
                abortTransfer(ex.getMessage(), total_elements);
                return false;
            }
            cb.callback(total_elements);
        }
        try {
            finishTransfer(session_uuid);
        } catch (MGXServerException ex) {
            abortTransfer(ex.getMessage(), total_elements);
            return false;
        }
        return true;
    }

    public long getNumElementsSent() {
        return total_elements;
    }

    private String initTransfer(long seqrun_id) throws MGXServerException {
        ClientResponse res = wr.path("/Sequence/initUpload/" + seqrun_id).accept("application/x-protobuf").get(ClientResponse.class);
        catchException(res);
        fireTaskChange(total_elements);
        MGXString session_uuid = res.<MGXString>getEntity(MGXString.class);
        return session_uuid.getValue();
    }

    private void finishTransfer(String uuid) throws MGXServerException {
        ClientResponse res = wr.path("/Sequence/closeUpload/" + uuid).get(ClientResponse.class);
        catchException(res);
        fireTaskChange(total_elements);
    }

    private void sendChunk(SequenceDTOList seqList, String session_uuid) throws MGXServerException {
        ClientResponse res = wr.path("/Sequence/add/" + session_uuid).type("application/x-protobuf").post(ClientResponse.class, seqList);
        catchException(res);
        fireTaskChange(total_elements);
    }
}
