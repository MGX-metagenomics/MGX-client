package de.cebitec.mgx.client.access.rest;

import de.cebitec.mgx.client.exception.MGXClientException;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.dto.dto.AttributeTypeDTO;
import de.cebitec.mgx.dto.dto.AttributeTypeDTOList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

/**
 *
 * @author sjaenick
 */
public class AttributeTypeAccess extends AccessBase<AttributeTypeDTO, AttributeTypeDTOList> {

    public List<AttributeTypeDTO> BySeqRun(long seqrunId) throws MGXServerException, MGXClientException {
        return get("/AttributeType/BySeqRun/" + seqrunId, AttributeTypeDTOList.class).getAttributeTypeList();
    }

    @Override
    public AttributeTypeDTO fetch(long id) throws MGXServerException, MGXClientException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Iterator<AttributeTypeDTO> fetchall() throws MGXServerException, MGXClientException {
        throw new UnsupportedOperationException("Not supported yet.");
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

    public List<AttributeTypeDTO> ByJob(long seqrun_id) throws MGXServerException {
        return get("/AttributeType/ByJob/" + seqrun_id, AttributeTypeDTOList.class).getAttributeTypeList();
    }
}
