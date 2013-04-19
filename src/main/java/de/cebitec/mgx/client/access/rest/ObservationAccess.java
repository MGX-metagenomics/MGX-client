package de.cebitec.mgx.client.access.rest;

import de.cebitec.mgx.client.exception.MGXClientException;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.dto.dto.ObservationDTO;
import de.cebitec.mgx.dto.dto.ObservationDTOList;
import java.util.Collection;
import java.util.Iterator;

/**
 *
 * @author sj
 */
public class ObservationAccess extends AccessBase<ObservationDTO, ObservationDTOList> {

    public Iterator<ObservationDTO> ByRead(long seqId) throws MGXServerException, MGXClientException {
        return get(r.resolve(ObservationDTO.class, "byRead") + seqId, ObservationDTOList.class).getObservationList().iterator();
    }

    @Override
    public ObservationDTO fetch(long id) throws MGXServerException, MGXClientException {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public Collection<ObservationDTO> fetchall() throws MGXServerException, MGXClientException {
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
    public boolean delete(long id) throws MGXServerException, MGXClientException {
        throw new UnsupportedOperationException("Not supported.");
    }
}
