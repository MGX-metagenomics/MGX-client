package de.cebitec.mgx.client.access.rest;

import de.cebitec.gpms.rest.RESTAccessI;
import de.cebitec.mgx.client.exception.MGXDTOException;
import de.cebitec.mgx.dto.dto.TaskDTO;
import de.cebitec.mgx.dto.dto.TaskDTOList;
import java.util.UUID;

/**
 *
 * @author sjaenick
 */
public class TaskAccess extends AccessBase<TaskDTO, TaskDTOList> {

    public TaskAccess(RESTAccessI restAccess) {
        super(restAccess);
    }

    public TaskDTO get(UUID taskId) throws MGXDTOException {
        return get(TaskDTO.class, "Task", "get", String.valueOf(taskId));
    }

    @Override
    public TaskDTO fetch(long id) throws MGXDTOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public TaskDTOList fetchall() throws MGXDTOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public long create(TaskDTO t) throws MGXDTOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void update(TaskDTO t) throws MGXDTOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public UUID delete(long id) throws MGXDTOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
