package de.cebitec.mgx.client.access.rest;

import de.cebitec.gpms.rest.RESTAccessI;
import de.cebitec.mgx.client.exception.MGXClientException;
import de.cebitec.mgx.client.exception.MGXDTOException;
import de.cebitec.mgx.dto.dto.GeneDTO;
import de.cebitec.mgx.dto.dto.GeneDTOList;
import de.cebitec.mgx.dto.dto.MGXStringList;
import java.util.Iterator;
import java.util.UUID;

/**
 *
 * @author sjaenick
 */
public class GeneAccess extends AccessBase<GeneDTO, GeneDTOList> {

    public GeneAccess(RESTAccessI restAccess) {
        super(restAccess);
    }

    @Override
    public Iterator<GeneDTO> fetchall() throws MGXDTOException {
        return fetchlist(GeneDTOList.class).getGeneList().iterator();
    }

    @Override
    public GeneDTO fetch(long id) throws MGXDTOException {
        return super.fetch(id, GeneDTO.class);
    }

    @Override
    public long create(GeneDTO s) throws MGXDTOException {
        return super.create(s, GeneDTO.class);
    }

    @Override
    public void update(GeneDTO d) throws MGXDTOException {
        super.update(d, GeneDTO.class);
    }

    @Override
    public UUID delete(long id) throws MGXDTOException {
        return super.delete(id, GeneDTO.class);
    }

    public Iterator<GeneDTO> byContig(long contig_id) throws MGXDTOException {
        return get(GeneDTOList.class, r.resolve(GeneDTOList.class, "byContig", String.valueOf(contig_id))).getGeneList().iterator();
    }

    public String[] getSequence(long gene_id) throws MGXDTOException {
        MGXStringList get = get(MGXStringList.class, r.resolve(MGXStringList.class, "getSequence", String.valueOf(gene_id)));
        return new String[]{get.getString(0).getValue(), get.getString(1).getValue()};
    }
}
