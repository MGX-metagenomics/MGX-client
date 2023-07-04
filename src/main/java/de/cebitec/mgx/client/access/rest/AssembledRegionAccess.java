package de.cebitec.mgx.client.access.rest;

import de.cebitec.gpms.rest.RESTAccessI;
import de.cebitec.mgx.client.MGXDTOMaster;
import de.cebitec.mgx.client.datatransfer.GeneByAttributeDownloader;
import de.cebitec.mgx.client.exception.MGXDTOException;
import de.cebitec.mgx.dto.dto.AssembledRegionDTO;
import de.cebitec.mgx.dto.dto.AssembledRegionDTOList;
import de.cebitec.mgx.dto.dto.AttributeDTOList;
import de.cebitec.mgx.dto.dto.BinSearchResultDTO;
import de.cebitec.mgx.dto.dto.BinSearchResultDTOList;
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
public class AssembledRegionAccess extends AccessBase<AssembledRegionDTO, AssembledRegionDTOList> {

    private final MGXDTOMaster dtomaster;

    public AssembledRegionAccess(MGXDTOMaster dtomaster, RESTAccessI restAccess) {
        super(restAccess);
        this.dtomaster = dtomaster;
    }

    @Override
    public AssembledRegionDTOList fetchall() throws MGXDTOException {
        return fetchlist(AssembledRegionDTOList.class);
    }

    @Override
    public AssembledRegionDTO fetch(long id) throws MGXDTOException {
        return super.fetch(id, AssembledRegionDTO.class);
    }

    @Override
    public long create(AssembledRegionDTO s) throws MGXDTOException {
        return super.create(s, AssembledRegionDTO.class);
    }

    @Override
    public void update(AssembledRegionDTO d) throws MGXDTOException {
        super.update(d, AssembledRegionDTO.class);
    }

    @Override
    public UUID delete(long id) throws MGXDTOException {
        return super.delete(id, AssembledRegionDTO.class);
    }

    public Iterator<AssembledRegionDTO> byAssembledContig(long contig_id) throws MGXDTOException {
        return get(AssembledRegionDTOList.class, r.resolve(AssembledRegionDTOList.class, "byContig", String.valueOf(contig_id))).getRegionList().iterator();
    }

    public SequenceDTO getDNASequence(long gene_id) throws MGXDTOException {
        return get(SequenceDTO.class, r.resolve(AssembledRegionDTO.class, "getDNASequence", String.valueOf(gene_id)));
    }
    
    public Iterator<BinSearchResultDTO> search(long bin_id, String term) throws MGXDTOException {
        return get(BinSearchResultDTOList.class, r.resolve(BinSearchResultDTO.class, "search", String.valueOf(bin_id), term)).getResultList().iterator();
    }

    public GeneByAttributeDownloader createDownloaderByAttributes(AttributeDTOList attrs, SeqWriterI<? extends DNASequenceI> writer, boolean closeWriter, Set<String> seenGeneNames) throws MGXDTOException {
        return new GeneByAttributeDownloader(dtomaster, getRESTAccess(), attrs, writer, closeWriter, seenGeneNames);
    }
}
