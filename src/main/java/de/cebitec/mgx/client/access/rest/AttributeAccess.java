package de.cebitec.mgx.client.access.rest;

import de.cebitec.mgx.client.exception.MGXClientException;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.dto.dto.AttributeCount;
import de.cebitec.mgx.dto.dto.AttributeDTO;
import de.cebitec.mgx.dto.dto.AttributeDTOList;
import de.cebitec.mgx.dto.dto.AttributeDistribution;
import de.cebitec.mgx.dto.dto.MGXString;
import de.cebitec.mgx.dto.dto.MGXStringList;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author sjaenick
 */
public class AttributeAccess extends AccessBase<AttributeDTO, AttributeDTOList> {

    public Collection<MGXString> listTypes() throws MGXServerException {
        return get("/Attribute/listTypes/", MGXStringList.class).getStringList();
    }

    public Collection<MGXString> listTypesByJob(Long jobId) throws MGXServerException {
        return get("/Attribute/listTypesByJob/" + jobId, MGXStringList.class).getStringList();
    }

    public Collection<MGXString> listTypesBySeqRun(Long seqrunId) throws MGXServerException {
        return get("/Attribute/listTypesBySeqRun/" + seqrunId, MGXStringList.class).getStringList();
    }

    public List<AttributeCount> getDistribution(String attributeName, Long jobId, List<Long> seqrun_ids) throws MGXServerException {
        String uri = new StringBuilder("/Attribute/getDistribution/")
                .append(attributeName).append("/")
                .append(jobId.toString()).append("/")
                .append(join(seqrun_ids, ","))
                .toString();
        return get(uri, AttributeDistribution.class).getAttributecountList();
    }

    public List<AttributeCount> getDistributionByRuns(String attributeName, List<Long> seqrun_ids) throws MGXServerException {
        String uri = new StringBuilder("/Attribute/getDistributionByRuns/")
                .append(attributeName).append("/")
                .append(join(seqrun_ids, ","))
                .toString();
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
