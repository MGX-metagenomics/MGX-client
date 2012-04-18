package de.cebitec.mgx.client.access.rest;

import de.cebitec.mgx.client.exception.MGXClientException;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.dto.dto.FileOrDirectory;
import de.cebitec.mgx.dto.dto.FoDList;
import java.util.Collection;

/**
 *
 * @author sjaenick
 */
public class FileAccess extends AccessBase<FileOrDirectory, FoDList> {

    @Override
    public Collection<FileOrDirectory> fetchall() throws MGXServerException, MGXClientException {
        return fetchlist(FoDList.class).getEntryList();
    }

    @Override
    public void delete(long id) throws MGXServerException, MGXClientException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public FileOrDirectory fetch(long id) throws MGXServerException, MGXClientException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public long create(FileOrDirectory t) throws MGXServerException, MGXClientException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void update(FileOrDirectory t) throws MGXServerException, MGXClientException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
