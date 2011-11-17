package de.cebitec.mgx.client.access.rest;

import de.cebitec.mgx.client.exception.MGXServerException;
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

    public AttributeDistribution getDistribution(String attributeName, Long jobId, List<Long> seqrun_ids) throws MGXServerException {
        String uri = new StringBuilder("/Attribute/getDistribution/").append(attributeName).append("/").append(jobId.toString()).append("/").append(join(seqrun_ids, ",")).toString();
        return get(uri, AttributeDistribution.class);
    }
}
