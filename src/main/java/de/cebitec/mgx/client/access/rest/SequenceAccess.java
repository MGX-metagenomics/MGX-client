package de.cebitec.mgx.client.access.rest;

import de.cebitec.mgx.client.exception.MGXClientException;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.client.upload.SeqUploader;
import de.cebitec.mgx.dto.dto.SequenceDTO;
import de.cebitec.mgx.dto.dto.SequenceDTOList;
import de.cebitec.mgx.sequence.SeqReaderI;
import java.util.Collection;

/**
 *
 * @author sjaenick
 */
public class SequenceAccess extends AccessBase<SequenceDTO, SequenceDTOList> {

    public void sendSequences(long seqrun_id, SeqReaderI reader) throws MGXServerException {
        SeqUploader seqUploader = new SeqUploader(getWebResource(), seqrun_id, reader);
        boolean success = seqUploader.upload();
        if (!success) {
            throw new MGXServerException(seqUploader.getErrorMessage());
        }
    }
    
    public SeqUploader createUploader(long seqrun_id, SeqReaderI reader) {
        return new SeqUploader(getWebResource(), seqrun_id, reader);
    }
        
    @Override
    public SequenceDTO fetch(long id) throws MGXServerException, MGXClientException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Collection<SequenceDTO> fetchall() throws MGXServerException, MGXClientException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public long create(SequenceDTO t) throws MGXServerException, MGXClientException {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public void update(SequenceDTO t) throws MGXServerException, MGXClientException {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public void delete(long id) throws MGXServerException, MGXClientException {
        throw new UnsupportedOperationException("Not supported.");
    }
}
