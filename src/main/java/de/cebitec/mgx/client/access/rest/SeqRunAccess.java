package de.cebitec.mgx.client.access.rest;

import de.cebitec.gpms.rest.RESTAccessI;
import de.cebitec.mgx.client.exception.MGXDTOException;
import de.cebitec.mgx.common.ToolScope;
import de.cebitec.mgx.dto.dto.JobAndAttributeTypes;
import de.cebitec.mgx.dto.dto.JobsAndAttributeTypesDTO;
import de.cebitec.mgx.dto.dto.MGXBoolean;
import de.cebitec.mgx.dto.dto.QCResultDTO;
import de.cebitec.mgx.dto.dto.QCResultDTOList;
import de.cebitec.mgx.dto.dto.SeqRunDTO;
import de.cebitec.mgx.dto.dto.SeqRunDTOList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

/**
 *
 * @author sjaenick
 */
public class SeqRunAccess extends AccessBase<SeqRunDTO, SeqRunDTOList> {

    public SeqRunAccess(RESTAccessI restAccess) {
        super(restAccess);
    }

    @Override
    public SeqRunDTOList fetchall() throws MGXDTOException {
        return fetchlist(SeqRunDTOList.class);
    }

    @Override
    public SeqRunDTO fetch(long id) throws MGXDTOException {
        return super.fetch(id, SeqRunDTO.class);
    }

    public Iterator<SeqRunDTO> byExtract(long extract_id) throws MGXDTOException {
        return get(SeqRunDTOList.class, r.resolve(SeqRunDTO.class, "byExtract", String.valueOf(extract_id))).getSeqrunList().iterator();
    }

    public Iterator<SeqRunDTO> byJob(long jobId) throws MGXDTOException {
        return get(SeqRunDTOList.class, r.resolve(SeqRunDTO.class, "byJob", String.valueOf(jobId))).getSeqrunList().iterator();
    }

    public Iterator<SeqRunDTO> byAssembly(long asmId) throws MGXDTOException {
        return get(SeqRunDTOList.class, r.resolve(SeqRunDTO.class, "byAssembly", String.valueOf(asmId))).getSeqrunList().iterator();
    }

    @Override
    public long create(SeqRunDTO sr) throws MGXDTOException {
        return super.create(sr, SeqRunDTO.class);
    }

    @Override
    public void update(SeqRunDTO d) throws MGXDTOException {
        super.update(d, SeqRunDTO.class);
    }

    @Override
    public UUID delete(long id) throws MGXDTOException {
        return super.delete(id, SeqRunDTO.class);
    }

    public List<JobAndAttributeTypes> getJobsAndAttributeTypes(long seqrun_id) throws MGXDTOException {
        return get(JobsAndAttributeTypesDTO.class, r.resolve(SeqRunDTO.class, "JobsAndAttributeTypes", String.valueOf(seqrun_id),
                "0",
                String.valueOf(ToolScope.READ.getValue()))).getEntryList();
    }

    public List<JobAndAttributeTypes> getJobsAndAttributeTypes(long seqrun_id, long assembly_id) throws MGXDTOException {
        return get(JobsAndAttributeTypesDTO.class, r.resolve(SeqRunDTO.class, "JobsAndAttributeTypes", String.valueOf(seqrun_id),
                String.valueOf(assembly_id),
                String.valueOf(ToolScope.GENE_ANNOTATION.getValue()))).getEntryList();
    }

    public List<QCResultDTO> getQC(long seqrun_id) throws MGXDTOException {
        return get(QCResultDTOList.class, r.resolve(SeqRunDTO.class, "getQC", String.valueOf(seqrun_id))).getResultList();
    }

    public boolean hasQuality(long seqrun_id) throws MGXDTOException {
        return get(MGXBoolean.class, r.resolve(SeqRunDTO.class, "hasQuality", String.valueOf(seqrun_id))).getValue();
    }
}
