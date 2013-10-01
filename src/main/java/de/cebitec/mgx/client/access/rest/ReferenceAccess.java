package de.cebitec.mgx.client.access.rest;

import de.cebitec.mgx.client.exception.MGXClientException;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.dto.dto.MGXLong;
import de.cebitec.mgx.dto.dto.ReferenceDTO;
import de.cebitec.mgx.dto.dto.ReferenceDTOList;
import de.cebitec.mgx.dto.dto.RegionDTO;
import de.cebitec.mgx.dto.dto.RegionDTOList;
import java.util.Iterator;
import java.util.UUID;

/**
 *
 * @author belmann
 */
public class ReferenceAccess extends AccessBase<ReferenceDTO, ReferenceDTOList> {

    @Override
    public Iterator<ReferenceDTO> fetchall() throws MGXServerException, MGXClientException {
        return fetchlist(ReferenceDTOList.class).getReferenceList().iterator();
    }

    @Override
    public long create(ReferenceDTO sr) throws MGXServerException, MGXClientException {
        return super.create(sr, ReferenceDTO.class);
    }

    @Override
    public void update(ReferenceDTO d) throws MGXServerException, MGXClientException {
        super.update(d, ReferenceDTO.class);
    }

    public long installGlobalReference(long id) throws MGXServerException {
        return get("/Reference/installGlobalReference/" + id, MGXLong.class).getValue();
    }
    
    @Override
    public UUID delete(long id) throws MGXServerException, MGXClientException {
        return super.delete(id, ReferenceDTO.class);
    }

    @Override
    public ReferenceDTO fetch(long id) throws MGXServerException, MGXClientException {
        return super.fetch(id, ReferenceDTO.class);
    }

    public Iterator<ReferenceDTO> listGlobalReferences() throws MGXServerException {
        return get("/Reference/listGlobalReferences", ReferenceDTOList.class).getReferenceList().iterator();
    }
    
    
    public Iterator<RegionDTO> byReferenceInterval(long id, int from, int to) throws MGXClientException, MGXServerException {
        return get(r.resolve(RegionDTO.class, "byReferenceInterval") + id, RegionDTOList.class).getRegionList().iterator();
    }
}
