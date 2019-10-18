package de.cebitec.mgx.client.access.rest;

import de.cebitec.gpms.rest.RESTAccessI;
import de.cebitec.mgx.client.MGXDTOMaster;
import de.cebitec.mgx.client.datatransfer.GeneByAttributeDownloader;
import de.cebitec.mgx.client.exception.MGXDTOException;
import de.cebitec.mgx.dto.dto.AttributeDTOList;
import de.cebitec.mgx.dto.dto.GeneDTO;
import de.cebitec.mgx.dto.dto.GeneDTOList;
import de.cebitec.mgx.dto.dto.SequenceDTO;
import de.cebitec.mgx.sequence.DNASequenceI;
import de.cebitec.mgx.sequence.SeqWriterI;
import java.util.Iterator;
import java.util.UUID;

/**
 *
 * @author sjaenick
 */
public class GeneAccess extends AccessBase<GeneDTO, GeneDTOList> {

    private final MGXDTOMaster dtomaster;

    public GeneAccess(MGXDTOMaster dtomaster, RESTAccessI restAccess) {
        super(restAccess);
        this.dtomaster = dtomaster;
    }

    @Override
    public Iterator<GeneDTO> fetchall() throws MGXDTOException {
        return fetchlist(GeneDTOList.class).getGeneList().iterator();
    }

    @Override
    public GeneDTO fetch(long id) throws MGXDTOException {
        return super.fetch(id, GeneDTO.class);
    }

    @Override
    public long create(GeneDTO s) throws MGXDTOException {
        return super.create(s, GeneDTO.class);
    }

    @Override
    public void update(GeneDTO d) throws MGXDTOException {
        super.update(d, GeneDTO.class);
    }

    @Override
    public UUID delete(long id) throws MGXDTOException {
        return super.delete(id, GeneDTO.class);
    }

    public Iterator<GeneDTO> byContig(long contig_id) throws MGXDTOException {
        return get(GeneDTOList.class, r.resolve(GeneDTOList.class, "byContig", String.valueOf(contig_id))).getGeneList().iterator();
    }

    public SequenceDTO getDNASequence(long gene_id) throws MGXDTOException {
        return get(SequenceDTO.class, r.resolve(GeneDTO.class, "getDNASequence", String.valueOf(gene_id)));
    }

    public GeneByAttributeDownloader createDownloaderByAttributes(AttributeDTOList attrs, SeqWriterI<? extends DNASequenceI> writer, boolean closeWriter) {
        return new GeneByAttributeDownloader(dtomaster, getRESTAccess(), attrs, writer, closeWriter);
    }
}
