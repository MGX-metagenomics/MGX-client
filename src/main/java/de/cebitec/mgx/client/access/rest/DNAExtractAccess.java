package de.cebitec.mgx.client.access.rest;

import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.dto.DNAExtractDTO;
import de.cebitec.mgx.dto.DNAExtractDTOList;
import java.util.Collection;

/**
 *
 * @author sjaenick
 */
public class DNAExtractAccess<T,U> extends AccessBase<T,U> {

    @Override
    Class getType() {
        return DNAExtractDTO.class;
    }

    @Override
    Class getListType() {
        return DNAExtractDTOList.class;
    }

    public Collection<DNAExtractDTO> fetchall() throws MGXServerException {
        return get("/DNAExtract/fetchall", DNAExtractDTOList.class).getExtractList();
    }

    public Collection<DNAExtractDTO> BySample(Long sample_id) throws MGXServerException {
        return get("/DNAExtract/bySample/"+sample_id, DNAExtractDTOList.class).getExtractList();
    }
}
