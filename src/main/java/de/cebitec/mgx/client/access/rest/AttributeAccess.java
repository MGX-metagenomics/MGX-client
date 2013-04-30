package de.cebitec.mgx.client.access.rest;

import de.cebitec.mgx.client.exception.MGXClientException;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.dto.dto.AttributeCorrelation;
import de.cebitec.mgx.dto.dto.AttributeDTO;
import de.cebitec.mgx.dto.dto.AttributeDTOList;
import de.cebitec.mgx.dto.dto.AttributeDistribution;
import de.cebitec.mgx.dto.dto.SearchRequestDTO;
import de.cebitec.mgx.dto.dto.SequenceDTO;
import de.cebitec.mgx.dto.dto.SequenceDTOList;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

/**
 *
 * @author sjaenick
 */
public class AttributeAccess extends AccessBase<AttributeDTO, AttributeDTOList> {

    public Iterator<AttributeDTO> BySeqRun(final long seqrunId) throws MGXServerException {
        return get("/Attribute/BySeqRun/" + seqrunId, AttributeDTOList.class).getAttributeList().iterator();
    }

    public AttributeDistribution getDistribution(final long attrType_id, long job_id) throws MGXServerException {
        return get("/Attribute/getDistribution/" + attrType_id + "/" + job_id, AttributeDistribution.class);
    }

    public AttributeDistribution getHierarchy(final long attrType_id, final long job_id) throws MGXServerException {
        return get("/Attribute/getHierarchy/" + attrType_id + "/" + job_id, AttributeDistribution.class);
    }

    public AttributeCorrelation getCorrelation(final long attrtypeId1, final long jobid1, final long attrtypeid2, final long jobid2) throws MGXServerException {
        String path = new StringBuilder("/Attribute/getCorrelation/")
                .append(attrtypeId1)
                .append('/')
                .append(jobid1)
                .append('/')
                .append(attrtypeid2)
                .append('/')
                .append(jobid2)
                .toString();
        return get(path, AttributeCorrelation.class);
    }

//    public List<AttributeCount> getDistributionByRuns(String attributeName, List<Long> seqrun_ids) throws MGXServerException {
//        String uri = new StringBuilder("/Attribute/getDistributionByRuns/")
//                .append(attributeName).append("/")
//                .append(join(seqrun_ids, ","))
//                .toString();
//        return get(uri, AttributeDistribution.class).getAttributecountList();
//    }
    @Override
    public AttributeDTO fetch(final long id) throws MGXServerException, MGXClientException {
        return super.fetch(id, AttributeDTO.class);
    }

    @Override
    public Iterator<AttributeDTO> fetchall() throws MGXServerException, MGXClientException {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public long create(AttributeDTO t) throws MGXServerException, MGXClientException {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public void update(AttributeDTO t) throws MGXServerException, MGXClientException {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public UUID delete(long id) throws MGXServerException, MGXClientException {
        throw new UnsupportedOperationException("Not supported.");
    }

    public List<SequenceDTO> search(SearchRequestDTO req) throws MGXServerException {
        List<SequenceDTO> ret = new ArrayList<>();
        SequenceDTOList reply = put("/Attribute/search/", req, SequenceDTOList.class);
        ret.addAll(reply.getSeqList());
        //Logger.getGlobal().log(Level.INFO, "got "+ret.size()+" seqs");

        while (!reply.getComplete()) {
            String uuid = reply.getUuid();
            reply = get("/Attribute/continueSearch/" + uuid, SequenceDTOList.class);
            //Logger.getGlobal().log(Level.INFO,"got additional "+reply.getSeqCount()+" seqs");
            ret.addAll(reply.getSeqList());
        }
        return ret;
    }
}
