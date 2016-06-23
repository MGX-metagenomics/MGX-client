package de.cebitec.mgx.client.datatransfer;

import de.cebitec.gpms.rest.RESTAccessI;
import de.cebitec.mgx.client.MGXDTOMaster;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.dto.dto.MGXLong;
import de.cebitec.mgx.dto.dto.MGXString;
import de.cebitec.mgx.dto.dto.ReferenceDTO;
import de.cebitec.mgx.dto.dto.RegionDTO;
import de.cebitec.mgx.dto.dto.RegionDTOList;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
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

    private final File localFile;
    private long total_elements_sent = 0;
    private final List<Long> generatedRefIDs = new ArrayList<>();
    private long reference_id = -1;

    public ReferenceUploader(MGXDTOMaster dtomaster, RESTAccessI rab, final File file) {
        super(dtomaster, rab);
        this.localFile = file;
        int randomNess = (int) Math.round(Math.random() * 20);
        setChunkSize(42 + randomNess);
    }

    @Override
    public long getProgress() {
        return total_elements_sent;
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
            Character first = (char) br.read();
            br.reset();
            if (first.toString().equals("L")) {
                seqs = RichSequence.IOTools.readGenbankDNA(br, ns);
            } else if (first.toString().equals(">")) {
                seqs = RichSequence.IOTools.readFastaDNA(br, ns);
            } else {
                seqs = RichSequence.IOTools.readEMBLDNA(br, ns);
            }
        } catch (IOException ex) {
            abortTransfer(ex.getMessage());
            return false;
        }

        while (seqs.hasNext()) {

            RichSequence rs;
            try {
                rs = seqs.nextRichSequence();
            } catch (NoSuchElementException | BioException ex) {
                abortTransfer(ex.getMessage());
                return false;
            }

            String seqname = rs.getDescription() != null ? rs.getDescription().replaceAll("\n", " ") : "unnamed sequence";
            if (seqname.endsWith(", complete sequence.")) {
                int trimPos = seqname.lastIndexOf(", complete sequence.");
                seqname = seqname.substring(0, trimPos);
            }
            if (seqname.endsWith(", complete genome.")) {
                int trimPos = seqname.lastIndexOf(", complete genome.");
                seqname = seqname.substring(0, trimPos);
            }

            boolean sequenceSent = false;
            List<RegionDTO> regions = new ArrayList<>();

            String session_uuid = null;

            Iterator<Feature> iter = rs.features();
            while (iter.hasNext()) {
                RichFeature elem = (RichFeature) iter.next();

                if (elem.getType() != null && (elem.getType().equals("CDS") || elem.getType().equals("rRNA") || elem.getType().equals("tRNA"))) {
                    if (!sequenceSent) {
                        try {
                            //String genomeSeq = elem.getSequence().seqString();
                            reference_id = createReference(seqname, elem.getSequence().length());
                            generatedRefIDs.add(reference_id);
                            session_uuid = initTransfer(reference_id);
                            sendSequence(elem.getSequence(), session_uuid);
                            sequenceSent = true;
                        } catch (MGXServerException ex) {
                            abortTransfer(ex.getMessage());
                            return false;
                        }
                    }

                    Annotation annot = elem.getAnnotation();
                    RegionDTO.Builder region = RegionDTO.newBuilder();
                    region.setName((String) annot.getProperty("locus_tag"));
                    region.setType(elem.getType());
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
                        try {
                            sendRegions(regions, session_uuid);
                        } catch (MGXServerException ex) {
                            abortTransfer(ex.getMessage());
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
                    abortTransfer(ex.getMessage());
                    return false;
                }
                total_elements_sent += regions.size();
                cb.callback(total_elements_sent);
                regions.clear();
            }

            try {
                finishTransfer(session_uuid);
            } catch (MGXServerException ex) {
                abortTransfer(ex.getMessage());
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
        MGXString session_uuid = super.get(MGXString.class, "Reference", "init", String.valueOf(ref_id));
        fireTaskChange(TransferBase.NUM_ELEMENTS_TRANSFERRED, total_elements_sent);
        return session_uuid.getValue();
    }

    private long createReference(String name, int length) throws MGXServerException {
        ReferenceDTO ref = ReferenceDTO.newBuilder()
                .setName(name)
                .setLength(length)
                .build();
        return super.put(ref, MGXLong.class, "Reference", "create").getValue();
    }

    @Override
    protected void abortTransfer(String reason) {
        if (reference_id != -1) {
            try {
                super.delete("Reference", "delete", String.valueOf(reference_id));
            } catch (MGXServerException ex) {
                Logger.getLogger(ReferenceUploader.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        super.abortTransfer(reason);
    }

    private void sendRegions(List<RegionDTO> regions, String session_uuid) throws MGXServerException {
        RegionDTOList.Builder data = RegionDTOList.newBuilder();
        for (RegionDTO r : regions) {
            data.addRegion(r);
        }
        super.put(data.build(), "Reference", "addRegions", session_uuid);
    }

    private void sendSequence(Sequence seq, String session_uuid) throws MGXServerException {
        String dna = seq.seqString();
        int length = dna.length();
        for (int i = 0; i < length; i += 10000) {
            String chunk = dna.substring(i, Math.min(length, i + 10000));
            super.put(MGXString.newBuilder().setValue(chunk).build(), "Reference", "addSequence", session_uuid);
        }
    }

    private void finishTransfer(String uuid) throws MGXServerException {
        super.get("Reference", "close", uuid);
        fireTaskChange(TransferBase.NUM_ELEMENTS_TRANSFERRED, total_elements_sent);
    }
}
