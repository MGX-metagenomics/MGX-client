package de.cebitec.mgx.client.access.rest;

import de.cebitec.mgx.client.exception.MGXClientException;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.dto.dto.DNAExtractDTO;
import de.cebitec.mgx.dto.dto.DNAExtractDTOList;
import java.util.Collection;

/**
 *
 * @author sjaenick
 */
public class DNAExtractAccess extends AccessBase<DNAExtractDTO, DNAExtractDTOList> {

    @Override
    public Collection<DNAExtractDTO> fetchall() throws MGXServerException, MGXClientException {
        return fetchlist(DNAExtractDTOList.class).getExtractList();
    }

    public Collection<DNAExtractDTO> BySample(long sample_id) throws MGXServerException, MGXClientException {
        return get(r.resolve(DNAExtractDTO.class, "bySample") + sample_id, DNAExtractDTOList.class).getExtractList();
    }
    
    @Override
    public DNAExtractDTO fetch(Long id) throws MGXServerException, MGXClientException {
        return super.fetch(id, DNAExtractDTO.class);
    }

    @Override
    public Long create(DNAExtractDTO d) throws MGXServerException, MGXClientException {
        return super.create(d, DNAExtractDTO.class);
    }
    
    @Override
    public void update(DNAExtractDTO d) throws MGXServerException, MGXClientException {
        super.update(d, DNAExtractDTO.class);
    }
    
    @Override
    public void delete(long id) throws MGXServerException, MGXClientException {
        super.delete(id, DNAExtractDTO.class);
    }
}
