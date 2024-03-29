package de.cebitec.mgx.client.datatransfer;

import com.google.protobuf.ByteString;
import de.cebitec.gpms.rest.RESTAccessI;
import de.cebitec.mgx.client.MGXDTOMaster;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.dto.dto.BytesDTO;
import de.cebitec.mgx.dto.dto.MGXString;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sj
 */
public class FileUploader extends UploadBase {

    private final File localFile;
    private final String remoteName;
    private InputStream in = null;
    private long total_elements_sent = 0;

    public FileUploader(MGXDTOMaster dtomaster, RESTAccessI rab, final File file, final String targetName) {
        super(dtomaster, rab);
        this.localFile = file;

        String tmp = targetName;
        if (!tmp.startsWith("./")) {
            tmp = "./" + tmp;
        }
        this.remoteName = tmp.replace("/", "|");
        int randomNess = (int) Math.round(Math.random() * 20);
        super.setChunkSize(1024 * 256 + randomNess);
    }

    @Override
    public final long getProgress() {
        return total_elements_sent;
    }

    @Override
    public final boolean upload() {

        String session_uuid;
        try {
            session_uuid = initTransfer();
        } catch (MGXServerException | UnsupportedEncodingException ex) {
            abortTransfer(ex.getMessage());
            return false;
        }

        fireTaskChange(TransferBase.NUM_ELEMENTS_TRANSFERRED, total_elements_sent);

        if (in == null) {
            try {
                FileInputStream fis = new FileInputStream(localFile);
                in = new BufferedInputStream(fis);
            } catch (FileNotFoundException ex) {
                abortTransfer(ex.getMessage());
                return false;
            }
        }

        int bytesRead;
        byte[] buf = new byte[getChunkSize()];
        try {
            while (!cancelled && (bytesRead = in.read(buf)) != -1) {
                byte[] data = new byte[bytesRead];
                System.arraycopy(buf, 0, data, 0, bytesRead);
                total_elements_sent += bytesRead;
                sendChunk(data, session_uuid);
            }
        } catch (MGXServerException | IOException ex) {
            abortTransfer(ex.getMessage());
            return false;
        } finally {
            try {
                in.close();
            } catch (IOException ex) {
                Logger.getLogger(FileUploader.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        fireTaskChange(TransferBase.NUM_ELEMENTS_TRANSFERRED, total_elements_sent);

        try {
            if (!cancelled) {
                finishTransfer(session_uuid);
            }
        } catch (MGXServerException ex) {
            abortTransfer(ex.getMessage());
            return false;
        }

        if (!cancelled) {
            fireTaskChange(TransferBase.TRANSFER_COMPLETED, total_elements_sent);
            return true;
        } else {
            try {
                super.get("File", "cancel", session_uuid);
            } catch (MGXServerException ex) {
                Logger.getLogger(FileUploader.class.getName()).log(Level.SEVERE, null, ex);
            }
            fireTaskChange(TransferBase.TRANSFER_ABORTED, total_elements_sent);
            return false;
        }

    }

    private String initTransfer() throws MGXServerException, UnsupportedEncodingException {
        MGXString session_uuid = super.get(MGXString.class, "File", "initUpload", URLEncoder.encode(remoteName, "UTF-8"));
        fireTaskChange(TransferBase.NUM_ELEMENTS_TRANSFERRED, total_elements_sent);
        return session_uuid.getValue();
    }

    private void sendChunk(final byte[] data, String session_uuid) throws MGXServerException {
        BytesDTO rawData = BytesDTO.newBuilder().setData(ByteString.copyFrom(data)).build();
        super.post(rawData, "File", "add", session_uuid);
        fireTaskChange(TransferBase.NUM_ELEMENTS_TRANSFERRED, total_elements_sent);
    }

    private void finishTransfer(String uuid) throws MGXServerException {
        super.get("File", "closeUpload", uuid);
        fireTaskChange(TransferBase.NUM_ELEMENTS_TRANSFERRED, total_elements_sent);
        super.dispose();
    }
}
