package de.cebitec.mgx.client.datatransfer;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.dto.dto.BytesDTO;
import de.cebitec.mgx.dto.dto.MGXString;
import java.awt.EventQueue;
import java.io.IOException;
import java.io.OutputStream;

/**
 *
 * @author sjaenick
 */
public class PluginDumpDownloader extends DownloadBase {

    protected final WebResource wr;
    protected long total_elements = 0;
    protected final OutputStream writer;

    public PluginDumpDownloader(WebResource wr, OutputStream writer) {
        super();
        this.wr = wr;
        this.writer = writer;
    }

    @Override
    public boolean download() {
        CallbackI cb = getProgressCallback();

        String session_uuid;
        try {
            session_uuid = initDownload();
        } catch (MGXServerException ex) {
            abortTransfer(ex.getMessage(), total_elements);
            return false;
        }

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
                abortTransfer(ex.getMessage(), total_elements);
                return false;
            }

            // empty sequence list indicates end of download
            need_refetch = chunk.length > 0;

            try {
                writer.write(chunk);
            } catch (IOException ex1) {
                abortTransfer(ex1.getMessage(), total_elements + chunk.length);
                return false;
            }
            total_elements += chunk.length;
            cb.callback(total_elements);
            fireTaskChange(TransferBase.NUM_ELEMENTS_RECEIVED, total_elements);
        }

        // finish the transfer
        try {
            finishTransfer(session_uuid);
        } catch (MGXServerException ex) {
            abortTransfer(ex.getMessage(), total_elements);
            return false;
        }

        try {
            writer.close();
        } catch (IOException ex) {
            abortTransfer(ex.getMessage(), total_elements);
            return false;
        }

        return true;
    }

    protected String initDownload() throws MGXServerException {
        assert !EventQueue.isDispatchThread();
        ClientResponse res = wr.path("/File/initPluginDownload/").accept("application/x-protobuf").get(ClientResponse.class);
        catchException(res);
        fireTaskChange(TransferBase.NUM_ELEMENTS_RECEIVED, total_elements);
        MGXString session_uuid = res.<MGXString>getEntity(MGXString.class);
        return session_uuid.getValue();
    }

    protected void finishTransfer(String uuid) throws MGXServerException {
        assert !EventQueue.isDispatchThread();
        ClientResponse res = wr.path("/File/closeDownload/" + uuid).get(ClientResponse.class);
        catchException(res);
        fireTaskChange(TransferBase.NUM_ELEMENTS_RECEIVED, total_elements);
        fireTaskChange(TransferBase.TRANSFER_COMPLETED, total_elements);
    }

    protected byte[] fetchChunk(String session_uuid) throws MGXServerException {
        assert !EventQueue.isDispatchThread();
        ClientResponse res = wr.path("/File/get/" + session_uuid).type("application/x-protobuf").get(ClientResponse.class);
        catchException(res);
        BytesDTO entity = res.<BytesDTO>getEntity(BytesDTO.class);
        fireTaskChange(TransferBase.NUM_ELEMENTS_RECEIVED, total_elements);
        return entity.getData().toByteArray();
    }

    public long getProgress() {
        return total_elements;
    }
}
