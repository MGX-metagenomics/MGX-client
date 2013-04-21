package de.cebitec.mgx.client.access.rest;

import de.cebitec.mgx.client.exception.MGXClientException;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.dto.dto.JobDTO;
import de.cebitec.mgx.dto.dto.JobDTOList;
import de.cebitec.mgx.dto.dto.JobParameterDTO;
import de.cebitec.mgx.dto.dto.JobParameterListDTO;
import de.cebitec.mgx.dto.dto.MGXBoolean;
import java.util.Iterator;

/**
 *
 * @author sjaenick
 */
public class JobAccess extends AccessBase<JobDTO, JobDTOList> {
    
    @Override
    public Iterator<JobDTO> fetchall() throws MGXServerException, MGXClientException {
        return fetchlist(JobDTOList.class).getJobList().iterator();
    }
    
    public boolean verify(long jobId) throws MGXServerException {
        return get("/Job/verify/" + jobId, MGXBoolean.class).getValue();
    }
    
    public boolean execute(long jobId) throws MGXServerException {
        return get("/Job/execute/" + jobId, MGXBoolean.class).getValue();
    }
    
    public boolean cancel(long jobId) throws MGXServerException {
        return get("/Job/cancel/" + jobId, MGXBoolean.class).getValue();
    }
    
    @Override
    public long create(JobDTO dto) throws MGXServerException, MGXClientException {
        return super.create(dto, JobDTO.class);
    }
    
    @Override
    public JobDTO fetch(long job_id) throws MGXServerException, MGXClientException {
        return super.fetch(job_id, JobDTO.class);
    }
    
    @Override
    public void update(JobDTO t) throws MGXServerException, MGXClientException {
        super.update(t, JobDTO.class);
    }
    
    @Override
    public boolean delete(long id) throws MGXServerException, MGXClientException {
        super.delete(id, JobDTO.class);
        return true;
    }
    
    public Iterable<JobDTO> ByAttributeTypeAndSeqRun(long atype_id, long seqrun_id) throws MGXServerException {
        return get("/Job/ByAttributeTypeAndSeqRun/" + atype_id + "/" + seqrun_id, JobDTOList.class).getJobList();
    }
    
    public Iterable<JobDTO> BySeqRun(long seqrun_id) throws MGXServerException {
        return get("/Job/BySeqRun/" + seqrun_id, JobDTOList.class).getJobList();
    }
    
    public Iterable<JobParameterDTO> getParameters(long job_id) throws MGXServerException {
        return get("/Job/getParameters/" + job_id, JobParameterListDTO.class).getParameterList();
    }

    public void setParameters(long job_id, JobParameterListDTO paramValues) throws MGXServerException {
        post("/Job/setParameters/" + job_id, paramValues);
    }
}
