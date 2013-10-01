package de.cebitec.mgx.client.access.rest;

import de.cebitec.mgx.client.exception.MGXClientException;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.dto.dto.MappingDTO;
import de.cebitec.mgx.dto.dto.MappingDTOList;
import java.util.Iterator;
import java.util.UUID;

/**
 *
 * @author sjaenick
 */
public class MappingAccess extends AccessBase<MappingDTO, MappingDTOList> {

    @Override
    public Iterator<MappingDTO> fetchall() throws MGXServerException, MGXClientException {
        return fetchlist(MappingDTOList.class).getMappingList().iterator();
    }

    public Iterator<MappingDTO> BySeqRun(long id) throws MGXServerException, MGXClientException {
        return get(r.resolve(MappingDTO.class, "bySeqRun") + id, MappingDTOList.class).getMappingList().iterator();
    }

    public Iterator<MappingDTO> ByReference(long id) throws MGXServerException, MGXClientException {
        return get(r.resolve(MappingDTO.class, "byReference") + id, MappingDTOList.class).getMappingList().iterator();
    }

    @Override
    public long create(MappingDTO sr) throws MGXServerException, MGXClientException {
        return -1; // super.create(sr, MappingDTO.class);
    }

    @Override
    public void update(MappingDTO d) throws MGXServerException, MGXClientException {
        throw new UnsupportedOperationException("Not supported.");
        //super.update(d, MappingDTO.class);
    }

    @Override
    public UUID delete(long id) throws MGXServerException, MGXClientException {
        return super.delete(id, MappingDTO.class);
    }

    @Override
    public MappingDTO fetch(long id) throws MGXServerException, MGXClientException {
        return super.fetch(id, MappingDTO.class);
    }
}
