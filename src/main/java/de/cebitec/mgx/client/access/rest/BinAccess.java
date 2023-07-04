package de.cebitec.mgx.client.access.rest;

import de.cebitec.gpms.rest.RESTAccessI;
import de.cebitec.mgx.client.MGXDTOMaster;
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

    private final MGXDTOMaster dtomaster;

    public BinAccess(MGXDTOMaster dtomaster, RESTAccessI restAccess) {
        super(restAccess);
        this.dtomaster = dtomaster;
    }

    @Override
    public BinDTOList fetchall() throws MGXDTOException {
        return fetchlist(BinDTOList.class);
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
        return super.create(s, BinDTO.class);
    }

    @Override
    public void update(BinDTO d) throws MGXDTOException {
        super.update(d, BinDTO.class);
    }

    @Override
    public UUID delete(long id) throws MGXDTOException {
        return super.delete(id, BinDTO.class);
    }
}
