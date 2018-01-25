package de.cebitec.mgx.client.access.rest;

import de.cebitec.gpms.rest.RESTAccessI;
import de.cebitec.mgx.client.exception.MGXDTOException;
import de.cebitec.mgx.dto.dto;
import de.cebitec.mgx.dto.dto.JobAndAttributeTypes;
import de.cebitec.mgx.dto.dto.JobsAndAttributeTypesDTO;
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
    public Iterator<SeqRunDTO> fetchall() throws MGXDTOException {
        return fetchlist(SeqRunDTOList.class).getSeqrunList().iterator();
    }

    @Override
    public SeqRunDTO fetch(long id) throws MGXDTOException {
        return super.fetch(id, SeqRunDTO.class); 
    }

    public Iterator<SeqRunDTO> byExtract(long extract_id) throws MGXDTOException {
        return get(SeqRunDTOList.class, r.resolve(SeqRunDTO.class, "byExtract", String.valueOf(extract_id))).getSeqrunList().iterator();
    }

    public SeqRunDTO byJob(long jobId) throws MGXDTOException {
        return get(SeqRunDTO.class, r.resolve(SeqRunDTO.class, "byJob", String.valueOf(jobId)));
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
        return get(JobsAndAttributeTypesDTO.class, r.resolve(SeqRunDTO.class, "JobsAndAttributeTypes", String.valueOf(seqrun_id))).getEntryList();
    }

    public List<QCResultDTO> getQC(long seqrun_id) throws MGXDTOException {
        return get(QCResultDTOList.class, r.resolve(SeqRunDTO.class, "getQC", String.valueOf(seqrun_id))).getResultList();
    }

    public boolean hasQuality(long seqrun_id) throws MGXDTOException {
        return get(dto.MGXBoolean.class, r.resolve(SeqRunDTO.class, "hasQuality", String.valueOf(seqrun_id))).getValue();
    }
}
