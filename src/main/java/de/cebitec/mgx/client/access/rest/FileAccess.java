package de.cebitec.mgx.client.access.rest;

import de.cebitec.mgx.client.exception.MGXClientException;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.client.upload.FileUploader;
import de.cebitec.mgx.dto.dto.FileDTO;
import de.cebitec.mgx.dto.dto.FileDTOList;
import java.io.FileReader;
import java.util.Collection;

/**
 *
 * @author sjaenick
 */
public class FileAccess extends AccessBase<FileDTO, FileDTOList> {

    public Collection<FileDTO> fetchall(String rootDir) throws MGXServerException, MGXClientException {
        //System.err.println("request dir listing for " + rootDir);
        rootDir = rootDir.replace("/", "|");
        String resolve = r.resolve(FileDTOList.class, "fetchall");
        return this.get(resolve + rootDir, FileDTOList.class).getFileList();
    }

    @Override
    public void delete(long id) throws MGXServerException, MGXClientException {
        throw new UnsupportedOperationException("Not supported.");
    }

    public void delete(FileDTO dto) throws MGXServerException, MGXClientException {
        String path = dto.getName().replace("/", "|");
        String resolve = r.resolve(FileDTO.class, "delete");
        this.delete(resolve + path);
    }

    @Override
    public FileDTO fetch(long id) throws MGXServerException, MGXClientException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public long create(FileDTO t) throws MGXServerException, MGXClientException {
        // this method is only used to create directories; files
        // are created using the upload mechanism
        assert t.getIsDirectory();
        return super.create(t, FileDTO.class);
    }

    @Override
    public void update(FileDTO t) throws MGXServerException, MGXClientException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Collection<FileDTO> fetchall() throws MGXServerException, MGXClientException {
        return fetchall(".");
    }

    public FileUploader createUploader(String fullPath, FileReader reader) {
        return new FileUploader(getWebResource(), reader, fullPath);
    }
}
