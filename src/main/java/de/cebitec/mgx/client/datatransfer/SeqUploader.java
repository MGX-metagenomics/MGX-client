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
    private long total_elements = 0;

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
        super.setChunkSize(10_000 + randomNess);
    }

    @Override
    public boolean upload() {
        int current_num_elements = 0;

        String session_uuid;
        try {
            session_uuid = initTransfer();
        } catch (MGXServerException ex) {
            abortTransfer(ex.getMessage());
            return false;
        }

        Builder seqListBuilder = SequenceDTOList.newBuilder();
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

                if (current_num_elements >= getChunkSize()) {
                    total_elements += current_num_elements;
                    try {
                        sendChunk(seqListBuilder.build(), session_uuid);
                    } catch (MGXServerException ex) {
                        abortTransfer(ex.getMessage());
                        return false;
                    }
                    current_num_elements = 0;
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

//        while (!pendingRequests.isEmpty()) {
//
//            List<AsyncRequestHandleI> toRemove = new ArrayList<>();
//
//            for (AsyncRequestHandleI arh : pendingRequests) {
//                if (arh.isDone()) {
//                    try {
//                        // isSuccess always returns true or throws exception
//                        boolean success = arh.isSuccess();
//                        if (!success) {
//                            throw new MGXServerException("Not reached.");
//                        }
//                        toRemove.add(arh);
//                    } catch (RESTException ex) {
//                        throw new MGXServerException(ex.getMessage());
//                    }
//                }
//            }
//            pendingRequests.removeAll(toRemove);
//        }

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
    }

    private void sendChunk(final SequenceDTOList seqList, final String session_uuid) throws MGXServerException {
//        //
//        // remove finished requests from queue
//        //
//        List<AsyncRequestHandleI> toRemove = new ArrayList<>();
//
//        for (AsyncRequestHandleI arh : pendingRequests) {
//            if (arh.isDone()) {
//                // isSuccess always returns true or throws exception
//                try {
//                    boolean success = arh.isSuccess();
//                    if (!success) {
//                        throw new MGXServerException("Not reached.");
//                    }
//                    toRemove.add(arh);
//                    fireTaskChange(TransferBase.NUM_ELEMENTS_TRANSFERRED, total_elements);
//                } catch (RESTException ex) {
//                    throw new MGXServerException(ex.getMessage());
//                }
//
//            }
//        }
//        pendingRequests.removeAll(toRemove);
//        toRemove.clear();
//
//        //
//        // if queue is full, need to await completion of at least one request
//        //
//        while (pendingRequests.size() >= 40) {
//            for (AsyncRequestHandleI arh : pendingRequests) {
//                try {
//                    // isSuccess always returns true or throws exception
//                    boolean success = arh.isSuccess();
//                    if (!success) {
//                        throw new MGXServerException("Not reached.");
//                    }
//                    toRemove.add(arh);
//                    fireTaskChange(TransferBase.NUM_ELEMENTS_TRANSFERRED, total_elements);
//                } catch (RESTException ex) {
//                    throw new MGXServerException(ex.getMessage());
//                }
//            }
//
//            pendingRequests.removeAll(toRemove);
//            toRemove.clear();
//        }
        
        super.post(seqList, "Sequence", "add", session_uuid);
        fireTaskChange(TransferBase.NUM_ELEMENTS_TRANSFERRED, total_elements);

        //AsyncRequestHandleI handle = super.postAsync(seqList, "Sequence", "add", session_uuid);
        //pendingRequests.add(handle);

    }

//    private final Set<AsyncRequestHandleI> pendingRequests = new HashSet<>(40);
}
