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

    public Collection<DNAExtractDTO> fetchall() throws MGXServerException, MGXClientException {
        return fetchlist(DNAExtractDTOList.class).getExtractList();
    }

    public Collection<DNAExtractDTO> BySample(Long sample_id) throws MGXServerException, MGXClientException {
        return get(r.resolve(DNAExtractDTO.class, "bySample") + sample_id, DNAExtractDTOList.class).getExtractList();
    }

    public Long create(DNAExtractDTO d) throws MGXServerException, MGXClientException {
        return super.create(d, DNAExtractDTO.class);
    }
    
    public void update(DNAExtractDTO d) throws MGXServerException, MGXClientException {
        super.update(d, DNAExtractDTO.class);
    }
    
    public void delete(DNAExtractDTO s) throws MGXServerException, MGXClientException {
        super.delete(s.getId(), DNAExtractDTO.class);
    }
}
