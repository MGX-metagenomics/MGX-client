package de.cebitec.mgx.client.access.rest;

import de.cebitec.mgx.client.exception.MGXClientException;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.dto.dto.ObservationDTO;
import de.cebitec.mgx.dto.dto.ObservationDTOList;
import java.util.Iterator;
import java.util.UUID;

/**
 *
 * @author sj
 */
public class ObservationAccess extends AccessBase<ObservationDTO, ObservationDTOList> {

    public Iterator<ObservationDTO> ByRead(long seqId) throws MGXServerException, MGXClientException {
        return get(ObservationDTOList.class, r.resolve(ObservationDTO.class, "byRead", String.valueOf(seqId))).getObservationList().iterator();
    }

    @Override
    public ObservationDTO fetch(long id) throws MGXServerException, MGXClientException {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public Iterator<ObservationDTO> fetchall() throws MGXServerException, MGXClientException {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public long create(ObservationDTO t) throws MGXServerException, MGXClientException {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public void update(ObservationDTO t) throws MGXServerException, MGXClientException {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public UUID delete(long id) throws MGXServerException, MGXClientException {
        throw new UnsupportedOperationException("Not supported.");
    }
}
