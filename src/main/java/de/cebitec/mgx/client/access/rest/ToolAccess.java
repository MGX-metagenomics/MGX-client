package de.cebitec.mgx.client.access.rest;

import de.cebitec.mgx.client.exception.MGXClientException;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.dto.dto.JobParameterDTO;
import de.cebitec.mgx.dto.dto.JobParameterListDTO;
import de.cebitec.mgx.dto.dto.MGXLong;
import de.cebitec.mgx.dto.dto.ToolDTO;
import de.cebitec.mgx.dto.dto.ToolDTOList;
import java.util.Collection;

/**
 *
 * @author sjaenick
 */
public class ToolAccess extends AccessBase<ToolDTO, ToolDTOList> {

    public Iterable<JobParameterDTO> getAvailableParameters(long tool_id, boolean isGlobal) throws MGXServerException {
        return get("/Tool/getAvailableParameters/" + tool_id + "/" + isGlobal, JobParameterListDTO.class).getParameterList();
    }

    public Iterable<JobParameterDTO> getAvailableParameters(ToolDTO dto) throws MGXServerException {
        return put("/Tool/getAvailableParameters/", dto, JobParameterListDTO.class).getParameterList();
    }

    @Override
    public Collection<ToolDTO> fetchall() throws MGXServerException, MGXClientException {
        return fetchlist(ToolDTOList.class).getToolList();
    }

    public Collection<ToolDTO> listGlobalTools() throws MGXServerException {
        return get("/Tool/listGlobalTools", ToolDTOList.class).getToolList();
    }

    public long installGlobalTool(long global_id) throws MGXServerException {
        return get("/Tool/installGlobalTool/" + global_id, MGXLong.class).getValue();
    }

    @Override
    public void delete(long id) throws MGXServerException, MGXClientException {
        super.delete(id, ToolDTO.class);
    }

    @Override
    public ToolDTO fetch(long id) throws MGXServerException, MGXClientException {
        return super.fetch(id, ToolDTO.class);
    }

    @Override
    public long create(ToolDTO t) throws MGXServerException, MGXClientException {
        return super.create(t, ToolDTO.class);
    }

    @Override
    public void update(ToolDTO t) throws MGXServerException, MGXClientException {
        super.update(t, ToolDTO.class);
    }

    public ToolDTO ByJob(long job_id) throws MGXServerException, MGXClientException {
        return get(r.resolve(ToolDTO.class, "byJob") + job_id, ToolDTO.class);
    }
}
