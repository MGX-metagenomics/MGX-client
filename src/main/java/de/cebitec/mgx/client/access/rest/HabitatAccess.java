package de.cebitec.mgx.client.access.rest;

import de.cebitec.gpms.rest.RESTAccessI;
import de.cebitec.mgx.client.exception.MGXDTOException;
import de.cebitec.mgx.dto.dto.HabitatDTO;
import de.cebitec.mgx.dto.dto.HabitatDTOList;
import java.util.UUID;

/**
 *
 * @author sjaenick
 */
public class HabitatAccess extends AccessBase<HabitatDTO, HabitatDTOList> {

    public HabitatAccess(RESTAccessI restAccess) {
        super(restAccess);
    }

    @Override
    public HabitatDTOList fetchall() throws MGXDTOException {
        return fetchlist(HabitatDTOList.class);
    }

    @Override
    public long create(HabitatDTO dto) throws MGXDTOException {
        return super.create(dto, HabitatDTO.class);
    }
    
    @Override
    public HabitatDTO fetch (long id) throws MGXDTOException {
        return super.fetch(id, HabitatDTO.class);
    }

    @Override
    public void update(HabitatDTO dto) throws MGXDTOException {
        super.update(dto, HabitatDTO.class);
    }

    @Override
    public UUID delete(long id) throws MGXDTOException {
        return super.delete(id, HabitatDTO.class);
    }
}
