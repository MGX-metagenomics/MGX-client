package de.cebitec.mgx.client.access.rest;

import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.dto.HabitatDTO;
import de.cebitec.mgx.dto.HabitatDTOList;
import java.util.Collection;

/**
 *
 * @author sjaenick
 */
public class HabitatAccess<T, U> extends AccessBase<T, U> {

    @Override
    Class getType() {
        return HabitatDTO.class;
    }

    @Override
    Class getListType() {
        return HabitatDTOList.class;
    }

    public Collection<HabitatDTO> fetchall() throws MGXServerException {
        return get("/Habitat/fetchall/", HabitatDTOList.class).getHabitatList();
    }
}
