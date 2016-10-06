package de.cebitec.mgx.client.access.rest;

import de.cebitec.gpms.rest.RESTAccessI;
import de.cebitec.mgx.client.exception.MGXDTOException;
import de.cebitec.mgx.dto.dto.DNAExtractDTO;
import de.cebitec.mgx.dto.dto.DNAExtractDTOList;
import java.util.Iterator;
import java.util.UUID;

/**
 *
 * @author sjaenick
 */
public class DNAExtractAccess extends AccessBase<DNAExtractDTO, DNAExtractDTOList> {

    public DNAExtractAccess(RESTAccessI restAccess) {
        super(restAccess);
    }

    @Override
    public Iterator<DNAExtractDTO> fetchall() throws MGXDTOException {
        return fetchlist(DNAExtractDTOList.class).getExtractList().iterator();
    }

    public Iterator<DNAExtractDTO> bySample(long sample_id) throws MGXDTOException {
        return get(DNAExtractDTOList.class, r.resolve(DNAExtractDTO.class, "bySample", String.valueOf(sample_id))).getExtractList().iterator();
    }

    @Override
    public DNAExtractDTO fetch(long id) throws MGXDTOException {
        return super.fetch(id, DNAExtractDTO.class);
    }

    @Override
    public long create(DNAExtractDTO d) throws MGXDTOException {
        return super.create(d, DNAExtractDTO.class);
    }

    @Override
    public void update(DNAExtractDTO d) throws MGXDTOException {
        super.update(d, DNAExtractDTO.class);
    }

    @Override
    public UUID delete(long id) throws MGXDTOException {
        return super.delete(id, DNAExtractDTO.class);
    }
}
