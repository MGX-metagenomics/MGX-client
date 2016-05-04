package de.cebitec.mgx.client.access.rest;

import de.cebitec.gpms.rest.RESTAccessI;
import de.cebitec.mgx.client.exception.MGXClientException;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.dto.dto.AttributeCorrelation;
import de.cebitec.mgx.dto.dto.AttributeDTO;
import de.cebitec.mgx.dto.dto.AttributeDTOList;
import de.cebitec.mgx.dto.dto.AttributeDistribution;
import de.cebitec.mgx.dto.dto.MGXString;
import de.cebitec.mgx.dto.dto.MGXStringList;
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

    public AttributeAccess(RESTAccessI restAccess) {
        super(restAccess);
    }

    public Iterator<AttributeDTO> BySeqRun(final long seqrunId) throws MGXServerException {
        return get(AttributeDTOList.class, "Attribute", "BySeqRun", String.valueOf(seqrunId)).getAttributeList().iterator();
    }

    public Iterator<AttributeDTO> ByJob(long jobId) throws MGXServerException {
        return get(AttributeDTOList.class, "Attribute", "ByJob", String.valueOf(jobId)).getAttributeList().iterator();
    }

    public AttributeDistribution getDistribution(final long attrType_id, long job_id) throws MGXServerException {
        return get(AttributeDistribution.class, "Attribute", "getDistribution", String.valueOf(attrType_id), String.valueOf(job_id));
    }

    public AttributeDistribution getHierarchy(final long attrType_id, final long job_id) throws MGXServerException {
        return get(AttributeDistribution.class, "Attribute", "getHierarchy", String.valueOf(attrType_id), String.valueOf(job_id));
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
        return get(AttributeCorrelation.class, path);
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
    public long create(AttributeDTO dto) throws MGXServerException, MGXClientException {
        return super.create(dto, AttributeDTO.class);
    }

    @Override
    public void update(AttributeDTO dto) throws MGXServerException, MGXClientException {
        super.update(dto, AttributeDTO.class);
    }

    @Override
    public UUID delete(long id) throws MGXServerException, MGXClientException {
        throw new MGXClientException("Attribute deletion is not supported. Delete the corresponding job instead.");
        //return super.delete(id, AttributeDTO.class);
    }

    public Iterator<SequenceDTO> search(SearchRequestDTO req) throws MGXServerException {
        List<SequenceDTO> ret = new ArrayList<>();
        SequenceDTOList reply = put(req, SequenceDTOList.class, "Attribute", "search");
        ret.addAll(reply.getSeqList());
        //Logger.getGlobal().log(Level.INFO, "got "+ret.size()+" seqs");

        while (!reply.getComplete()) {
            String uuid = reply.getUuid();
            reply = get(SequenceDTOList.class, "Attribute", "continueSearch", uuid, toString());
            //Logger.getGlobal().log(Level.INFO,"got additional "+reply.getSeqCount()+" seqs");
            ret.addAll(reply.getSeqList());
        }
        return ret.iterator();
    }

    public Iterator<String> find(SearchRequestDTO req) throws MGXServerException, MGXClientException {
        List<String> ret = new ArrayList<>();
        MGXStringList reply = put(req, MGXStringList.class, "Attribute", "find");
        for (MGXString ms : reply.getStringList()) {
            ret.add(ms.getValue());
        }
        return ret.iterator();
    }

}
