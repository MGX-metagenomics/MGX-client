package de.cebitec.mgx.client.access.rest;

import de.cebitec.mgx.client.exception.MGXClientException;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.dto.dto.SampleDTO;
import de.cebitec.mgx.dto.dto.SampleDTOList;
import java.util.Iterator;
import java.util.UUID;

/**
 *
 * @author sjaenick
 */
public class SampleAccess extends AccessBase<SampleDTO, SampleDTOList> {

    @Override
    public Iterator<SampleDTO> fetchall() throws MGXServerException, MGXClientException {
        return fetchlist(SampleDTOList.class).getSampleList().iterator();
    }

    @Override
    public SampleDTO fetch(long id) throws MGXServerException, MGXClientException {
        return super.fetch(id, SampleDTO.class);
    }

    public Iterator<SampleDTO> ByHabitat(long habitat_id) throws MGXServerException, MGXClientException {
        return get(SampleDTOList.class, r.resolve(SampleDTO.class, "byHabitat", String.valueOf(habitat_id))).getSampleList().iterator();
    }

    @Override
    public long create(SampleDTO s) throws MGXServerException, MGXClientException {
        return super.create(s, SampleDTO.class);
    }

    @Override
    public void update(SampleDTO d) throws MGXServerException, MGXClientException {
        super.update(d, SampleDTO.class);
    }

    @Override
    public UUID delete(long id) throws MGXServerException, MGXClientException {
        return super.delete(id, SampleDTO.class);
    }
}
