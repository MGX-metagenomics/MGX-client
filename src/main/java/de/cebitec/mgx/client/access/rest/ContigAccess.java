package de.cebitec.mgx.client.access.rest;

import de.cebitec.gpms.rest.RESTAccessI;
import de.cebitec.mgx.client.exception.MGXDTOException;
import de.cebitec.mgx.dto.dto.ContigDTO;
import de.cebitec.mgx.dto.dto.ContigDTOList;
import java.util.Iterator;
import java.util.UUID;

/**
 *
 * @author sjaenick
 */
public class ContigAccess extends AccessBase<ContigDTO, ContigDTOList> {

    public ContigAccess(RESTAccessI restAccess) {
        super(restAccess);
    }

    @Override
    public Iterator<ContigDTO> fetchall() throws MGXDTOException {
        return fetchlist(ContigDTOList.class).getContigList().iterator();
    }

    @Override
    public ContigDTO fetch(long id) throws MGXDTOException {
        return super.fetch(id, ContigDTO.class);
    }

    @Override
    public long create(ContigDTO s) throws MGXDTOException {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public void update(ContigDTO d) throws MGXDTOException {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public UUID delete(long id) throws MGXDTOException {
        return super.delete(id, ContigDTO.class);
    }

    public Iterator<ContigDTO> byBin(long bin_id) throws MGXDTOException {
        return get(ContigDTOList.class, r.resolve(ContigDTOList.class, "byBin", String.valueOf(bin_id))).getContigList().iterator();
    }
}
