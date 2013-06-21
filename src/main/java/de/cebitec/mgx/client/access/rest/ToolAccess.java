package de.cebitec.mgx.client.access.rest;

import de.cebitec.mgx.client.access.rest.util.XMLValidator;
import java.util.logging.Logger;
import de.cebitec.mgx.client.exception.MGXClientException;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.dto.dto.JobParameterDTO;
import de.cebitec.mgx.dto.dto.JobParameterListDTO;
import de.cebitec.mgx.dto.dto.MGXLong;
import de.cebitec.mgx.dto.dto.ToolDTO;
import de.cebitec.mgx.dto.dto.ToolDTOList;
import java.io.IOException;
import java.util.Iterator;
import java.util.UUID;
import java.util.logging.Level;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

/**
 *
 * @author sjaenick
 */
public class ToolAccess extends AccessBase<ToolDTO, ToolDTOList> {

    public Iterable<JobParameterDTO> getAvailableParameters(long tool_id, boolean isGlobal) throws MGXServerException {
        return get("/Tool/getAvailableParameters/" + tool_id + "/" + isGlobal, JobParameterListDTO.class).getParameterList();
    }

    public Iterable<JobParameterDTO> getAvailableParameters(ToolDTO dto) throws MGXServerException, MGXClientException {
        XMLValidator validator = new XMLValidator();
        if (!validator.isValid(dto.getXml())) {
            throw new MGXClientException("Invalid tool file");
        }
        return put("/Tool/getAvailableParameters/", dto, JobParameterListDTO.class).getParameterList();
    }

    @Override
    public Iterator<ToolDTO> fetchall() throws MGXServerException, MGXClientException {
        return fetchlist(ToolDTOList.class).getToolList().iterator();
    }

    public Iterator<ToolDTO> listGlobalTools() throws MGXServerException {
        return get("/Tool/listGlobalTools", ToolDTOList.class).getToolList().iterator();
    }

    public long installGlobalTool(long global_id) throws MGXServerException {
        return get("/Tool/installGlobalTool/" + global_id, MGXLong.class).getValue();
    }

    @Override
    public UUID delete(long id) throws MGXServerException, MGXClientException {
        return super.delete(id, ToolDTO.class);
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
