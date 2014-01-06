package de.cebitec.mgx.client.access.rest;

import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import static de.cebitec.mgx.client.access.rest.RESTMethods.PROTOBUF_TYPE;
import de.cebitec.mgx.client.exception.MGXClientException;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.dto.dto.MGXString;
import de.cebitec.mgx.dto.dto.MappedSequenceDTO;
import de.cebitec.mgx.dto.dto.MappedSequenceDTOList;
import de.cebitec.mgx.dto.dto.MappingDTO;
import de.cebitec.mgx.dto.dto.MappingDTOList;
import java.awt.EventQueue;
import java.util.Iterator;
import java.util.UUID;
import javax.net.ssl.SSLHandshakeException;

/**
 *
 * @author sjaenick
 */
public class MappingAccess extends AccessBase<MappingDTO, MappingDTOList> {

    @Override
    public Iterator<MappingDTO> fetchall() throws MGXServerException, MGXClientException {
        return fetchlist(MappingDTOList.class).getMappingList().iterator();
    }

    public Iterator<MappingDTO> BySeqRun(long id) throws MGXServerException, MGXClientException {
        return get(r.resolve(MappingDTO.class, "bySeqRun") + id, MappingDTOList.class).getMappingList().iterator();
    }

    public Iterator<MappingDTO> ByReference(long id) throws MGXServerException, MGXClientException {
        return get(r.resolve(MappingDTO.class, "byReference") + id, MappingDTOList.class).getMappingList().iterator();
    }

    @Override
    public long create(MappingDTO sr) throws MGXServerException, MGXClientException {
        return -1; // super.create(sr, MappingDTO.class);
    }

    @Override
    public void update(MappingDTO d) throws MGXServerException, MGXClientException {
        throw new UnsupportedOperationException("Not supported.");
        //super.update(d, MappingDTO.class);
    }

    @Override
    public UUID delete(long id) throws MGXServerException, MGXClientException {
        return super.delete(id, MappingDTO.class);
    }

    @Override
    public MappingDTO fetch(long id) throws MGXServerException, MGXClientException {
        return super.fetch(id, MappingDTO.class);
    }

    public UUID openMapping(long id) throws MGXServerException {
        return UUID.fromString(super.get("Mapping/openMapping/" + id, MGXString.class).getValue());
    }

    public Iterator<MappedSequenceDTO> byReferenceInterval(UUID uuid, int from, int to) throws MGXServerException, MGXClientException {
        return super.get("Mapping/" + uuid + "/" + from + "/" + to, MappedSequenceDTOList.class).getMappedSequenceList().iterator();
    }

    public void closeMapping(UUID uuid) throws MGXServerException {
        assert !EventQueue.isDispatchThread();
        try {
            ClientResponse res = getWebResource().path("Mapping/closeMapping/" + uuid).type(PROTOBUF_TYPE).accept(PROTOBUF_TYPE).get(ClientResponse.class);
            catchException(res);
        } catch (ClientHandlerException ex) {
            if (ex.getCause() != null && ex.getCause() instanceof SSLHandshakeException) {
                closeMapping(uuid); // retry
            } else {
                throw ex; // rethrow
            }
        }
    }
}
