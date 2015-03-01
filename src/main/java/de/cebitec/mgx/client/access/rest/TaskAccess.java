package de.cebitec.mgx.client.access.rest;

import de.cebitec.mgx.client.exception.MGXClientException;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.dto.dto.TaskDTO;
import de.cebitec.mgx.dto.dto.TaskDTOList;
import java.util.Iterator;
import java.util.UUID;

/**
 *
 * @author sjaenick
 */
public class TaskAccess extends AccessBase<TaskDTO, TaskDTOList> {

    public TaskDTO get(UUID taskId) throws MGXServerException, MGXClientException {
        return get(TaskDTO.class, "Task", "get", String.valueOf(taskId));
    }

    @Override
    public TaskDTO fetch(long id) throws MGXServerException, MGXClientException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Iterator<TaskDTO> fetchall() throws MGXServerException, MGXClientException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public long create(TaskDTO t) throws MGXServerException, MGXClientException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void update(TaskDTO t) throws MGXServerException, MGXClientException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public UUID delete(long id) throws MGXServerException, MGXClientException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
