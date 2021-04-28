package de.cebitec.mgx.client.access.rest;

import de.cebitec.gpms.rest.RESTAccessI;
import de.cebitec.mgx.client.exception.MGXDTOException;
import de.cebitec.mgx.dto.dto.JobDTO;
import de.cebitec.mgx.dto.dto.JobDTOList;
import de.cebitec.mgx.dto.dto.JobParameterDTO;
import de.cebitec.mgx.dto.dto.JobParameterListDTO;
import de.cebitec.mgx.dto.dto.MGXBoolean;
import de.cebitec.mgx.dto.dto.MGXString;
import java.util.Iterator;
import java.util.UUID;

/**
 *
 * @author sjaenick
 */
public class JobAccess extends AccessBase<JobDTO, JobDTOList> {

    public JobAccess(RESTAccessI restAccess) {
        super(restAccess);
    }

    @Override
    public Iterator<JobDTO> fetchall() throws MGXDTOException {
        return fetchlist(JobDTOList.class).getJobList().iterator();
    }

    public boolean verify(long jobId) throws MGXDTOException {
        return get(MGXBoolean.class, "Job", "verify", String.valueOf(jobId)).getValue();
        //return get("/Job/verify/" + jobId, MGXBoolean.class).getValue();
    }

    public boolean execute(long jobId) throws MGXDTOException {
        return get(MGXBoolean.class, "Job", "execute", String.valueOf(jobId)).getValue();
    }

    public boolean cancel(long jobId) throws MGXDTOException {
        return get(MGXBoolean.class, "Job", "cancel", String.valueOf(jobId)).getValue();
    }

    public UUID restart(long jobId) throws MGXDTOException {
        return UUID.fromString(get(MGXString.class, "Job", "restart", String.valueOf(jobId)).getValue());
    }

    @Override
    public long create(JobDTO dto) throws MGXDTOException {
        return super.create(dto, JobDTO.class);
    }

    @Override
    public JobDTO fetch(long jobId) throws MGXDTOException {
        return super.fetch(jobId, JobDTO.class);
    }

    @Override
    public void update(JobDTO t) throws MGXDTOException {
        super.update(t, JobDTO.class);
        }

    @Override
    public UUID delete(long id) throws MGXDTOException {
        return super.delete(id, JobDTO.class);
    }

    public Iterable<JobDTO> byAttributeTypeAndSeqRun(long atype_id, long seqrun_id) throws MGXDTOException {
        return get(JobDTOList.class, "Job", "ByAttributeTypeAndSeqRun", String.valueOf(atype_id), String.valueOf(seqrun_id)).getJobList();
    }

    public Iterable<JobDTO> bySeqRun(long seqrun_id) throws MGXDTOException {
        return get(JobDTOList.class, "Job", "BySeqRun", String.valueOf(seqrun_id)).getJobList();
    }

    public Iterable<JobParameterDTO> getParameters(long job_id) throws MGXDTOException {
        return get(JobParameterListDTO.class, "Job", "getParameters", String.valueOf(job_id)).getParameterList();
    }

    public void setParameters(long job_id, JobParameterListDTO paramValues) throws MGXDTOException {
        post(paramValues, "Job", "setParameters", String.valueOf(job_id));
    }

    public MGXString getError(long job_id) throws MGXDTOException {
        return get(MGXString.class, "Job", "GetError", String.valueOf(job_id));
    }

    public void runDefaultTools(long runId) throws MGXDTOException {
        MGXString dto = MGXString.newBuilder().setValue(String.valueOf(runId)).build();
        put(dto, "Job", "runDefaultTools");
    }
}
