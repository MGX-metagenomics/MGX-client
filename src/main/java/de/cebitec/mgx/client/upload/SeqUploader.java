package de.cebitec.mgx.client.upload;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.dto.dto.SequenceDTO;
import de.cebitec.mgx.dto.dto.SequenceDTOList;
import de.cebitec.mgx.dto.dto.SequenceDTOList.Builder;
import de.cebitec.mgx.sequence.DNASequenceI;
import de.cebitec.mgx.sequence.SeqReaderI;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author sj
 */
public class SeqUploader extends UploadBase {

    private WebResource wr;
    private long seqrun_id;
    private SeqReaderI reader = null;
    private static int DEFAULT_CHUNK_SIZE = 500;
    private int chunk_size = DEFAULT_CHUNK_SIZE;
    private int total_elements = 0;
    private final PropertyChangeSupport pcs;

    public SeqUploader(WebResource wr, long seqrun_id, SeqReaderI reader) {
        this.wr = wr;
        this.seqrun_id = seqrun_id;
        this.reader = reader;
        pcs = new PropertyChangeSupport(this);
    }
    
    public void setChunkSize(int i) {
        chunk_size = i;
    }

    @Override
    public boolean upload() {
        CallbackI cb = getProgressCallback();
        int current_num_elements = 0;

        String session_uuid;
        try {
            session_uuid = initTransfer(seqrun_id);
        } catch (MGXServerException ex) {
            abortTransfer(ex.getMessage());
            return false;
        }

        cb.callback(total_elements);
        Builder seqListBuilder = de.cebitec.mgx.dto.dto.SequenceDTOList.newBuilder();
        while (reader.hasMoreElements()) {
            DNASequenceI nextElement = reader.nextElement();
            SequenceDTO seq = SequenceDTO.newBuilder().setName(new String(nextElement.getName())).setSequence(new String(nextElement.getSequence())).build();
            seqListBuilder.addSeq(seq);
            current_num_elements++;

            if (current_num_elements >= chunk_size) {
                total_elements += current_num_elements;
                cb.callback(total_elements);
                try {
                    sendChunk(seqListBuilder.build(), session_uuid);
                } catch (MGXServerException ex) {
                    abortTransfer(ex.getMessage());
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
                abortTransfer(ex.getMessage());
                return false;
            }
            cb.callback(total_elements);
        }
        try {
            finishTransfer(session_uuid);
        } catch (MGXServerException ex) {
            abortTransfer(ex.getMessage());
            return false;
        }
        return true;
    }

    public int getNumSequencesSent() {
        return total_elements;
    }

    private String initTransfer(long seqrun_id) throws MGXServerException {
        // FIXME use MGXString and application/protobuf instead
        ClientResponse res = wr.path("/Sequence/init/" + seqrun_id).accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
        catchException(res);
        String session_uuid = res.getEntity(String.class);
        return session_uuid;
    }

    private void finishTransfer(String uuid) throws MGXServerException {
        ClientResponse res = wr.path("/Sequence/close/" + uuid).get(ClientResponse.class);
        catchException(res);
        fireTaskChange();
    }

    private void abortTransfer(String reason) {
        setErrorMessage(reason);
        fireTaskChange();
        // FIXME
    }

    private void sendChunk(SequenceDTOList seqList, String session_uuid) throws MGXServerException {
        ClientResponse res = wr.path("/Sequence/add/" + session_uuid).type("application/x-protobuf").post(ClientResponse.class, seqList);
        catchException(res);
        fireTaskChange();
    }

    public void addPropertyChangeListener(PropertyChangeListener p) {
        pcs.addPropertyChangeListener(p);
    }

    public void removePropertyChangeListener(PropertyChangeListener p) {
        pcs.removePropertyChangeListener(p);
    }

    private void fireTaskChange() {
        pcs.firePropertyChange("PROP_CHANGED", 0, 1);
    }
}
