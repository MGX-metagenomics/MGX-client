package de.cebitec.mgx.client.access.rest;

import de.cebitec.mgx.client.exception.MGXClientException;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.dto.dto.AttributeCount;
import de.cebitec.mgx.dto.dto.AttributeDTO;
import de.cebitec.mgx.dto.dto.AttributeDTOList;
import de.cebitec.mgx.dto.dto.AttributeDistribution;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author sjaenick
 */
public class AttributeAccess extends AccessBase<AttributeDTO, AttributeDTOList> {

    public Collection<AttributeDTO> listTypes() throws MGXServerException {
        return get("/Attribute/listTypes/", AttributeDTOList.class).getAttributeList();
    }

    public Collection<AttributeDTO> listTypesByJob(Long jobId) throws MGXServerException {
        return get("/Attribute/listTypesByJob/" + jobId, AttributeDTOList.class).getAttributeList();
    }

    public List<AttributeCount> getDistribution(String attributeName, Long jobId, List<Long> seqrun_ids) throws MGXServerException {
        String uri = new StringBuilder("/Attribute/getDistribution/").append(attributeName).append("/").append(jobId.toString()).append("/").append(join(seqrun_ids, ",")).toString();
        return get(uri, AttributeDistribution.class).getAttributecountList();
    }

    @Override
    public AttributeDTO fetch(Long id) throws MGXServerException, MGXClientException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Collection<AttributeDTO> fetchall() throws MGXServerException, MGXClientException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Long create(AttributeDTO t) throws MGXServerException, MGXClientException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void update(AttributeDTO t) throws MGXServerException, MGXClientException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void delete(Long id) throws MGXServerException, MGXClientException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
