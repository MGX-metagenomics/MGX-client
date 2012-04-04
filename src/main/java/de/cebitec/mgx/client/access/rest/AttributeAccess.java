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

//    public Collection<MGXString> listTypes() throws MGXServerException {
//        return get("/Attribute/listTypes/", MGXStringList.class).getStringList();
//    }
//
//    public Collection<MGXString> listTypesByJob(Long jobId) throws MGXServerException {
//        return get("/Attribute/listTypesByJob/" + jobId, MGXStringList.class).getStringList();
//    }
    public List<AttributeDTO> BySeqRun(Long seqrunId) throws MGXServerException {
        return get("/Attribute/BySeqRun/" + seqrunId, AttributeDTOList.class).getAttributeList();
    }

    public AttributeDistribution getDistribution(Long attrType_id, Long job_id) throws MGXServerException {
        return get("/Attribute/getDistribution/" + attrType_id + "/" + job_id, AttributeDistribution.class);
    }

    public AttributeDistribution getHierarchy(Long attrType_id, Long job_id) throws MGXServerException {
        return get("/Attribute/getHierarchy/" + attrType_id + "/" + job_id, AttributeDistribution.class);
    }

//    public List<AttributeCount> getDistributionByRuns(String attributeName, List<Long> seqrun_ids) throws MGXServerException {
//        String uri = new StringBuilder("/Attribute/getDistributionByRuns/")
//                .append(attributeName).append("/")
//                .append(join(seqrun_ids, ","))
//                .toString();
//        return get(uri, AttributeDistribution.class).getAttributecountList();
//    }
    @Override
    public AttributeDTO fetch(Long id) throws MGXServerException, MGXClientException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Collection<AttributeDTO> fetchall() throws MGXServerException, MGXClientException {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public Long create(AttributeDTO t) throws MGXServerException, MGXClientException {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public void update(AttributeDTO t) throws MGXServerException, MGXClientException {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public void delete(Long id) throws MGXServerException, MGXClientException {
        throw new UnsupportedOperationException("Not supported.");
    }
}
