package de.cebitec.mgx.client.access.rest;

import de.cebitec.mgx.client.exception.MGXClientException;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.dto.dto.JobDTO;
import de.cebitec.mgx.dto.dto.JobDTOList;
import de.cebitec.mgx.dto.dto.MGXBoolean;
import java.util.Collection;

/**
 *
 * @author sjaenick
 */
public class JobAccess extends AccessBase<JobDTO, JobDTOList> {

    @Override
    public Collection<JobDTO> fetchall() throws MGXServerException, MGXClientException {
        return fetchlist(JobDTOList.class).getJobList();
    }

    public boolean verify(Long jobId) throws MGXServerException {
        return get("/Job/verify/" + jobId.toString(), MGXBoolean.class).getValue();
    }

    public boolean execute(Long jobId) throws MGXServerException {
        return get("/Job/execute/" + jobId.toString(), MGXBoolean.class).getValue();
    }

    public boolean cancel(Long jobId) throws MGXServerException {
        return get("/Job/cancel/" + jobId.toString(), MGXBoolean.class).getValue();
    }

    @Override
    public Long create(JobDTO dto) throws MGXServerException, MGXClientException {
        return super.create(dto, JobDTO.class);
    }

    @Override
    public JobDTO fetch(Long job_id) throws MGXServerException, MGXClientException {
        return super.fetch(job_id, JobDTO.class);
    }

    @Override
    public void update(JobDTO t) throws MGXServerException, MGXClientException {
        super.update(t, JobDTO.class);
    }

    @Override
    public void delete(Long id) throws MGXServerException, MGXClientException {
        super.delete(id, JobDTO.class);
    }

    public Iterable<JobDTO> ByAttributeTypeAndSeqRun(Long atype_id, Long seqrun_id) throws MGXServerException {
        return get("/Job/ByAttributeTypeAndSeqRun/" + atype_id + "/" + seqrun_id, JobDTOList.class).getJobList();
    }

    public Iterable<JobDTO> BySeqRun(Long seqrun_id) throws MGXServerException {
        return get("/Job/BySeqRun/" + seqrun_id, JobDTOList.class).getJobList();
    }
}
