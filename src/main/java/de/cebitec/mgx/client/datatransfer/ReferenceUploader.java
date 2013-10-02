package de.cebitec.mgx.client.datatransfer;

import com.google.protobuf.ByteString;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.dto.dto.BytesDTO;
import de.cebitec.mgx.dto.dto.MGXString;
import de.cebitec.mgx.dto.dto.ReferenceDTO;
import de.cebitec.mgx.dto.dto.RegionDTO;
import java.awt.EventQueue;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.biojava.bio.Annotation;
import org.biojava.bio.BioException;
import org.biojava.bio.seq.Feature;
import org.biojava.bio.seq.Sequence;
import org.biojavax.Namespace;
import org.biojavax.RichObjectFactory;
import org.biojavax.bio.seq.RichFeature;
import org.biojavax.bio.seq.RichSequence;
import org.biojavax.bio.seq.RichSequenceIterator;

/**
 *
 * @author sj
 */
public class ReferenceUploader extends UploadBase {

    private final WebResource wr;
    private final File localFile;
    private InputStream in = null;
    private long total_elements_sent = 0;

    public ReferenceUploader(final WebResource wr, final File file) {
        super();
        this.localFile = file;
        this.wr = wr;
        int randomNess = (int) Math.round(Math.random() * 20);
        setChunkSize(42 + randomNess);
    }

    @Override
    public boolean upload() {
        CallbackI cb = getProgressCallback();

        RichSequenceIterator seqs = null;
        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader(localFile));
            Namespace ns = RichObjectFactory.getDefaultNamespace();
            br.mark(10);
            Character first = Character.valueOf((char) br.read());
            br.reset();
            if (first.toString().equals("L")) {
                seqs = RichSequence.IOTools.readGenbankDNA(br, ns);
            } else {
                seqs = RichSequence.IOTools.readEMBLDNA(br, ns);
            }
        } catch (IOException ex) {
            abortTransfer(ex.getMessage(), total_elements_sent);
            return false;
        }


        while (seqs.hasNext()) {
            // obtain session for upload
            String session_uuid;
            try {
                session_uuid = initTransfer();
            } catch (MGXServerException ex) {
                abortTransfer(ex.getMessage(), total_elements_sent);
                return false;
            }

            RichSequence rs;
            try {
                rs = seqs.nextRichSequence();
            } catch (NoSuchElementException | BioException ex) {
                abortTransfer(ex.getMessage(), total_elements_sent);
                return false;
            }

            String seqname = rs.getDescription().replaceAll("\n", " ");
            if (seqname.endsWith(", complete sequence.")) {
                int trimPos = seqname.lastIndexOf(", complete sequence.");
                seqname = seqname.substring(0, trimPos);
            }
            if (seqname.endsWith(", complete genome.")) {
                int trimPos = seqname.lastIndexOf(", complete genome.");
                seqname = seqname.substring(0, trimPos);
            }

            boolean sequenceSent = false;
            List<RegionDTO> regions = new LinkedList<>();

            Iterator<Feature> iter = rs.features();
            while (iter.hasNext()) {
                RichFeature elem = (RichFeature) iter.next();

                if (elem.getType().equals("CDS") || elem.getType().equals("rRNA") || elem.getType().equals("tRNA")) {
                    if (!sequenceSent) {
                        //String genomeSeq = elem.getSequence().seqString();
                        sendNameAndLength(seqname, elem.getSequence().length(), session_uuid);
                        sendSequence(elem.getSequence(), session_uuid);
                        sequenceSent = true;
                    }

                    Annotation annot = elem.getAnnotation();
                    RegionDTO.Builder region = RegionDTO.newBuilder();
                    region.setName((String) annot.getProperty("locus_tag"));
                    if (annot.containsProperty("product")) {
                        region.setDescription((String) annot.getProperty("product"));
                    } else if (annot.containsProperty("function")) {
                        region.setDescription((String) annot.getProperty("function"));
                    } else {
                        region.setDescription("");
                    }

                    int abs_start, abs_stop;
                    if (elem.getStrand().getValue() == 1) {
                        abs_start = elem.getLocation().getMin() - 1;
                        abs_stop = elem.getLocation().getMax() - 1;
                    } else {
                        abs_stop = elem.getLocation().getMin() - 1;
                        abs_start = elem.getLocation().getMax() - 1;
                    }
                    region.setStart(abs_start);
                    region.setStop(abs_stop);

                    regions.add(region.build());
                    if (regions.size() >= getChunkSize()) {
                        sendRegions(regions, session_uuid);
                        regions.clear();
                    }
                }
            }

            try {
                finishTransfer(session_uuid);
            } catch (MGXServerException ex) {
                abortTransfer(ex.getMessage(), total_elements_sent);
                return false;
            }
            cb.callback(total_elements_sent);
        }
        try {
            br.close();
        } catch (IOException ex) {
        }
        
        return true;
    }

    private String initTransfer() throws MGXServerException {
        assert !EventQueue.isDispatchThread();
        ClientResponse res = wr.path("/Reference/init/").accept("application/x-protobuf").get(ClientResponse.class);
        catchException(res);
        fireTaskChange(TransferBase.NUM_ELEMENTS_SENT, total_elements_sent);
        MGXString session_uuid = res.<MGXString>getEntity(MGXString.class);
        return session_uuid.getValue();
    }

    private void sendNameAndLength(String name, int length, String session_uuid) {
    }

    private void sendRegions(List<RegionDTO> regions, String session_uuid) {
    }

    private void sendSequence(Sequence seq, String session_uuid) {
    }

    private void sendChunk(final byte[] data, String session_uuid) throws MGXServerException {
        assert !EventQueue.isDispatchThread();
        BytesDTO rawData = BytesDTO.newBuilder().setData(ByteString.copyFrom(data)).build();
        ClientResponse res = wr.path("/Reference/add/" + session_uuid).type("application/x-protobuf").post(ClientResponse.class, rawData);
        catchException(res);
        fireTaskChange(TransferBase.NUM_ELEMENTS_SENT, total_elements_sent);
    }

    private void finishTransfer(String uuid) throws MGXServerException {
        assert !EventQueue.isDispatchThread();
        ClientResponse res = wr.path("/Reference/close/" + uuid).get(ClientResponse.class);
        catchException(res);
        fireTaskChange(TransferBase.NUM_ELEMENTS_SENT, total_elements_sent);
    }
}
