
package de.cebitec.mgx.client.access.rest;

import de.cebitec.mgx.client.exception.MGXClientException;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.dto.dto.ReferenceDTO;
import de.cebitec.mgx.dto.dto.ReferenceDTOList;
import java.util.Iterator;
import java.util.UUID;

/**
 *
 * @author belmann
 */
public class ReferenceAccess extends AccessBase<ReferenceDTO, ReferenceDTOList>{
     
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

    @Override
    public UUID delete(long id) throws MGXServerException, MGXClientException {
        return super.delete(id, ReferenceDTO.class);
    }

    @Override
    public ReferenceDTO fetch(long id) throws MGXServerException, MGXClientException {
        return super.fetch(id, ReferenceDTO.class);    
    }
}
