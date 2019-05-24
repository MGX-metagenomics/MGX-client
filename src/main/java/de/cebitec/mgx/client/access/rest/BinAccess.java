package de.cebitec.mgx.client.access.rest;

import de.cebitec.gpms.rest.RESTAccessI;
import de.cebitec.mgx.client.exception.MGXDTOException;
import de.cebitec.mgx.dto.dto.BinDTO;
import de.cebitec.mgx.dto.dto.BinDTOList;
import java.util.Iterator;
import java.util.UUID;

/**
 *
 * @author sjaenick
 */
public class BinAccess extends AccessBase<BinDTO, BinDTOList> {

    public BinAccess(RESTAccessI restAccess) {
        super(restAccess);
    }

    @Override
    public Iterator<BinDTO> fetchall() throws MGXDTOException {
        return fetchlist(BinDTOList.class).getBinList().iterator();
    }

    public Iterator<BinDTO> byAssembly(long asm_id) throws MGXDTOException {
        return get(BinDTOList.class, r.resolve(BinDTOList.class, "byAssembly", String.valueOf(asm_id))).getBinList().iterator();
    }

    @Override
    public BinDTO fetch(long id) throws MGXDTOException {
        return super.fetch(id, BinDTO.class);
    }

    @Override
    public long create(BinDTO s) throws MGXDTOException {
        throw new UnsupportedOperationException("Not supported.");

    }

    @Override
    public void update(BinDTO d) throws MGXDTOException {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public UUID delete(long id) throws MGXDTOException {
        return super.delete(id, BinDTO.class);
    }
}
