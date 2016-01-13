package de.cebitec.mgx.client.datatransfer;

import com.google.protobuf.ByteString;
import com.sun.jersey.api.client.ClientHandlerException;
import de.cebitec.gpms.rest.RESTAccessI;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.dto.dto.MGXString;
import de.cebitec.mgx.dto.dto.SequenceDTO;
import de.cebitec.mgx.dto.dto.SequenceDTOList;
import de.cebitec.mgx.dto.dto.SequenceDTOList.Builder;
import de.cebitec.mgx.sequence.DNAQualitySequenceI;
import de.cebitec.mgx.sequence.DNASequenceI;
import de.cebitec.mgx.sequence.SeqReaderI;
import de.cebitec.mgx.sequence.SeqStoreException;
import javax.net.ssl.SSLHandshakeException;

/**
 *
 * @author sj
 */
public class SeqUploader extends UploadBase {

    private final long seqrun_id;
    private final SeqReaderI<? extends DNASequenceI> reader;
    private long total_elements = 0;

    public SeqUploader(RESTAccessI rab, long seqrun_id, SeqReaderI<? extends DNASequenceI> reader) {
        super(rab);
        this.seqrun_id = seqrun_id;
        this.reader = reader;
        // add in some randomness to make the numbers appear "nicer"
        int randomNess = (int) Math.round(Math.random() * 20);
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
        seqListBuilder.setComplete(true);

        try {
            while (reader.hasMoreElements()) {
                DNASequenceI nextElement = reader.nextElement();

                // ignore empty sequences
                if (nextElement.getSequence().length == 0) {
                    continue;
                }

                SequenceDTO.Builder seqbuilder = SequenceDTO.newBuilder()
                        .setName(new String(nextElement.getName()))
                        .setSequence(new String(nextElement.getSequence()));

                if (nextElement instanceof DNAQualitySequenceI) {
                    DNAQualitySequenceI q = (DNAQualitySequenceI) nextElement;
                    seqbuilder = seqbuilder.setQuality(ByteString.copyFrom(q.getQuality()));
                }
                seqListBuilder.addSeq(seqbuilder.build());
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
                    seqListBuilder.setComplete(true);
                }
            }
        } catch (SeqStoreException ex) {
            abortTransfer(ex.getMessage(), total_elements);
            return false;
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

    @Override
    public long getProgress() {
        return total_elements;
    }

    private String initTransfer(long seqrun_id) throws MGXServerException {
        try {
            MGXString session_uuid = super.get(MGXString.class, "Sequence", "initUpload", String.valueOf(seqrun_id), String.valueOf(reader.hasQuality()));
            fireTaskChange(TransferBase.NUM_ELEMENTS_TRANSFERRED, total_elements);
            return session_uuid.getValue();
        } catch (ClientHandlerException ex) {
            if (ex.getCause() != null && ex.getCause() instanceof SSLHandshakeException) {
                return initTransfer(seqrun_id); // retry
            } else {
                throw ex; // rethrow
            }
        }
    }

    private void finishTransfer(String uuid) throws MGXServerException {
        try {
            super.get("Sequence", "closeUpload", uuid);
        } catch (ClientHandlerException ex) {
            if (ex.getCause() != null && ex.getCause() instanceof SSLHandshakeException) {
                finishTransfer(uuid); // retry
            } else {
                throw ex; // rethrow
            }
        }
        fireTaskChange(TransferBase.NUM_ELEMENTS_TRANSFERRED, total_elements);
        fireTaskChange(TransferBase.TRANSFER_COMPLETED, total_elements);
    }

    private void sendChunk(SequenceDTOList seqList, String session_uuid) throws MGXServerException {
        try {
            super.post(seqList, "Sequence", "add", session_uuid);
            fireTaskChange(TransferBase.NUM_ELEMENTS_TRANSFERRED, total_elements);
        } catch (ClientHandlerException ex) {
            if (ex.getCause() != null && ex.getCause() instanceof SSLHandshakeException) {
                sendChunk(seqList, session_uuid); // retry
            } else {
                throw ex; // rethrow
            }
        }
    }
}
