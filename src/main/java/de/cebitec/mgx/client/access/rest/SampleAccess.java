package de.cebitec.mgx.client.access.rest;

import de.cebitec.mgx.client.exception.MGXClientException;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.dto.SampleDTO;
import de.cebitec.mgx.dto.SampleDTOList;
import java.util.Collection;

/**
 *
 * @author sjaenick
 */
public class SampleAccess extends AccessBase<SampleDTO, SampleDTOList> {

    public Collection<SampleDTO> fetchall() throws MGXServerException, MGXClientException {
        return fetchlist(SampleDTOList.class).getSampleList();
    }

    public Collection<SampleDTO> ByHabitat(Long habitat_id) throws MGXServerException {
        return get("/Sample/byHabitat/" + habitat_id, SampleDTOList.class).getSampleList();
    }

    public Long create(SampleDTO s) throws MGXServerException, MGXClientException {
        return super.create(s, SampleDTO.class);
    }
}
