package de.cebitec.mgx.client.access.rest;

import de.cebitec.gpms.rest.RESTAccessI;
import de.cebitec.mgx.client.access.rest.util.XMLValidator;
import de.cebitec.mgx.client.exception.MGXClientException;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.dto.dto.JobParameterDTO;
import de.cebitec.mgx.dto.dto.JobParameterListDTO;
import de.cebitec.mgx.dto.dto.MGXLong;
import de.cebitec.mgx.dto.dto.MGXString;
import de.cebitec.mgx.dto.dto.ToolDTO;
import de.cebitec.mgx.dto.dto.ToolDTOList;
import java.util.Iterator;
import java.util.UUID;

/**
 *
 * @author sjaenick
 */
public class ToolAccess extends AccessBase<ToolDTO, ToolDTOList> {

    public ToolAccess(RESTAccessI restAccess) {
        super(restAccess);
    }

    public Iterable<JobParameterDTO> getAvailableParameters(long tool_id, boolean isGlobal) throws MGXServerException {
        return get(JobParameterListDTO.class, "Tool", "getAvailableParameters", String.valueOf(tool_id), String.valueOf(isGlobal)).getParameterList();
    }

    public Iterable<JobParameterDTO> getAvailableParameters(String toolXml) throws MGXClientException, MGXServerException {
        XMLValidator validator = new XMLValidator();
        if (!validator.isValid(toolXml)) {
            throw new MGXClientException("Invalid tool XML data");
        }
        MGXString dto = MGXString.newBuilder().setValue(toolXml).build();
        return put(dto, JobParameterListDTO.class, "Tool", "getParameters").getParameterList();
    }

//    public Iterable<JobParameterDTO> getAvailableParameters(ToolDTO dto) throws MGXServerException, MGXClientException {
//        XMLValidator validator = new XMLValidator();
//        if (!validator.isValid(dto.getXml())) {
//            throw new MGXClientException("Invalid tool file");
//        }
//        return put(dto, JobParameterListDTO.class, "Tool", "getAvailableParameters").getParameterList();
//    }

    @Override
    public Iterator<ToolDTO> fetchall() throws MGXServerException, MGXClientException {
        return fetchlist(ToolDTOList.class).getToolList().iterator();
    }

    public Iterator<ToolDTO> listGlobalTools() throws MGXServerException {
        return get(ToolDTOList.class, "Tool", "listGlobalTools").getToolList().iterator();
    }

    public long installGlobalTool(long global_id) throws MGXServerException {
        return get(MGXLong.class, "Tool", "installGlobalTool", String.valueOf(global_id)).getValue();
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
        return get(ToolDTO.class, r.resolve(ToolDTO.class, "byJob", String.valueOf(job_id)));
    }

    public String getXMLDefinition(long tool_id) throws MGXServerException, MGXClientException {
        return get(MGXString.class, r.resolve(ToolDTO.class, "getXML", String.valueOf(tool_id))).getValue();
    }

}
