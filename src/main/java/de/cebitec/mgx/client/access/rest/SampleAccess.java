package de.cebitec.mgx.client.access.rest;

import de.cebitec.gpms.rest.RESTAccessI;
import de.cebitec.mgx.client.exception.MGXDTOException;
import de.cebitec.mgx.dto.dto.SampleDTO;
import de.cebitec.mgx.dto.dto.SampleDTOList;
import java.util.Iterator;
import java.util.UUID;

/**
 *
 * @author sjaenick
 */
public class SampleAccess extends AccessBase<SampleDTO, SampleDTOList> {

    public SampleAccess(RESTAccessI restAccess) {
        super(restAccess);
    }
    
    @Override
    public SampleDTOList fetchall() throws MGXDTOException {
        return fetchlist(SampleDTOList.class);
    }

    @Override
    public SampleDTO fetch(long id) throws MGXDTOException {
        return super.fetch(id, SampleDTO.class);
    }

    public Iterator<SampleDTO> byHabitat(long habitat_id) throws MGXDTOException {
        return get(SampleDTOList.class, r.resolve(SampleDTO.class, "byHabitat", String.valueOf(habitat_id))).getSampleList().iterator();
    }

    @Override
    public long create(SampleDTO s) throws MGXDTOException {
        return super.create(s, SampleDTO.class);
    }

    @Override
    public void update(SampleDTO d) throws MGXDTOException {
        super.update(d, SampleDTO.class);
    }

    @Override
    public UUID delete(long id) throws MGXDTOException {
        return super.delete(id, SampleDTO.class);
    }
}
