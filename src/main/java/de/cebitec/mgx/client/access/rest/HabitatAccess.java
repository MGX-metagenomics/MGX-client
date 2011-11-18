package de.cebitec.mgx.client.access.rest;

import de.cebitec.mgx.client.exception.MGXClientException;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.dto.dto.HabitatDTO;
import de.cebitec.mgx.dto.dto.HabitatDTOList;
import java.util.Collection;

/**
 *
 * @author sjaenick
 */
public class HabitatAccess extends AccessBase<HabitatDTO, HabitatDTOList> {

    public Collection<HabitatDTO> fetchall() throws MGXServerException, MGXClientException {
        return fetchlist(HabitatDTOList.class).getHabitatList();
    }

    public Long create(HabitatDTO h1) throws MGXServerException, MGXClientException {
        return super.create(h1, HabitatDTO.class);
    }
}
