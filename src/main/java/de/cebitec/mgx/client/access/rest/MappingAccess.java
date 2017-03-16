package de.cebitec.mgx.client.access.rest;

import de.cebitec.gpms.rest.RESTAccessI;
import de.cebitec.mgx.client.MGXDTOMaster;
import de.cebitec.mgx.client.access.rest.util.IteratorIterator;
import de.cebitec.mgx.client.datatransfer.BAMFileDownloader;
import de.cebitec.mgx.client.exception.MGXDTOException;
import de.cebitec.mgx.dto.dto.MGXLong;
import de.cebitec.mgx.dto.dto.MGXString;
import de.cebitec.mgx.dto.dto.MappedSequenceDTO;
import de.cebitec.mgx.dto.dto.MappedSequenceDTOList;
import de.cebitec.mgx.dto.dto.MappingDTO;
import de.cebitec.mgx.dto.dto.MappingDTOList;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.WebApplicationException;

/**
 *
 * @author sjaenick
 */
public class MappingAccess extends AccessBase<MappingDTO, MappingDTOList> {

    private final MGXDTOMaster dtomaster;

    public MappingAccess(MGXDTOMaster dtomaster, RESTAccessI restAccess) {
        super(restAccess);
        this.dtomaster = dtomaster;
    }

    @Override
    public Iterator<MappingDTO> fetchall() throws MGXDTOException {
        return fetchlist(MappingDTOList.class).getMappingList().iterator();
    }

    public Iterator<MappingDTO> bySeqRun(long id) throws MGXDTOException {
        return get(MappingDTOList.class, r.resolve(MappingDTO.class, "bySeqRun", String.valueOf(id))).getMappingList().iterator();
    }

    public Iterator<MappingDTO> byReference(long id) throws MGXDTOException {
        return get(MappingDTOList.class, r.resolve(MappingDTOList.class, "byReference", String.valueOf(id))).getMappingList().iterator();
    }

    public Iterator<MappingDTO> byJob(long id) throws MGXDTOException {
        return get(MappingDTOList.class, r.resolve(MappingDTOList.class, "byJob", String.valueOf(id))).getMappingList().iterator();
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

    @SuppressWarnings("unchecked")
    public Iterator<MappedSequenceDTO> byReferenceInterval(UUID uuid, int from, int to) throws MGXDTOException {
        try {
            long duration = System.currentTimeMillis();
            Iterator<MappedSequenceDTO> ret = ListByReferenceInterval(uuid, from, to).iterator();
            duration = System.currentTimeMillis() - duration;
            Logger.getLogger(MappingAccess.class.getName()).log(Level.INFO, "{0}-{1} took {2}ms", new Object[]{from, to, duration});
            return ret;
        } catch (WebApplicationException ex) {
            Logger.getLogger(MappingAccess.class.getName()).log(Level.INFO, "Too many mappings in interval {0}-{1}, splitting query", new Object[]{from, to});
            // too much data, retry with smaller intervals
            if (ex.getMessage() != null && ex.getMessage().contains("Protocol message was too large")) {
                int mid = from + (to - from) / 2;
                Iterator<MappedSequenceDTO> firstHalf = byReferenceInterval(uuid, from, mid);
                Iterator<MappedSequenceDTO> secondHalf = byReferenceInterval(uuid, mid + 1, to);
                return new IteratorIterator<>(firstHalf, secondHalf);
            } else {
                throw ex;
            }
        }
    }

    private List<MappedSequenceDTO> ListByReferenceInterval(UUID uuid, int from, int to) throws MGXDTOException {
        return super.get(MappedSequenceDTOList.class, "Mapping", "byReferenceInterval", uuid.toString(), String.valueOf(from), String.valueOf(to)).getMappedSequenceList();
    }

    public long getMaxCoverage(UUID uuid) throws MGXDTOException {
        return super.get(MGXLong.class, "Mapping", "getMaxCoverage", uuid.toString()).getValue();
    }

    public void closeMapping(UUID uuid) throws MGXDTOException {
        super.get("Mapping", "closeMapping", uuid.toString());
    }

    public BAMFileDownloader createDownloader(long mappingId, OutputStream writer) throws MGXDTOException {
        return new BAMFileDownloader(dtomaster, getRESTAccess(), mappingId, writer);
    }

}
