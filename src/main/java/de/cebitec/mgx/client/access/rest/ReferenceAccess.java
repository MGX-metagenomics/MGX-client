package de.cebitec.mgx.client.access.rest;

import de.cebitec.mgx.client.datatransfer.ReferenceUploader;
import de.cebitec.mgx.client.exception.MGXClientException;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.dto.dto.MGXLong;
import de.cebitec.mgx.dto.dto.MGXString;
import de.cebitec.mgx.dto.dto.ReferenceDTO;
import de.cebitec.mgx.dto.dto.ReferenceDTOList;
import de.cebitec.mgx.dto.dto.RegionDTO;
import de.cebitec.mgx.dto.dto.RegionDTOList;
import java.io.File;
import java.util.Iterator;
import java.util.UUID;

/**
 *
 * @author belmann
 */
public class ReferenceAccess extends AccessBase<ReferenceDTO, ReferenceDTOList> {

    @Override
    public Iterator<ReferenceDTO> fetchall() throws MGXServerException, MGXClientException {
        return fetchlist(ReferenceDTOList.class).getReferenceList().iterator();
    }

    @Override
    public long create(ReferenceDTO sr) throws MGXServerException, MGXClientException {
        return super.create(sr, ReferenceDTO.class);
    }

    @Override
    public void update(ReferenceDTO d) throws MGXServerException, MGXClientException {
        super.update(d, ReferenceDTO.class);
    }

    public long installGlobalReference(long id) throws MGXServerException {
        return get(MGXLong.class, "Reference", "installGlobalReference", String.valueOf(id)).getValue();
    }

    @Override
    public UUID delete(long id) throws MGXServerException, MGXClientException {
        return super.delete(id, ReferenceDTO.class);
    }

    @Override
    public ReferenceDTO fetch(long id) throws MGXServerException, MGXClientException {
        return super.fetch(id, ReferenceDTO.class);
    }

    public Iterator<ReferenceDTO> listGlobalReferences() throws MGXServerException {
        return get(ReferenceDTOList.class, "Reference", "listGlobalReferences").getReferenceList().iterator();
    }

    public Iterator<RegionDTO> byReferenceInterval(long id, int from, int to) throws MGXClientException, MGXServerException {
        return get(RegionDTOList.class, "Reference", "byReferenceInterval", String.valueOf(id), String.valueOf(from), String.valueOf(to)).getRegionList().iterator();
    }

    public String getSequence(long id, int from, int to) throws MGXClientException, MGXServerException {
        return get(MGXString.class, "Reference", "getSequence", String.valueOf(id), String.valueOf(from), String.valueOf(to)).getValue().toLowerCase();
    }

    public ReferenceUploader createUploader(File localFile) {
        return new ReferenceUploader(getWebResource(), localFile);
    }
}
