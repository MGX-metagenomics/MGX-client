package de.cebitec.mgx.client.datatransfer;

import de.cebitec.gpms.rest.RESTAccessI;
import de.cebitec.mgx.client.MGXDTOMaster;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.dto.dto.BytesDTO;
import de.cebitec.mgx.dto.dto.MGXString;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 *
 * @author sjaenick
 */
public class FileDownloader extends DownloadBase {

    protected long total_elements = 0;
    protected final OutputStream writer;
    protected final String serverFname;

    public FileDownloader(MGXDTOMaster dtomaster, RESTAccessI rab, String serverFname, OutputStream writer) {
        super(dtomaster, rab);
        this.serverFname = serverFname;
        this.writer = writer;
    }

    @Override
    public boolean download() {
        CallbackI cb = getProgressCallback();

        String session_uuid;
        try {
            session_uuid = initDownload();
        } catch (MGXServerException | UnsupportedEncodingException ex) {
            abortTransfer(ex.getMessage());
            return false;
        }

        fireTaskChange(TransferBase.NUM_ELEMENTS_TRANSFERRED, total_elements);
        cb.callback(total_elements);

        boolean need_refetch = true;
        while (need_refetch) {
            byte[] chunk;
            try {
                chunk = fetchChunk(session_uuid);
            } catch (MGXServerException ex) {
                try {
                    writer.close();
                } catch (IOException ex1) {
                }
                abortTransfer(ex.getMessage());
                return false;
            }

            // empty chunk indicates end of download
            need_refetch = chunk.length > 0;

            try {
                writer.write(chunk);
            } catch (IOException ex1) {
                abortTransfer(ex1.getMessage());
                return false;
            }
            total_elements += chunk.length;
            cb.callback(total_elements);
            fireTaskChange(TransferBase.NUM_ELEMENTS_TRANSFERRED, total_elements);
        }

        // finish the transfer
        try {
            finishTransfer(session_uuid);
        } catch (MGXServerException ex) {
            abortTransfer(ex.getMessage());
            return false;
        }

        try {
            writer.close();
        } catch (IOException ex) {
            abortTransfer(ex.getMessage());
            return false;
        }

        return true;
    }

    protected String initDownload() throws MGXServerException, UnsupportedEncodingException {
        MGXString session_uuid = super.get(MGXString.class, "File", "initDownload", URLEncoder.encode(serverFname, "UTF-8"));
        //fireTaskChange(TransferBase.NUM_ELEMENTS_TRANSFERRED, total_elements);
        return session_uuid.getValue();
    }

    protected void finishTransfer(String uuid) throws MGXServerException {
        super.get("File", "closeDownload", uuid);
        //fireTaskChange(TransferBase.NUM_ELEMENTS_TRANSFERRED, total_elements);
        fireTaskChange(TransferBase.TRANSFER_COMPLETED, total_elements);
    }

    protected byte[] fetchChunk(String session_uuid) throws MGXServerException {
        BytesDTO entity = super.get(BytesDTO.class, "File", "get", session_uuid);
        //fireTaskChange(TransferBase.NUM_ELEMENTS_TRANSFERRED, total_elements);
        return entity.getData().toByteArray();
    }

    @Override
    public long getProgress() {
        return total_elements;
    }
}
