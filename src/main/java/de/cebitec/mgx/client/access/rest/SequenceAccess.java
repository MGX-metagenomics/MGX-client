package de.cebitec.mgx.client.access.rest;

import static de.cebitec.mgx.client.access.rest.AccessBase.r;
import de.cebitec.mgx.client.datatransfer.SeqByAttributeDownloader;
import de.cebitec.mgx.client.datatransfer.SeqDownloader;
import de.cebitec.mgx.client.datatransfer.SeqUploader;
import de.cebitec.mgx.client.exception.MGXClientException;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.dto.dto.AttributeDTOList;
import de.cebitec.mgx.dto.dto.MGXLong;
import de.cebitec.mgx.dto.dto.MGXLongList;
import de.cebitec.mgx.dto.dto.MGXLongList.Builder;
import de.cebitec.mgx.dto.dto.SequenceDTO;
import de.cebitec.mgx.dto.dto.SequenceDTOList;
import de.cebitec.mgx.sequence.DNASequenceI;
import de.cebitec.mgx.sequence.SeqReaderI;
import de.cebitec.mgx.sequence.SeqWriterI;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

/**
 *
 * @author sjaenick
 */
public class SequenceAccess extends AccessBase<SequenceDTO, SequenceDTOList> {

    public void sendSequences(long seqrun_id, SeqReaderI reader) throws MGXServerException {
        SeqUploader seqUploader = new SeqUploader(getWebResource(), seqrun_id, reader);
        boolean success = seqUploader.upload();
        if (!success) {
            throw new MGXServerException(seqUploader.getErrorMessage());
        }
    }

    public void downloadSequences(long seqrun_id, SeqWriterI<DNASequenceI> writer) throws MGXServerException {
        SeqDownloader dl = new SeqDownloader(getWebResource(), seqrun_id, writer);
        boolean success = dl.download();
        if (!success) {
            throw new MGXServerException(dl.getErrorMessage());
        }
    }

    public SeqUploader createUploader(long seqrun_id, SeqReaderI reader) {
        return new SeqUploader(getWebResource(), seqrun_id, reader);
    }

    public SeqDownloader createDownloader(long seqrun_id, SeqWriterI<DNASequenceI> writer) {
        return new SeqDownloader(getWebResource(), seqrun_id, writer);
    }

    @Override
    public SequenceDTO fetch(long id) throws MGXServerException, MGXClientException {
        return super.fetch(id, SequenceDTO.class);
    }

    public SequenceDTOList fetchSeqData(Set<Long> ids) throws MGXServerException, MGXClientException {
        String resolve = r.resolve(SequenceDTOList.class, "fetchall");
        Builder b = MGXLongList.newBuilder();
        for (Long id : ids) {
            b.addLong(MGXLong.newBuilder().setValue(id).build());
        }
        return super.<SequenceDTOList>put(resolve, b.build(), SequenceDTOList.class);
    }

    public SeqByAttributeDownloader createDownloaderByAttributes(AttributeDTOList attrs, SeqWriterI<DNASequenceI> writer) {
        return new SeqByAttributeDownloader(getWebResource(), attrs, writer);
    }

    public void fetchAnnotatedReads(AttributeDTOList attrs, SeqWriterI<DNASequenceI> writer) throws MGXServerException {
        SeqByAttributeDownloader dl = new SeqByAttributeDownloader(getWebResource(), attrs, writer);
        boolean success = dl.download();
        if (!success) {
            throw new MGXServerException(dl.getErrorMessage());
        }
    }

    @Override
    public Collection<SequenceDTO> fetchall() throws MGXServerException, MGXClientException {
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
    public boolean delete(long id) throws MGXServerException, MGXClientException {
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
