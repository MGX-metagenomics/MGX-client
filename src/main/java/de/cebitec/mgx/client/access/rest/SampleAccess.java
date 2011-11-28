package de.cebitec.mgx.client.access.rest;

import de.cebitec.mgx.client.exception.MGXClientException;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.dto.dto.SampleDTO;
import de.cebitec.mgx.dto.dto.SampleDTOList;
import java.util.Collection;

/**
 *
 * @author sjaenick
 */
public class SampleAccess extends AccessBase<SampleDTO, SampleDTOList> {

    @Override
    public Collection<SampleDTO> fetchall() throws MGXServerException, MGXClientException {
        return fetchlist(SampleDTOList.class).getSampleList();
    }

    @Override
    public SampleDTO fetch(Long id) throws MGXServerException, MGXClientException {
        return super.fetch(id, SampleDTO.class);
    }

    public Collection<SampleDTO> ByHabitat(Long habitat_id) throws MGXServerException, MGXClientException {
        return get(r.resolve(SampleDTO.class, "byHabitat") + habitat_id, SampleDTOList.class).getSampleList();
    }

    @Override
    public Long create(SampleDTO s) throws MGXServerException, MGXClientException {
        return super.create(s, SampleDTO.class);
    }

    @Override
    public void update(SampleDTO d) throws MGXServerException, MGXClientException {
        super.update(d, SampleDTO.class);
    }

    @Override
    public void delete(Long id) throws MGXServerException, MGXClientException {
        super.delete(id, SampleDTO.class);
    }
}
