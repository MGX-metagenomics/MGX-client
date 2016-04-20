package de.cebitec.mgx.client.access.rest;

import de.cebitec.gpms.rest.RESTAccessI;
import de.cebitec.gpms.rest.RESTException;
import static de.cebitec.mgx.client.access.rest.AccessBase.r;
import de.cebitec.mgx.client.exception.MGXClientException;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.dto.dto;
import de.cebitec.mgx.dto.dto.MGXLong;
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

    public void create(long seqId, ObservationDTO dto) throws MGXServerException, MGXClientException {
        if (dto == null) {
            throw new MGXClientException("Cannot create null object.");
        }
        String[] resolve = r.resolve(ObservationDTO.class, "create");
        resolve = Arrays.copyOf(resolve, resolve.length + 1);
        resolve[resolve.length - 1] = String.valueOf(seqId);
        put(dto, resolve);
    }

    @Override
    public void update(ObservationDTO t) throws MGXServerException, MGXClientException {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public UUID delete(long id) throws MGXServerException, MGXClientException {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public long create(ObservationDTO t) throws MGXServerException, MGXClientException {
        throw new UnsupportedOperationException("Not supported.");
    }
}
