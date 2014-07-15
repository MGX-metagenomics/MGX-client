package de.cebitec.mgx.client.datatransfer;

import com.google.protobuf.ByteString;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.dto.dto.BytesDTO;
import de.cebitec.mgx.dto.dto.MGXString;
import java.awt.EventQueue;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sj
 */
public class FileUploader extends UploadBase {

    private final WebResource wr;
    private final File localFile;
    private final String remoteName;
    private InputStream in = null;
    private long total_elements_sent = 0;
    private final long totalFileSize;

    public FileUploader(final WebResource wr, final File file, final String targetName) {
        super();
        this.localFile = file;
        totalFileSize = file.length();
        this.wr = wr;

        String tmp = targetName;
        if (!tmp.startsWith("./")) {
            tmp = "./" + tmp;
        }
        this.remoteName = tmp.replace("/", "|");
        int randomNess = (int) Math.round(Math.random() * 20);
        setChunkSize(4096 + randomNess);
    }

    public int getProgress() {
        float complete = 1.0f * total_elements_sent / totalFileSize;
        return Math.round(100 * complete);
    }

    @Override
    public boolean upload() {
        CallbackI cb = getProgressCallback();

        String session_uuid;
        try {
            session_uuid = initTransfer();
        } catch (MGXServerException ex) {
            abortTransfer(ex.getMessage(), total_elements_sent);
            return false;
        }

        cb.callback(total_elements_sent);

        if (in == null) {
            try {
                FileInputStream fis = new FileInputStream(localFile);
                in = new BufferedInputStream(fis);
            } catch (FileNotFoundException ex) {
                abortTransfer(ex.getMessage(), total_elements_sent);
            }
        }

        int bytesRead;
        byte[] buf = new byte[chunk_size];
        try {
            while ((bytesRead = in.read(buf)) != -1) {
                byte[] data = new byte[bytesRead];
                System.arraycopy(buf, 0, data, 0, bytesRead);
                sendChunk(data, session_uuid);
                total_elements_sent += bytesRead;
                cb.callback(total_elements_sent);
            }
        } catch (MGXServerException | IOException ex) {
            abortTransfer(ex.getMessage(), total_elements_sent);
            return false;
        } finally {
            try {
                in.close();
            } catch (IOException ex) {
                Logger.getLogger(FileUploader.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        cb.callback(total_elements_sent);
        try {
            finishTransfer(session_uuid);
        } catch (MGXServerException ex) {
            abortTransfer(ex.getMessage(), total_elements_sent);
            return false;
        }
        return true;
    }

    private String initTransfer() throws MGXServerException {
        assert !EventQueue.isDispatchThread();
        ClientResponse res = wr.path("/File/initUpload/" + remoteName).accept("application/x-protobuf").get(ClientResponse.class);
        catchException(res);
        fireTaskChange(TransferBase.NUM_ELEMENTS_SENT, total_elements_sent);
        MGXString session_uuid = res.<MGXString>getEntity(MGXString.class);
        return session_uuid.getValue();
    }

    private void sendChunk(final byte[] data, String session_uuid) throws MGXServerException {
        assert !EventQueue.isDispatchThread();
        BytesDTO rawData = BytesDTO.newBuilder().setData(ByteString.copyFrom(data)).build();
        ClientResponse res = wr.path("/File/add/" + session_uuid).type("application/x-protobuf").post(ClientResponse.class, rawData);
        catchException(res);
        fireTaskChange(TransferBase.NUM_ELEMENTS_SENT, total_elements_sent);
    }

    private void finishTransfer(String uuid) throws MGXServerException {
        assert !EventQueue.isDispatchThread();
        ClientResponse res = wr.path("/File/closeUpload/" + uuid).get(ClientResponse.class);
        catchException(res);
        fireTaskChange(TransferBase.NUM_ELEMENTS_SENT, total_elements_sent);
        fireTaskChange(TransferBase.TRANSFER_COMPLETED, total_elements_sent);
    }
}
