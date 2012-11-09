package de.cebitec.mgx.client.access.rest;

import de.cebitec.mgx.client.exception.MGXClientException;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.dto.dto.FileDTO;
import de.cebitec.mgx.dto.dto.FileDTOList;
import java.util.Collection;

/**
 *
 * @author sjaenick
 */
public class FileAccess extends AccessBase<FileDTO, FileDTOList> {
    
    public Collection<FileDTO> fetchall(String rootDir) throws MGXServerException, MGXClientException {
//        try {
//            rootDir = URLEncoder.encode(rootDir, "UTF-8");
//        } catch (UnsupportedEncodingException ex) {
//            throw new MGXClientException(ex.getMessage());
//        }

        System.err.println("request dir listing for "+rootDir);
        rootDir = rootDir.replace("/", "|");
        String resolve = r.resolve(FileDTOList.class, "fetchall");
        return this.get(resolve + rootDir, FileDTOList.class).getFileList();
    }

    @Override
    public void delete(long id) throws MGXServerException, MGXClientException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public FileDTO fetch(long id) throws MGXServerException, MGXClientException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public long create(FileDTO t) throws MGXServerException, MGXClientException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void update(FileDTO t) throws MGXServerException, MGXClientException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Collection<FileDTO> fetchall() throws MGXServerException, MGXClientException {
        return fetchall(".");
    }
}
