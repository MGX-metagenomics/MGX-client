package de.cebitec.mgx.client.access.rest;

import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.dto.SampleDTO;
import de.cebitec.mgx.dto.SampleDTOList;
import java.util.Collection;

/**
 *
 * @author sjaenick
 */
public class SampleAccess<T, U> extends AccessBase<T, U> {

    @Override
    Class getType() {
        return SampleDTO.class;
    }

    @Override
    Class getListType() {
        return SampleDTOList.class;
    }

    public Collection<SampleDTO> fetchall() throws MGXServerException {
        return get("/Sample/fetchall", SampleDTOList.class).getSampleList();
    }

    public Collection<SampleDTO> ByHabitat(Long habitat_id) throws MGXServerException {
        return get("/Sample/byHabitat/" + habitat_id, SampleDTOList.class).getSampleList();
    }
}
