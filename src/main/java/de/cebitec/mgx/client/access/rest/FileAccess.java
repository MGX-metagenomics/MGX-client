package de.cebitec.mgx.client.access.rest;

import de.cebitec.gpms.rest.RESTAccessI;
import de.cebitec.mgx.client.MGXDTOMaster;
import de.cebitec.mgx.client.datatransfer.FileDownloader;
import de.cebitec.mgx.client.datatransfer.FileUploader;
import de.cebitec.mgx.client.datatransfer.PluginDumpDownloader;
import de.cebitec.mgx.client.exception.MGXClientException;
import de.cebitec.mgx.client.exception.MGXDTOException;
import de.cebitec.mgx.dto.dto.FileDTO;
import de.cebitec.mgx.dto.dto.FileDTOList;
import de.cebitec.mgx.dto.dto.MGXString;
import java.io.File;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.UUID;

/**
 *
 * @author sjaenick
 */
public class FileAccess extends AccessBase<FileDTO, FileDTOList> {

    public static final String ROOT = ".";
    public static final String separator = "|";
    //
    private final MGXDTOMaster dtomaster;

    public FileAccess(MGXDTOMaster dtomaster, RESTAccessI restAccess) {
        super(restAccess);
        this.dtomaster = dtomaster;
    }

    public Iterator<FileDTO> fetchall(String baseDir) throws MGXDTOException {
        if (!baseDir.startsWith(ROOT)) {
            throw new MGXClientException("Invalid path: " + baseDir);
        }
        baseDir = baseDir.replace("/", "|");
        String[] resolve = r.resolve(FileDTOList.class, "fetchall", baseDir);
        return this.get(FileDTOList.class, resolve).getFileList().iterator();
    }

    @Override
    public UUID delete(long id) throws MGXDTOException {
        throw new UnsupportedOperationException("Not supported.");
    }

    public UUID delete(FileDTO dto) throws MGXDTOException {
        if (!dto.getName().startsWith(ROOT)) {
            throw new MGXClientException("Invalid path: " + dto.getName());
        }
        String path = dto.getName().replace("/", "|");
        String[] resolve = r.resolve(FileDTO.class, "delete", path);
        return UUID.fromString(delete(MGXString.class, resolve).getValue());
    }

    @Override
    public FileDTO fetch(long id) throws MGXDTOException {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public long create(FileDTO t) throws MGXDTOException {
        if (!t.getName().startsWith(ROOT)) {
            throw new MGXClientException("Invalid target path: " + t.getName());
        }
        if (t.getName().contains("..")) {
            throw new MGXClientException("Invalid characters in path: ..");
        }
        // this method is only used to create directories; files
        // are created using the upload mechanism
        if (!t.getIsDirectory()) {
            throw new MGXClientException(t.getName() + " is not a directory.");
        }
        return super.create(t, FileDTO.class);
    }

    @Override
    public void update(FileDTO t) throws MGXDTOException {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public Iterator<FileDTO> fetchall() throws MGXDTOException {
        return fetchall(".|");
    }

    public FileUploader createUploader(File localFile, String remotePath) throws MGXDTOException {
        if (!localFile.exists() || !localFile.canRead()) {
            throw new MGXClientException("Cannot access local file: " + localFile.getAbsolutePath());
        }
        if (!remotePath.startsWith(ROOT)) {
            throw new MGXClientException("Invalid target path: " + remotePath);
        }
        return new FileUploader(dtomaster, getRESTAccess(), localFile, remotePath);
    }

    public FileDownloader createDownloader(String serverFname, OutputStream writer) throws MGXDTOException {
        if (!serverFname.startsWith(ROOT)) {
            throw new MGXClientException("Invalid target path: " + serverFname);
        }
        return new FileDownloader(dtomaster, getRESTAccess(), serverFname, writer);
    }

    public PluginDumpDownloader createPluginDumpDownloader(OutputStream writer) throws MGXDTOException {
        return new PluginDumpDownloader(dtomaster, getRESTAccess(), writer);
    }
}
