package de.cebitec.mgx.client.datatransfer;

import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.dto.dto.MGXLong;
import de.cebitec.mgx.dto.dto.MGXString;
import de.cebitec.mgx.dto.dto.ReferenceDTO;
import de.cebitec.mgx.dto.dto.RegionDTO;
import de.cebitec.mgx.dto.dto.RegionDTOList;
import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import javax.net.ssl.SSLHandshakeException;
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
    private long total_elements_sent = 0;
    private final List<Long> generatedRefIDs = new ArrayList<>();

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
            int regionCount = 0;
            List<RegionDTO> regions = new LinkedList<>();

            long reference_id;
            String session_uuid = null;

            Iterator<Feature> iter = rs.features();
            while (iter.hasNext()) {
                RichFeature elem = (RichFeature) iter.next();

                if (elem.getType().equals("CDS") || elem.getType().equals("rRNA") || elem.getType().equals("tRNA")) {
                    if (!sequenceSent) {
                        try {
                            //String genomeSeq = elem.getSequence().seqString();
                            reference_id = createReference(seqname, elem.getSequence().length());
                            generatedRefIDs.add(reference_id);
                            session_uuid = initTransfer(reference_id);
                            sendSequence(elem.getSequence(), session_uuid);
                            sequenceSent = true;
                        } catch (MGXServerException ex) {
                            abortTransfer(ex.getMessage(), total_elements_sent);
                            return false;
                        }
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
                    regionCount++;
                    if (regions.size() >= getChunkSize()) {
                        try {
                            sendRegions(regions, session_uuid);
                        } catch (MGXServerException ex) {
                            abortTransfer(ex.getMessage(), total_elements_sent);
                            return false;
                        }
                        total_elements_sent += regions.size();
                        cb.callback(total_elements_sent);
                        regions.clear();
                    }
                }
            }

            // flush remaining regions
            if (!regions.isEmpty()) {
                try {
                    sendRegions(regions, session_uuid);
                } catch (MGXServerException ex) {
                    abortTransfer(ex.getMessage(), total_elements_sent);
                    return false;
                }
                total_elements_sent += regions.size();
                cb.callback(total_elements_sent);
                regions.clear();
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

    public List<Long> getReferenceIDs() {
        return generatedRefIDs;
    }

    private String initTransfer(long ref_id) throws MGXServerException {
        assert !EventQueue.isDispatchThread();
        try {
            ClientResponse res = wr.path("/Reference/init/" + ref_id).accept("application/x-protobuf").get(ClientResponse.class);
            catchException(res);
            fireTaskChange(TransferBase.NUM_ELEMENTS_SENT, total_elements_sent);
            MGXString session_uuid = res.<MGXString>getEntity(MGXString.class);
            return session_uuid.getValue();
        } catch (ClientHandlerException ex) {
            if (ex.getCause() != null && ex.getCause() instanceof SSLHandshakeException) {
                return initTransfer(ref_id); // retry
            } else {
                throw ex; // rethrow
            }
        }
    }

    private long createReference(String name, int length) throws MGXServerException {
        assert !EventQueue.isDispatchThread();
        ReferenceDTO ref = ReferenceDTO.newBuilder()
                .setName(name)
                .setLength(length)
                .build();
        try {
            ClientResponse res = wr.path("/Reference/create/").accept("application/x-protobuf")
                    .put(ClientResponse.class, ref);
            catchException(res);
            return res.<MGXLong>getEntity(MGXLong.class).getValue();
        } catch (ClientHandlerException ex) {
            if (ex.getCause() != null && ex.getCause() instanceof SSLHandshakeException) {
                return createReference(name, length); // retry
            } else {
                throw ex; // rethrow
            }
        }
    }

    private void sendRegions(List<RegionDTO> regions, String session_uuid) throws MGXServerException {
        assert !EventQueue.isDispatchThread();
        RegionDTOList.Builder data = RegionDTOList.newBuilder();
        for (RegionDTO r : regions) {
            data.addRegion(r);
        }
        try {
            ClientResponse res = wr.path("/Reference/addRegions/" + session_uuid).accept("application/x-protobuf")
                    .put(ClientResponse.class, data.build());
            catchException(res);
        } catch (ClientHandlerException ex) {
            if (ex.getCause() != null && ex.getCause() instanceof SSLHandshakeException) {
                sendRegions(regions, session_uuid); // retry
            } else {
                throw ex; // rethrow
            }
        }
    }

    private void sendSequence(Sequence seq, String session_uuid) throws MGXServerException {
        assert !EventQueue.isDispatchThread();
        String dna = seq.seqString();
        int length = dna.length();
        for (int i = 0; i < length; i += 10000) {
            String chunk = dna.substring(i, Math.min(length, i + 10000));
            try {
                ClientResponse res = wr.path("/Reference/addSequence/" + session_uuid).accept("application/x-protobuf")
                        .put(ClientResponse.class, MGXString.newBuilder().setValue(chunk).build());
                catchException(res);
            } catch (ClientHandlerException ex) {
                if (ex.getCause() != null && ex.getCause() instanceof SSLHandshakeException) {
                    // retry
                    ClientResponse res = wr.path("/Reference/addSequence/" + session_uuid).accept("application/x-protobuf")
                            .put(ClientResponse.class, MGXString.newBuilder().setValue(chunk).build());
                    catchException(res);
                } else {
                    throw ex; // rethrow
                }
            }
        }
    }

    private void finishTransfer(String uuid) throws MGXServerException {
        assert !EventQueue.isDispatchThread();
        try {
            ClientResponse res = wr.path("/Reference/close/" + uuid).get(ClientResponse.class);
            catchException(res);
            fireTaskChange(TransferBase.NUM_ELEMENTS_SENT, total_elements_sent);
        } catch (ClientHandlerException ex) {
            if (ex.getCause() != null && ex.getCause() instanceof SSLHandshakeException) {
                finishTransfer(uuid); // retry
            } else {
                throw ex; // rethrow
            }
        }
    }
}
