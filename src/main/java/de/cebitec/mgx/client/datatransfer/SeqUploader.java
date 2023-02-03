package de.cebitec.mgx.client.datatransfer;

import com.google.protobuf.ByteString;
import de.cebitec.gpms.rest.RESTAccessI;
import de.cebitec.mgx.client.MGXDTOMaster;
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
import javax.ws.rs.ProcessingException;

/**
 *
 * @author sj
 */
public class SeqUploader extends UploadBase {

    private final long seqrun_id;
    private final SeqReaderI<? extends DNASequenceI> reader;
    private volatile long total_elements = 0;
    private long duration;

    private final static int BASE_PAIR_LIMIT = 2_000_000;

    public SeqUploader(MGXDTOMaster dtomaster, RESTAccessI rab, long seqrun_id, SeqReaderI<? extends DNASequenceI> reader) {
        super(dtomaster, rab);
        this.seqrun_id = seqrun_id;
        this.reader = reader;
        // add in some randomness to make the numbers appear "nicer"
        // generate an even number so interleaved paired-end uploads
        // stay in the correct order
        int randomNess = (int) Math.round(Math.random() * 20);
        if (randomNess % 2 == 1) {
            randomNess++;
        }
        super.setChunkSize(5_000 + randomNess);
        duration = System.currentTimeMillis();
    }

    @Override
    public boolean upload() {
        int current_num_elements = 0;
        int current_bp = 0;

        String session_uuid;
        try {
            session_uuid = initTransfer();
        } catch (MGXServerException ex) {
            abortTransfer(ex.getMessage());
            return false;
        }

        Builder seqListBuilder = SequenceDTOList.newBuilder();

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
                current_bp += nextElement.getSequence().length;

                // if number of sequences exceeds chunk size or bp in chunk
                // exceeds base pair limit, send chunk to server
                if (current_num_elements >= getChunkSize() || (current_num_elements % 2 == 0 && current_bp >= BASE_PAIR_LIMIT)) {
                    total_elements += current_num_elements;
                    try {
                        seqListBuilder.setComplete(!reader.hasMoreElements());
                        sendChunk(seqListBuilder.build(), session_uuid);
                    } catch (MGXServerException ex) {
                        abortTransfer(ex.getMessage());
                        return false;
                    }
                    current_num_elements = 0;
                    current_bp = 0;
                    seqListBuilder = SequenceDTOList.newBuilder();
                    seqListBuilder.setComplete(true);
                }
            }
        } catch (SeqStoreException ex) {
            abortTransfer(ex.getMessage());
            return false;
        }

        if (current_num_elements > 0) {
            total_elements += current_num_elements;
            try {
                seqListBuilder.setComplete(true);
                sendChunk(seqListBuilder.build(), session_uuid);
            } catch (MGXServerException ex) {
                abortTransfer(ex.getMessage());
                return false;
            }
        }
        try {
            finishTransfer(session_uuid);
        } catch (MGXServerException ex) {
            abortTransfer(ex.getMessage());
            return false;
        }
        return true;
    }

    @Override
    public long getProgress() {
        return total_elements;
    }

    private String initTransfer() throws MGXServerException {
        try {
            MGXString session_uuid = super.get(MGXString.class, "Sequence", "initUpload", String.valueOf(seqrun_id), String.valueOf(reader.hasQuality()));
            fireTaskChange(TransferBase.NUM_ELEMENTS_TRANSFERRED, total_elements);
            return session_uuid.getValue();
        } catch (ProcessingException ex) {
            if (ex.getCause() != null && ex.getCause() instanceof SSLHandshakeException) {
                return initTransfer(); // retry
            } else {
                throw ex; // rethrow
            }
        }
    }

    private void finishTransfer(final String uuid) throws MGXServerException {
        try {
            super.get("Sequence", "closeUpload", uuid);
        } catch (ProcessingException ex) {
            if (ex.getCause() != null && ex.getCause() instanceof SSLHandshakeException) {
                finishTransfer(uuid); // retry
            } else {
                throw ex; // rethrow
            }
        }
        fireTaskChange(TransferBase.NUM_ELEMENTS_TRANSFERRED, total_elements);
        fireTaskChange(TransferBase.TRANSFER_COMPLETED, total_elements);
        duration = System.currentTimeMillis() - duration;
        System.out.println("transfer done in "+duration + "ms");
    }

    private void sendChunk(final SequenceDTOList seqList, final String session_uuid) throws MGXServerException {
        super.post(seqList, "Sequence", "add", session_uuid);
        fireTaskChange(TransferBase.NUM_ELEMENTS_TRANSFERRED, total_elements);
    }

}
