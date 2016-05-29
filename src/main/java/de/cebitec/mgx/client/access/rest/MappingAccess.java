package de.cebitec.mgx.client.access.rest;

import de.cebitec.gpms.rest.RESTAccessI;
import de.cebitec.mgx.client.exception.MGXDTOException;
import de.cebitec.mgx.dto.dto.MGXLong;
import de.cebitec.mgx.dto.dto.MGXString;
import de.cebitec.mgx.dto.dto.MappedSequenceDTO;
import de.cebitec.mgx.dto.dto.MappedSequenceDTOList;
import de.cebitec.mgx.dto.dto.MappingDTO;
import de.cebitec.mgx.dto.dto.MappingDTOList;
import java.util.Iterator;
import java.util.UUID;

/**
 *
 * @author sjaenick
 */
public class MappingAccess extends AccessBase<MappingDTO, MappingDTOList> {

    
    public MappingAccess(RESTAccessI restAccess) {
        super(restAccess);
    }

    @Override
    public Iterator<MappingDTO> fetchall() throws MGXDTOException {
        return fetchlist(MappingDTOList.class).getMappingList().iterator();
    }

    public Iterator<MappingDTO> BySeqRun(long id) throws MGXDTOException {
        return get(MappingDTOList.class, r.resolve(MappingDTO.class, "bySeqRun", String.valueOf(id))).getMappingList().iterator();
    }

    public Iterator<MappingDTO> ByReference(long id) throws MGXDTOException {
        return get(MappingDTOList.class, r.resolve(MappingDTOList.class, "byReference", String.valueOf(id))).getMappingList().iterator();
    }

    @Override
    public long create(MappingDTO sr) throws MGXDTOException {
        return -1; // super.create(sr, MappingDTO.class);
    }

    @Override
    public void update(MappingDTO d) throws MGXDTOException {
        throw new UnsupportedOperationException("Not supported.");
        //super.update(d, MappingDTO.class);
    }

    @Override
    public UUID delete(long id) throws MGXDTOException {
        return super.delete(id, MappingDTO.class);
    }

    @Override
    public MappingDTO fetch(long id) throws MGXDTOException {
        return super.fetch(id, MappingDTO.class);
    }

    public UUID openMapping(long id) throws MGXDTOException {
        return UUID.fromString(super.get(MGXString.class, "Mapping", "openMapping", String.valueOf(id)).getValue());
    }

    public Iterator<MappedSequenceDTO> byReferenceInterval(UUID uuid, int from, int to) throws MGXDTOException {
        return super.get(MappedSequenceDTOList.class, "Mapping", "byReferenceInterval", uuid.toString(), String.valueOf(from), String.valueOf(to))
                .getMappedSequenceList().iterator();
    }

    public long getMaxCoverage(UUID uuid) throws MGXDTOException {
        return super.get(MGXLong.class, "Mapping", "getMaxCoverage", uuid.toString()).getValue();
    }

    public void closeMapping(UUID uuid) throws MGXDTOException {
        super.get("Mapping", "closeMapping", uuid.toString());
    }
}
