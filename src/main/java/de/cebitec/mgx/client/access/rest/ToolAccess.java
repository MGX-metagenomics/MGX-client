package de.cebitec.mgx.client.access.rest;

import de.cebitec.mgx.client.exception.MGXClientException;
import de.cebitec.mgx.client.exception.MGXServerException;
import com.sun.jersey.api.client.ClientResponse;
import de.cebitec.mgx.dto.dto.MGXLong;
import de.cebitec.mgx.dto.dto.ToolDTO;
import de.cebitec.mgx.dto.dto.ToolDTOList;
import java.util.Collection;

/**
 *
 * @author sjaenick
 */
public class ToolAccess extends AccessBase<ToolDTO, ToolDTOList> {

    @Override
    public Collection<ToolDTO> fetchall() throws MGXServerException, MGXClientException {
        return fetchlist(ToolDTOList.class).getToolList();
    }

    public Collection<ToolDTO> listGlobalTools() throws MGXServerException {
        return get("/Tool/listGlobalTools", ToolDTOList.class).getToolList();
    }

    public Long installTool(Long global_id) throws MGXServerException {
        MGXLong g_id = de.cebitec.mgx.dto.dto.MGXLong.newBuilder().setValue(global_id).build();
        ClientResponse res = getWebResource().path("/Tool/installTool/").type("application/x-protobuf").put(ClientResponse.class, g_id);
        catchException(res);
        MGXLong local_id = res.getEntity(MGXLong.class);
        return local_id.getValue();
    }
        
    @Override
    public void delete(Long id) throws MGXServerException, MGXClientException {
        super.delete(id, ToolDTO.class);
    }

    @Override
    public ToolDTO fetch(Long id) throws MGXServerException, MGXClientException {
        return super.fetch(id, ToolDTO.class);
    }

    @Override
    public Long create(ToolDTO t) throws MGXServerException, MGXClientException {
        return super.create(t, ToolDTO.class);
    }

    @Override
    public void update(ToolDTO t) throws MGXServerException, MGXClientException {
        super.update(t, ToolDTO.class);
    }
}
