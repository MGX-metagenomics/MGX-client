package de.cebitec.mgx.client.access.rest;

import de.cebitec.gpms.rest.RESTAccessI;
import de.cebitec.mgx.client.MGXDTOMaster;
import de.cebitec.mgx.client.datatransfer.SeqByAttributeDownloader;
import de.cebitec.mgx.client.datatransfer.SeqDownloader;
import de.cebitec.mgx.client.datatransfer.SeqUploader;
import de.cebitec.mgx.client.exception.MGXClientException;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.dto.dto.AttributeDTOList;
import de.cebitec.mgx.dto.dto.MGXLongList;
import de.cebitec.mgx.dto.dto.MGXLongList.Builder;
import de.cebitec.mgx.dto.dto.MGXString;
import de.cebitec.mgx.dto.dto.SequenceDTO;
import de.cebitec.mgx.dto.dto.SequenceDTOList;
import de.cebitec.mgx.sequence.DNASequenceI;
import de.cebitec.mgx.sequence.SeqReaderI;
import de.cebitec.mgx.sequence.SeqWriterI;
import java.util.Iterator;
import java.util.UUID;

/**
 *
 * @author sjaenick
 */
public class SequenceAccess extends AccessBase<SequenceDTO, SequenceDTOList> {

    private final MGXDTOMaster dtomaster;

    public SequenceAccess(MGXDTOMaster dtomaster, RESTAccessI restAccess) {
        super(restAccess);
        this.dtomaster = dtomaster;
    }

    public void sendSequences(long seqrun_id, SeqReaderI<? extends DNASequenceI> reader) throws MGXServerException {
        SeqUploader seqUploader = new SeqUploader(dtomaster, getRESTAccess(), seqrun_id, reader);
        boolean success = seqUploader.upload();
        if (!success) {
            throw new MGXServerException(seqUploader.getErrorMessage());
        }
    }

    public void downloadSequences(long seqrun_id, SeqWriterI<DNASequenceI> writer, boolean closeWriter) throws MGXServerException {
        SeqDownloader dl = new SeqDownloader(dtomaster, getRESTAccess(), seqrun_id, writer, closeWriter);
        boolean success = dl.download();
        if (!success) {
            throw new MGXServerException(dl.getErrorMessage());
        }
    }

    public SeqUploader createUploader(long seqrun_id, SeqReaderI<? extends DNASequenceI> reader) {
        return new SeqUploader(dtomaster, getRESTAccess(), seqrun_id, reader);
    }

    public SeqDownloader createDownloader(long seqrun_id, SeqWriterI<DNASequenceI> writer, boolean closeWriter) {
        return new SeqDownloader(dtomaster, getRESTAccess(), seqrun_id, writer, closeWriter);
    }

    @Override
    public SequenceDTO fetch(long id) throws MGXServerException, MGXClientException {
        return super.fetch(id, SequenceDTO.class);
    }

    public SequenceDTOList fetchSeqData(long[] ids) throws MGXServerException, MGXClientException {
        String resolve[] = r.resolve(SequenceDTOList.class, "fetchall");
        Builder b = MGXLongList.newBuilder();
        for (Long id : ids) {
            b.addLong(id);
        }
        return super.<SequenceDTOList>put(b.build(), SequenceDTOList.class, resolve);
    }
    
    
    public SequenceDTO byName(long runId, String seqName) throws MGXServerException, MGXClientException {
        String resolve = r.resolveClass(SequenceDTO.class);
        String[] path = new String[]{resolve, "byName", String.valueOf(runId)};
        MGXString seqNameDTO = MGXString.newBuilder().setValue(seqName).build();
        return super.put(seqNameDTO, SequenceDTO.class, path);
    }

    public SeqByAttributeDownloader createDownloaderByAttributes(AttributeDTOList attrs, SeqWriterI<DNASequenceI> writer, boolean closeWriter) {
        return new SeqByAttributeDownloader(dtomaster, getRESTAccess(), attrs, writer, closeWriter);
    }

    public void fetchAnnotatedReads(AttributeDTOList attrs, SeqWriterI<DNASequenceI> writer, boolean closeWriter) throws MGXServerException {
        SeqByAttributeDownloader dl = new SeqByAttributeDownloader(dtomaster, getRESTAccess(), attrs, writer, closeWriter);
        boolean success = dl.download();
        if (!success) {
            throw new MGXServerException(dl.getErrorMessage());
        }
    }

    @Override
    public Iterator<SequenceDTO> fetchall() throws MGXServerException, MGXClientException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public long create(SequenceDTO t) throws MGXServerException, MGXClientException {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public void update(SequenceDTO t) throws MGXServerException, MGXClientException {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public UUID delete(long id) throws MGXServerException, MGXClientException {
        throw new UnsupportedOperationException("Not supported.");
    }

    protected static String join(Iterable< ? extends Object> pColl, String separator) {
        Iterator< ? extends Object> oIter;
        if (pColl == null || (!(oIter = pColl.iterator()).hasNext())) {
            return "";
        }
        StringBuilder oBuilder = new StringBuilder(String.valueOf(oIter.next()));
        while (oIter.hasNext()) {
            oBuilder.append(separator).append(oIter.next());
        }
        return oBuilder.toString();
    }

}
