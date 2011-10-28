package de.cebitec.mgx.client.access.rest;

import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.dto.JobDTO;
import de.cebitec.mgx.dto.JobDTOList;
import de.cebitec.mgx.dto.MGXBoolean;

/**
 *
 * @author sjaenick
 */
public class JobAccess<T, U> extends AccessBase<T, U> {

    @Override
    Class getType() {
        return JobDTO.class;
    }

    @Override
    Class getListType() {
        return JobDTOList.class;
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
}
