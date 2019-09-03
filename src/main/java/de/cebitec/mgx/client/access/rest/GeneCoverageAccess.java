package de.cebitec.mgx.client.access.rest;

import de.cebitec.gpms.rest.RESTAccessI;
import de.cebitec.mgx.client.exception.MGXDTOException;
import de.cebitec.mgx.dto.dto.GeneCoverageDTO;
import de.cebitec.mgx.dto.dto.GeneCoverageDTOList;
import java.util.Iterator;
import java.util.UUID;

/**
 *
 * @author sjaenick
 */
public class GeneCoverageAccess extends AccessBase<GeneCoverageDTO, GeneCoverageDTOList> {

    public GeneCoverageAccess(RESTAccessI restAccess) {
        super(restAccess);
    }

    public Iterator<GeneCoverageDTO> byGene(long gene_id) throws MGXDTOException {
        return get(GeneCoverageDTOList.class, r.resolve(GeneCoverageDTOList.class, "byGene", String.valueOf(gene_id))).getGeneCoverageList().iterator();
    }

    @Override
    public GeneCoverageDTO fetch(long id) throws MGXDTOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Iterator<GeneCoverageDTO> fetchall() throws MGXDTOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public long create(GeneCoverageDTO t) throws MGXDTOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void update(GeneCoverageDTO t) throws MGXDTOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public UUID delete(long id) throws MGXDTOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
}
