package de.cebitec.mgx.client.access.rest;

import de.cebitec.gpms.rest.RESTAccessI;
import de.cebitec.mgx.client.exception.MGXClientException;
import de.cebitec.mgx.client.exception.MGXDTOException;
import de.cebitec.mgx.dto.dto.ObservationDTO;
import de.cebitec.mgx.dto.dto.ObservationDTOList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.UUID;

/**
 *
 * @author sj
 */
public class ObservationAccess extends AccessBase<ObservationDTO, ObservationDTOList> {

    public ObservationAccess(RESTAccessI restAccess) {
        super(restAccess);
    }

    public Iterator<ObservationDTO> ByRead(long seqId) throws MGXDTOException {
        return get(ObservationDTOList.class, r.resolve(ObservationDTO.class, "byRead", String.valueOf(seqId))).getObservationList().iterator();
    }

    @Override
    public ObservationDTO fetch(long id) throws MGXDTOException {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public Iterator<ObservationDTO> fetchall() throws MGXDTOException {
        throw new UnsupportedOperationException("Not supported.");
    }

    public void create(long seqId, long attrId, ObservationDTO dto) throws MGXDTOException {
        if (dto == null) {
            throw new MGXClientException("Cannot create null object.");
        }
        String[] resolve = r.resolve(ObservationDTO.class, "create");
        resolve = Arrays.copyOf(resolve, resolve.length + 2);
        resolve[resolve.length - 2] = String.valueOf(seqId);
        resolve[resolve.length - 1] = String.valueOf(attrId);
        put(dto, resolve);
    }

    @Override
    public void update(ObservationDTO t) throws MGXDTOException {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public UUID delete(long id) throws MGXDTOException {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public long create(ObservationDTO t) throws MGXDTOException {
        throw new UnsupportedOperationException("Not supported.");
    }
}
