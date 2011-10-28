package de.cebitec.mgx.client.access.rest;

import de.cebitec.mgx.client.exception.MGXClientException;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.dto.SeqRunDTO;
import de.cebitec.mgx.dto.SeqRunDTOList;
import java.util.Collection;

/**
 *
 * @author sjaenick
 */
public class SeqRunAccess<T, U> extends AccessBase<T, U> {

    @Override
    Class getType() {
        return SeqRunDTO.class;
    }

    @Override
    Class getListType() {
        return SeqRunDTOList.class;
    }

    public Collection<SeqRunDTO> fetchall() throws MGXServerException, MGXClientException {
        return ((SeqRunDTOList) fetchlist()).getSeqrunList();
    }

    public Collection<SeqRunDTO> ByExtract(Long extract_id) throws MGXServerException, MGXClientException {
        return ((SeqRunDTOList) get(r.resolve(getType(), "/byExtract/") + extract_id, getListType())).getSeqrunList();
    }

}
