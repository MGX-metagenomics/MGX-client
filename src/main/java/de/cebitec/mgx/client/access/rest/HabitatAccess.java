package de.cebitec.mgx.client.access.rest;

import de.cebitec.mgx.client.exception.MGXClientException;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.dto.dto.HabitatDTO;
import de.cebitec.mgx.dto.dto.HabitatDTOList;
import java.util.Iterator;
import java.util.UUID;

/**
 *
 * @author sjaenick
 */
public class HabitatAccess extends AccessBase<HabitatDTO, HabitatDTOList> {

    @Override
    public Iterator<HabitatDTO> fetchall() throws MGXServerException, MGXClientException {
        return fetchlist(HabitatDTOList.class).getHabitatList().iterator();
    }

    @Override
    public long create(HabitatDTO dto) throws MGXServerException, MGXClientException {
        return super.create(dto, HabitatDTO.class);
    }
    
    @Override
    public HabitatDTO fetch (long id) throws MGXServerException, MGXClientException {
        return super.fetch(id, HabitatDTO.class);
    }

    @Override
    public void update(HabitatDTO dto) throws MGXServerException, MGXClientException {
        super.update(dto, HabitatDTO.class);
    }

    @Override
    public UUID delete(long id) throws MGXServerException, MGXClientException {
        return super.delete(id, HabitatDTO.class);
    }
}
