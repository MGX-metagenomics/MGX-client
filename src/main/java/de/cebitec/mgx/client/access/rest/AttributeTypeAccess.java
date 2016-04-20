package de.cebitec.mgx.client.access.rest;

import de.cebitec.gpms.rest.RESTAccessI;
import de.cebitec.mgx.client.exception.MGXClientException;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.dto.dto.AttributeTypeDTO;
import de.cebitec.mgx.dto.dto.AttributeTypeDTOList;
import java.util.Iterator;
import java.util.UUID;

/**
 *
 * @author sjaenick
 */
public class AttributeTypeAccess extends AccessBase<AttributeTypeDTO, AttributeTypeDTOList> {

    public AttributeTypeAccess(RESTAccessI restAccess) {
        super(restAccess);
    }

    public Iterator<AttributeTypeDTO> BySeqRun(long seqrunId) throws MGXServerException, MGXClientException {
        return get(AttributeTypeDTOList.class, "AttributeType", "BySeqRun", String.valueOf(seqrunId)).getAttributeTypeList().iterator();
    }

    @Override
    public AttributeTypeDTO fetch(long id) throws MGXServerException, MGXClientException {
        return super.fetch(id, AttributeTypeDTO.class);
    }

    @Override
    public Iterator<AttributeTypeDTO> fetchall() throws MGXServerException, MGXClientException {
        return fetchlist(AttributeTypeDTOList.class).getAttributeTypeList().iterator();
    }

    @Override
    public long create(AttributeTypeDTO dto) throws MGXServerException, MGXClientException {
        return super.create(dto, AttributeTypeDTO.class);
    }

    @Override
    public void update(AttributeTypeDTO dto) throws MGXServerException, MGXClientException {
        super.update(dto, AttributeTypeDTO.class);
    }

    @Override
    public UUID delete(long id) throws MGXServerException, MGXClientException {
        return super.delete(id, AttributeTypeDTO.class);
    }

    public Iterator<AttributeTypeDTO> ByJob(long job_id) throws MGXServerException {
        return get(AttributeTypeDTOList.class, "AttributeType", "ByJob", String.valueOf(job_id)).getAttributeTypeList().iterator();
    }
}
