package de.cebitec.mgx.client.datatransfer;

import de.cebitec.gpms.rest.RESTAccessI;
import de.cebitec.mgx.client.MGXDTOMaster;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.dto.dto.MGXLong;
import de.cebitec.mgx.dto.dto.MGXString;
import de.cebitec.mgx.dto.dto.ReferenceDTO;
import de.cebitec.mgx.dto.dto.ReferenceRegionDTO;
import de.cebitec.mgx.dto.dto.ReferenceRegionDTOList;
import de.cebitec.mgx.dto.dto.RegionType;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.biojava.bio.Annotation;
import org.biojava.bio.BioException;
import org.biojava.bio.seq.Feature;
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
        super.setChunkSize(42 + randomNess);
    }

    @Override
    public long getProgress() {
        return total_elements_sent;
    }

    @Override
    public boolean upload() {
        //CallbackI cb = getProgressCallback();

        RichSequenceIterator seqIter;
        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader(localFile));
            Namespace ns = RichObjectFactory.getDefaultNamespace();
            br.mark(10);
            Character first = (char) br.read();
            br.reset();
            switch (first.toString()) {
                case "L":
                    seqIter = RichSequence.IOTools.readGenbankDNA(br, ns);
                    break;
                case ">":
                    seqIter = RichSequence.IOTools.readFastaDNA(br, ns);
                    break;
                case "I": // ID
                    seqIter = RichSequence.IOTools.readEMBLDNA(br, ns);
                    break;
                default:
                    setErrorMessage("Unknown sequence format.");
                    br.close();
                    return false;
            }
        } catch (IOException ex) {
            abortTransfer(ex.getMessage());
            return false;
        }

        while (seqIter.hasNext()) {

            RichSequence seq;
            try {
                seq = seqIter.nextRichSequence();
            } catch (NoSuchElementException | BioException ex) {
                abortTransfer(ex.getMessage());
                return false;
            }

            //
            // trim down sequence name
            //
            String seqname = null;
            if (seq.getDescription() != null) {
                seqname = seq.getDescription().replaceAll("\n", " ");
            }
            if (seqname == null || seqname.trim().isEmpty()) {
                seqname = seq.getName().trim();
            }
            if (seqname == null || seqname.trim().isEmpty()) {
                seqname = "unnamed sequence";
            }
            if (seqname.endsWith(", complete sequence.")) {
                int trimPos = seqname.lastIndexOf(", complete sequence.");
                seqname = seqname.substring(0, trimPos);
            }
            if (seqname.endsWith(" complete genome.")) {
                int trimPos = seqname.lastIndexOf(" complete genome.");
                seqname = seqname.substring(0, trimPos);
            }
            if (seqname.endsWith(".") || seqname.endsWith(",")) {
                seqname = seqname.substring(0, seqname.length() - 1);
            }
            fireTaskChange(MESSAGE, "Processing " + seqname);

            //
            // create reference object and transfer sequence data
            //
            String session_uuid;
            try {
                reference_id = createReference(seqname.trim(), seq.length());
                generatedRefIDs.add(reference_id);
                session_uuid = initTransfer(reference_id);
                sendSequence(seq.seqString(), session_uuid);
            } catch (MGXServerException ex) {
                abortTransfer(ex.getMessage());
                return false;
            }

            //
            // transfer subregions
            //
            Iterator<Feature> iter = seq.features();
            List<ReferenceRegionDTO> regions = new ArrayList<>();
            while (iter.hasNext()) {
                RichFeature elem = (RichFeature) iter.next();

                if (elem.getType() != null && (elem.getType().equals("CDS") || elem.getType().equals("rRNA") || elem.getType().equals("tRNA"))) {
                    Annotation annot = elem.getAnnotation();
                    ReferenceRegionDTO.Builder region = ReferenceRegionDTO.newBuilder();
                    region.setName(annot.getProperty("locus_tag").toString());

                    switch (elem.getType()) {
                        case "CDS":
                            region.setType(RegionType.CDS);
                            break;
                        case "rRNA":
                            region.setType(RegionType.RRNA);
                            break;
                        case "tRNA":
                            region.setType(RegionType.TRNA);
                            break;
                        case "tmRNA":
                            region.setType(RegionType.TMRNA);
                            break;
                        case "ncRNA":
                            region.setType(RegionType.NCRNA);
                            break;
                        default:
                            region.setType(RegionType.MISC);
                    }
                    
                    if (annot.containsProperty("product")) {
                        region.setDescription(annot.getProperty("product").toString());
                    } else if (annot.containsProperty("function")) {
                        region.setDescription(annot.getProperty("function").toString());
                    } else {
                        region.setDescription("");
                    }

                    // 0-based start/stop coordinates
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

                    // send region chunk
                    if (regions.size() >= getChunkSize()) {
                        try {
                            sendRegions(regions, session_uuid);
                        } catch (MGXServerException ex) {
                            abortTransfer(ex.getMessage());
                            return false;
                        }
                        total_elements_sent += regions.size();
                        regions.clear();
                        //cb.callback(total_elements_sent);
                        fireTaskChange(MESSAGE, NumberFormat.getInstance(Locale.US).format(total_elements_sent) + " regions sent.");
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
                //cb.callback(total_elements_sent);
                regions.clear();
                fireTaskChange(MESSAGE, NumberFormat.getInstance(Locale.US).format(total_elements_sent) + " regions sent.");
            }

            try {
                finishTransfer(session_uuid);
            } catch (MGXServerException ex) {
                abortTransfer(ex.getMessage());
                return false;
            }
            //cb.callback(total_elements_sent);
            fireTaskChange(MESSAGE, "Imported " + seqname);
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

    private void sendRegions(List<ReferenceRegionDTO> regions, String session_uuid) throws MGXServerException {
        ReferenceRegionDTOList.Builder data = ReferenceRegionDTOList.newBuilder();
        for (ReferenceRegionDTO r : regions) {
            data.addRegion(r);
        }
        super.put(data.build(), "Reference", "addRegions", session_uuid);
    }

    private void sendSequence(String dna, String session_uuid) throws MGXServerException {
        int length = dna.length();
        for (int i = 0; i < length; i += 10000) {
            fireTaskChange(MESSAGE, NumberFormat.getInstance(Locale.US).format(i) + " bp sent..");
            String chunk = dna.substring(i, Math.min(length, i + 10000));
            super.put(MGXString.newBuilder().setValue(chunk).build(), "Reference", "addSequence", session_uuid);
        }
        fireTaskChange(MESSAGE, NumberFormat.getInstance(Locale.US).format(length) + " bp sent..");
    }

    private void finishTransfer(String uuid) throws MGXServerException {
        super.get("Reference", "close", uuid);
        fireTaskChange(TransferBase.NUM_ELEMENTS_TRANSFERRED, total_elements_sent);
        super.dispose();
    }
}
