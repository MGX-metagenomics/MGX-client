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
import java.util.Collection;
import java.util.logging.Level;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

/**
 *
 * @author sjaenick
 */
public class ToolAccess extends AccessBase<ToolDTO, ToolDTOList> {

    private static final Logger LOGGER = Logger.getAnonymousLogger();

    public Iterable<JobParameterDTO> getAvailableParameters(long tool_id, boolean isGlobal) throws MGXServerException {
        return get("/Tool/getAvailableParameters/" + tool_id + "/" + isGlobal, JobParameterListDTO.class).getParameterList();
    }

    public Iterable<JobParameterDTO> getAvailableParameters(ToolDTO dto) throws MGXServerException {
        //TODO: Abfragen, ob xml valide..

        XMLValidator validator = new XMLValidator();
        try {
            if (!validator.isValid(dto.getXml())) {
                throw new MGXServerException("XML is not Valid");
            }
        } catch (SAXException ex) {
            Logger.getLogger(ToolAccess.class.getName()).log(Level.SEVERE, null, ex);
            throw new MGXServerException("XML is not valid: " + ex.getMessage());
        } catch (IOException ex) {
            Logger.getLogger(ToolAccess.class.getName()).log(Level.SEVERE, null, ex);
            throw new MGXServerException("XML is not valid: " + ex.getMessage());
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(ToolAccess.class.getName()).log(Level.SEVERE, null, ex);
            throw new MGXServerException("XML is not valid: " + ex.getMessage());
        }

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
