package de.cebitec.mgx.client.access.rest;

import de.cebitec.mgx.client.exception.MGXClientException;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.dto.dto.SeqRunDTO;
import de.cebitec.mgx.dto.dto.SeqRunDTOList;
import java.util.Collection;

/**
 *
 * @author sjaenick
 */
public class SeqRunAccess extends AccessBase<SeqRunDTO, SeqRunDTOList> {

    public Collection<SeqRunDTO> fetchall() throws MGXServerException, MGXClientException {
        return fetchlist(SeqRunDTOList.class).getSeqrunList();
    }

    public Collection<SeqRunDTO> ByExtract(Long extract_id) throws MGXServerException, MGXClientException {
        return get(r.resolve(SeqRunDTO.class, "byExtract") + extract_id, SeqRunDTOList.class).getSeqrunList();
    }

    public Long create(SeqRunDTO sr) throws MGXServerException, MGXClientException {
        return super.create(sr, SeqRunDTO.class);
    }

}
