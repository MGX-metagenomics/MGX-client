/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.client.access.rest;

import static de.cebitec.mgx.client.access.rest.AccessBase.r;
import de.cebitec.mgx.client.exception.MGXClientException;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.dto.dto;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

/**
 *
 * @author belmann
 */
public class ReferenceAccess extends AccessBase<dto.ReferenceDTO, dto.ReferenceDTOList>{
     
    @Override
    public Iterator<dto.ReferenceDTO> fetchall() throws MGXServerException, MGXClientException {
        return fetchlist(dto.ReferenceDTOList.class).getReferenceList().iterator();
    }

    @Override
    public long create(dto.ReferenceDTO sr) throws MGXServerException, MGXClientException {
        return super.create(sr, dto.ReferenceDTO.class);
    }

    @Override
    public void update(dto.ReferenceDTO d) throws MGXServerException, MGXClientException {
        super.update(d, dto.ReferenceDTO.class);
    }

    @Override
    public UUID delete(long id) throws MGXServerException, MGXClientException {
        return super.delete(id, dto.ReferenceDTO.class);
    }

    @Override
    public dto.ReferenceDTO fetch(long id) throws MGXServerException, MGXClientException {
        return super.fetch(id, dto.ReferenceDTO.class);    
    }
}
