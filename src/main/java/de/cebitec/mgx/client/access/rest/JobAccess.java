package de.cebitec.mgx.client.access.rest;

import de.cebitec.mgx.client.exception.MGXClientException;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.dto.dto.JobDTO;
import de.cebitec.mgx.dto.dto.JobDTOList;
import de.cebitec.mgx.dto.dto.MGXBoolean;

/**
 *
 * @author sjaenick
 */
public class JobAccess extends AccessBase<JobDTO, JobDTOList> {

    public boolean verify(Long jobId) throws MGXServerException {
        return get("/Job/verify/" + jobId.toString(), MGXBoolean.class).getValue();
    }

    public boolean execute(Long jobId) throws MGXServerException {
        return get("/Job/execute/" + jobId.toString(), MGXBoolean.class).getValue();
    }

    public boolean cancel(Long jobId) throws MGXServerException {
        return get("/Job/cancel/" + jobId.toString(), MGXBoolean.class).getValue();
    }

    public Long create(JobDTO dto) throws MGXServerException, MGXClientException {
        return super.create(dto, JobDTO.class);
    }

    public JobDTO fetch(Long job_id) throws MGXServerException, MGXClientException {
        return super.fetch(job_id, JobDTO.class);
    }
}
