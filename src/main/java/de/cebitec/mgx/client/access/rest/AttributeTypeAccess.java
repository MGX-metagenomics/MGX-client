package de.cebitec.mgx.client.access.rest;

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
    public long create(AttributeTypeDTO t) throws MGXServerException, MGXClientException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void update(AttributeTypeDTO t) throws MGXServerException, MGXClientException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public UUID delete(long id) throws MGXServerException, MGXClientException {
        throw new UnsupportedOperationException("Not supported.");
    }

    public Iterator<AttributeTypeDTO> ByJob(long job_id) throws MGXServerException {
        return get(AttributeTypeDTOList.class, "AttributeType", "ByJob", String.valueOf(job_id)).getAttributeTypeList().iterator();
    }
}
