package de.cebitec.mgx.client.upload;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.dto.dto.MGXString;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sj
 */
public class FileUploader extends UploadBase {

    private WebResource wr;
    private BufferedReader reader;
    private String remoteName;
    private long total_elements_sent = 0;

    public FileUploader(WebResource wr, FileReader reader, String remoteName) {
        super();
        this.wr = wr;
        this.reader = new BufferedReader(reader);
        this.remoteName = remoteName;
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
        char[] cbuf = new char[chunk_size];
        int charsRead;
        try {
            while ((charsRead = reader.read(cbuf)) != -1) {
                char[] data = new char[charsRead];
                System.arraycopy(cbuf, 0, data, 0, charsRead);
                sendChunk(data, session_uuid);
            }
        } catch (MGXServerException | IOException ex) {
            abortTransfer(ex.getMessage(), total_elements_sent);
            return false;
        } finally {
            try {
                reader.close();
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
        ClientResponse res = wr.path("/File/init/" + remoteName).accept("application/x-protobuf").get(ClientResponse.class);
        catchException(res);
        fireTaskChange(total_elements_sent);
        MGXString session_uuid = res.<MGXString>getEntity(MGXString.class);
        return session_uuid.getValue();
    }

    private void sendChunk(char[] data, String session_uuid) throws MGXServerException {
        ClientResponse res = wr.path("/File/add/" + session_uuid).type("application/x-protobuf").post(ClientResponse.class, data);
        catchException(res);
        fireTaskChange(total_elements_sent);
    }

    private void finishTransfer(String uuid) throws MGXServerException {
        ClientResponse res = wr.path("/File/close/" + uuid).get(ClientResponse.class);
        catchException(res);
        fireTaskChange(total_elements_sent);
    }
}
