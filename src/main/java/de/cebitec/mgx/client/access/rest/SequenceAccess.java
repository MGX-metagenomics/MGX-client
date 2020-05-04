package de.cebitec.mgx.client.access.rest;

import de.cebitec.gpms.rest.RESTAccessI;
import de.cebitec.mgx.client.MGXDTOMaster;
import de.cebitec.mgx.client.datatransfer.SeqByAttributeDownloader;
import de.cebitec.mgx.client.datatransfer.SeqDownloader;
import de.cebitec.mgx.client.datatransfer.SeqUploader;
import de.cebitec.mgx.client.exception.MGXClientException;
import de.cebitec.mgx.client.exception.MGXDTOException;
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

    public void sendSequences(long seqrun_id, SeqReaderI<? extends DNASequenceI> reader) throws MGXDTOException {
        SeqUploader seqUploader = new SeqUploader(dtomaster, getRESTAccess(), seqrun_id, reader);
        boolean success = seqUploader.upload();
        if (!success) {
            throw new MGXServerException(seqUploader.getErrorMessage());
        }
    }

    public void downloadSequences(long seqrun_id, SeqWriterI<DNASequenceI> writer, boolean closeWriter) throws MGXDTOException {
        SeqDownloader dl = new SeqDownloader(dtomaster, getRESTAccess(), seqrun_id, writer, closeWriter);
        boolean success = dl.download();
        if (!success) {
            throw new MGXServerException(dl.getErrorMessage());
        }
    }

    public SeqUploader createUploader(long seqrun_id, SeqReaderI<? extends DNASequenceI> reader) throws MGXDTOException {
        return new SeqUploader(dtomaster, getRESTAccess(), seqrun_id, reader);
    }

    public SeqDownloader createDownloader(long seqrun_id, SeqWriterI<? extends DNASequenceI> writer, boolean closeWriter) throws MGXDTOException {
        return new SeqDownloader(dtomaster, getRESTAccess(), seqrun_id, writer, closeWriter);
    }

    @Override
    public SequenceDTO fetch(long id) throws MGXDTOException {
        return super.fetch(id, SequenceDTO.class);
    }

    public SequenceDTOList fetchByIds(long[] ids) throws MGXDTOException {
        if (ids == null || ids.length == 0) {
            throw new MGXClientException("Empty/null list of sequence IDs.");
        }
        String[] resolve = r.resolve(SequenceDTOList.class, "fetchByIds");
        Builder b = MGXLongList.newBuilder();
        for (long id : ids) {
            if (id == -1) {
                throw new MGXClientException("Cannot fetch object with invalid identifier.");
            }
            b.addLong(id);
        }
        return super.<SequenceDTOList>put(b.build(), SequenceDTOList.class, resolve);
    }

    public SequenceDTO byName(long runId, String seqName) throws MGXDTOException {
        String resolve = r.resolveClass(SequenceDTO.class);
        String[] path = new String[]{resolve, "byName", String.valueOf(runId)};
        MGXString seqNameDTO = MGXString.newBuilder().setValue(seqName).build();
        return super.put(seqNameDTO, SequenceDTO.class, path);
    }

    public SeqByAttributeDownloader createDownloaderByAttributes(AttributeDTOList attrs, SeqWriterI<? extends DNASequenceI> writer, boolean closeWriter) throws MGXDTOException {
        return new SeqByAttributeDownloader(dtomaster, getRESTAccess(), attrs, writer, closeWriter);
    }

    public void fetchAnnotatedReads(AttributeDTOList attrs, SeqWriterI<? extends DNASequenceI> writer, boolean closeWriter) throws MGXDTOException {
        SeqByAttributeDownloader dl = new SeqByAttributeDownloader(dtomaster, getRESTAccess(), attrs, writer, closeWriter);
        boolean success = dl.download();
        if (!success) {
            throw new MGXServerException(dl.getErrorMessage());
        }
    }

    public Iterator<Long> fetchSequenceIDs(long attrId) throws MGXDTOException {
        return get(MGXLongList.class, "Sequence", "fetchSequenceIDs", String.valueOf(attrId)).getLongList().iterator();
    }

    @Override
    public Iterator<SequenceDTO> fetchall() throws MGXDTOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public long create(SequenceDTO t) throws MGXDTOException {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public void update(SequenceDTO t) throws MGXDTOException {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public UUID delete(long id) throws MGXDTOException {
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
