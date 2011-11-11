package de.cebitec.mgx.client.access.rest;

import de.cebitec.mgx.client.exception.MGXServerException;
import com.sun.jersey.api.client.ClientResponse;
import de.cebitec.mgx.dto.SequenceDTO;
import de.cebitec.mgx.dto.SequenceDTOList;
import de.cebitec.mgx.dto.SequenceDTOList.Builder;
import de.cebitec.mgx.sequence.DNASequenceI;
import de.cebitec.mgx.sequence.SeqReaderI;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author sjaenick
 */
public class SequenceAccess extends AccessBase<SequenceDTO, SequenceDTOList> {

    public void sendSequences(long seqrun_id, SeqReaderI reader) throws MGXServerException {
        ClientResponse res = getWebResource().path("/Sequence/init/" + seqrun_id).accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
        catchException(res);
        String session_uuid = res.getEntity(String.class);

        Builder seqListBuilder = de.cebitec.mgx.dto.SequenceDTOList.newBuilder();
        int num_elements = 0;
        while (reader.hasMoreElements()) {
            DNASequenceI nextElement = reader.nextElement();
            SequenceDTO seq = SequenceDTO.newBuilder().setName(new String(nextElement.getName())).setSequence(new String(nextElement.getSequence())).build();
            seqListBuilder.addSeq(seq);
            num_elements++;

            if (num_elements >= 5000) {
                System.err.println("sending chunk with " + num_elements + " seqs");
                sendChunk(seqListBuilder.build(), session_uuid);
                num_elements = 0;
                seqListBuilder = de.cebitec.mgx.dto.SequenceDTOList.newBuilder();
            }
        }
        if (num_elements > 0) {
            System.err.println("sending chunk with " + num_elements + " seqs");
            sendChunk(seqListBuilder.build(), session_uuid);
        }
        res = getWebResource().path("/Sequence/close/" + session_uuid).get(ClientResponse.class);
        catchException(res);

    }

    private void sendChunk(SequenceDTOList seqList, String session_uuid) throws MGXServerException {
        ClientResponse res = getWebResource().path("/Sequence/add/" + session_uuid).type("application/x-protobuf").post(ClientResponse.class, seqList);
        catchException(res);
    }
}
