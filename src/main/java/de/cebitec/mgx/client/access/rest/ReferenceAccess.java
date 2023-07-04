package de.cebitec.mgx.client.access.rest;

import de.cebitec.gpms.rest.RESTAccessI;
import de.cebitec.mgx.client.MGXDTOMaster;
import de.cebitec.mgx.client.datatransfer.ReferenceUploader;
import de.cebitec.mgx.client.exception.MGXDTOException;
import de.cebitec.mgx.dto.dto.MGXString;
import de.cebitec.mgx.dto.dto.ReferenceDTO;
import de.cebitec.mgx.dto.dto.ReferenceDTOList;
import java.io.File;
import java.util.Iterator;
import java.util.UUID;

/**
 *
 * @author belmann
 */
public class ReferenceAccess extends AccessBase<ReferenceDTO, ReferenceDTOList> {

    private final MGXDTOMaster dtomaster;

    public ReferenceAccess(MGXDTOMaster dtomaster, RESTAccessI restAccess) {
        super(restAccess);
        this.dtomaster = dtomaster;
    }

    @Override
    public ReferenceDTOList fetchall() throws MGXDTOException {
        return fetchlist(ReferenceDTOList.class);
    }

    @Override
    public long create(ReferenceDTO sr) throws MGXDTOException {
        return super.create(sr, ReferenceDTO.class);
    }

    @Override
    public void update(ReferenceDTO d) throws MGXDTOException {
        super.update(d, ReferenceDTO.class);
    }

    public UUID installGlobalReference(long id) throws MGXDTOException {
        return UUID.fromString(get(MGXString.class, "Reference", "installGlobalReference", String.valueOf(id)).getValue());
    }

    @Override
    public UUID delete(long id) throws MGXDTOException {
        return super.delete(id, ReferenceDTO.class);
    }

    @Override
    public ReferenceDTO fetch(long id) throws MGXDTOException {
        return super.fetch(id, ReferenceDTO.class);
    }

    public Iterator<ReferenceDTO> listGlobalReferences() throws MGXDTOException {
        return get(ReferenceDTOList.class, "Reference", "listGlobalReferences").getReferenceList().iterator();
    }

    public String getSequence(long id, int from, int to) throws MGXDTOException {
        return get(MGXString.class, "Reference", "getSequence", String.valueOf(id), String.valueOf(from), String.valueOf(to)).getValue().toUpperCase();
    }

    public ReferenceUploader createUploader(File localFile) throws MGXDTOException {
        return new ReferenceUploader(dtomaster, getRESTAccess(), localFile);
    }
}
