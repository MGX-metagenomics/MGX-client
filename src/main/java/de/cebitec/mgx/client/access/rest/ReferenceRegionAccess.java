package de.cebitec.mgx.client.access.rest;

import de.cebitec.gpms.rest.RESTAccessI;
import de.cebitec.mgx.client.MGXDTOMaster;
import de.cebitec.mgx.client.datatransfer.GeneByAttributeDownloader;
import de.cebitec.mgx.client.exception.MGXDTOException;
import de.cebitec.mgx.dto.dto.AttributeDTOList;
import de.cebitec.mgx.dto.dto.ReferenceRegionDTO;
import de.cebitec.mgx.dto.dto.ReferenceRegionDTOList;
import de.cebitec.mgx.dto.dto.SequenceDTO;
import de.cebitec.mgx.sequence.DNASequenceI;
import de.cebitec.mgx.sequence.SeqWriterI;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

/**
 *
 * @author sjaenick
 */
public class ReferenceRegionAccess extends AccessBase<ReferenceRegionDTO, ReferenceRegionDTOList> {

    private final MGXDTOMaster dtomaster;

    public ReferenceRegionAccess(MGXDTOMaster dtomaster, RESTAccessI restAccess) {
        super(restAccess);
        this.dtomaster = dtomaster;
    }

    @Override
    public Iterator<ReferenceRegionDTO> fetchall() throws MGXDTOException {
        return fetchlist(ReferenceRegionDTOList.class).getRegionList().iterator();
    }

    @Override
    public ReferenceRegionDTO fetch(long id) throws MGXDTOException {
        return super.fetch(id, ReferenceRegionDTO.class);
    }

    @Override
    public long create(ReferenceRegionDTO s) throws MGXDTOException {
        return super.create(s, ReferenceRegionDTO.class);
    }

    @Override
    public void update(ReferenceRegionDTO d) throws MGXDTOException {
        super.update(d, ReferenceRegionDTO.class);
    }

    @Override
    public UUID delete(long id) throws MGXDTOException {
        return super.delete(id, ReferenceRegionDTO.class);
    }

    public Iterator<ReferenceRegionDTO> byReference(long reference_id) throws MGXDTOException {
        return get(ReferenceRegionDTOList.class, "ReferenceRegion", "byReference", String.valueOf(reference_id)).getRegionList().iterator();
    }

    public Iterator<ReferenceRegionDTO> byReferenceInterval(long reference_id, int from, int to) throws MGXDTOException {
        return get(ReferenceRegionDTOList.class, "ReferenceRegion", "byReferenceInterval", String.valueOf(reference_id), String.valueOf(from), String.valueOf(to)).getRegionList().iterator();
    }

    public SequenceDTO getDNASequence(long region_id) throws MGXDTOException {
        return get(SequenceDTO.class, "ReferenceRegion", "getDNASequence", String.valueOf(region_id));
    }

    public GeneByAttributeDownloader createDownloaderByAttributes(AttributeDTOList attrs, SeqWriterI<? extends DNASequenceI> writer, boolean closeWriter, Set<String> seenGeneNames) throws MGXDTOException {
        return new GeneByAttributeDownloader(dtomaster, getRESTAccess(), attrs, writer, closeWriter, seenGeneNames);
    }
}
