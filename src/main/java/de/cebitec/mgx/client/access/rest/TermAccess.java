package de.cebitec.mgx.client.access.rest;

import de.cebitec.mgx.client.exception.MGXClientException;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.dto.dto.TermDTO;
import de.cebitec.mgx.dto.dto.TermDTOList;
import java.util.Collection;
import java.util.Iterator;

/**
 *
 * @author sjaenick
 */
public class TermAccess extends AccessBase<TermDTO, TermDTOList> {

    // ontology lookup categories
    public static final String SEQ_METHODS = "seq_methods";
    public static final String SEQ_PLATFORMS = "seq_platforms";

    public Collection<TermDTO> byCategory(String cat) throws MGXServerException, MGXClientException {
        return get("/Term/byCategory/" + cat, TermDTOList.class).getTermList();
    }

    @Override
    public TermDTO fetch(long id) throws MGXServerException, MGXClientException {
        return get("/Term/fetch/" + id, TermDTO.class);
    }

    @Override
    public Iterator<TermDTO> fetchall() throws MGXServerException, MGXClientException {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public long create(TermDTO t) throws MGXServerException, MGXClientException {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public void update(TermDTO t) throws MGXServerException, MGXClientException {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public boolean delete(long id) throws MGXServerException, MGXClientException {
        throw new UnsupportedOperationException("Not supported.");
    }
}
