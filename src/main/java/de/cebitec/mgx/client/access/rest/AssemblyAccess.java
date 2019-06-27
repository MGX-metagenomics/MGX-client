package de.cebitec.mgx.client.access.rest;

import de.cebitec.gpms.rest.RESTAccessI;
import de.cebitec.mgx.client.exception.MGXDTOException;
import de.cebitec.mgx.dto.dto.AssemblyDTO;
import de.cebitec.mgx.dto.dto.AssemblyDTOList;
import de.cebitec.mgx.dto.dto.SampleDTO;
import de.cebitec.mgx.dto.dto.SampleDTOList;
import java.util.Iterator;
import java.util.UUID;

/**
 *
 * @author sjaenick
 */
public class AssemblyAccess extends AccessBase<AssemblyDTO, AssemblyDTOList> {

    public AssemblyAccess(RESTAccessI restAccess) {
        super(restAccess);
    }

    @Override
    public Iterator<AssemblyDTO> fetchall() throws MGXDTOException {
        return fetchlist(AssemblyDTOList.class).getAssemblyList().iterator();
    }

    @Override
    public AssemblyDTO fetch(long id) throws MGXDTOException {
        return super.fetch(id, AssemblyDTO.class);
    }

    @Override
    public long create(AssemblyDTO s) throws MGXDTOException {
        return super.create(s, AssemblyDTO.class);
    }

    @Override
    public void update(AssemblyDTO d) throws MGXDTOException {
        super.update(d, AssemblyDTO.class);
    }

    @Override
    public UUID delete(long id) throws MGXDTOException {
        return super.delete(id, AssemblyDTO.class);
    }
}
