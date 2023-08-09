package de.cebitec.mgx.client.mgxtestclient;

import de.cebitec.gpms.core.GPMSException;
import de.cebitec.gpms.core.MembershipI;
import de.cebitec.gpms.rest.GPMSClientFactory;
import de.cebitec.gpms.rest.GPMSClientI;
import de.cebitec.mgx.client.MGXDTOMaster;
import de.cebitec.mgx.dto.dto.DNAExtractDTO;
import de.cebitec.mgx.dto.dto.SeqRunDTO;
import de.cebitec.mgx.dto.dto.TermDTO;
import de.cebitec.mgx.seqstorage.AlternatingQReader;
import de.cebitec.mgx.sequence.DNAQualitySequenceI;
import de.cebitec.mgx.sequence.DNASequenceI;
import de.cebitec.mgx.sequence.SeqReaderFactory;
import de.cebitec.mgx.sequence.SeqReaderI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class App {

//    public static void main(String[] args) throws Exception {
//        Console con = System.console();
//        String username = con.readLine("Username: ");
//        char[] password = con.readPassword("Password: ");
//        MGXDTOMaster master = getMaster(username, password, args[0]);
//
//        // get the attribute type
//        long attrTypeId = 0;
//        Iterator<AttributeTypeDTO> types = master.AttributeType().fetchall();
//        while (types != null && types.hasNext()) {
//            AttributeTypeDTO aType = types.next();
//            if (aType.getName().equals("NCBI_SPECIES")) {
//                attrTypeId = aType.getId();
//            }
//        }
//
//        Iterator<SeqRunDTO> runIter = master.SeqRun().fetchall();
//        while (runIter != null && runIter.hasNext()) {
//            SeqRunDTO run = runIter.next();
//            Iterator<JobDTO> jobs = master.Job().bySeqRun(run.getId()).iterator();
//
//            // only one job per run expected
//            JobDTO job = null;
//            while (jobs != null && jobs.hasNext()) {
//                job = jobs.next();
//            }
//
//            AttributeDistribution dist = master.Attribute().getDistribution(attrTypeId, job.getId());
//            List<AttributeCount> attributeCountsList = dist.getAttributeCountsList();
//            for (AttributeCount ac : attributeCountsList) {
//                if (ac.getAttribute().getValue().equals("uncultured bacterium")) {
//                    AttributeDTOList reqList = AttributeDTOList.newBuilder()
//                            .addAttribute(ac.getAttribute())
//                            .build();
//
//                    FastaWriter fw = new FastaWriter("uncult_" + run.getName() + ".fas");
//                    SeqByAttributeDownloader dl = master.Sequence().createDownloaderByAttributes(reqList, fw, true);
//                    if (!dl.download()) {
//                        System.err.println(run.getId() + "/" + run.getName() + " failed, " + dl.getErrorMessage());
//                    }
//
//                }
//            }
//        }
//    }
//
    public static void main(String[] args) throws Exception {

        if (args.length == 0) {
            System.err.println("Invalid arguments.");
            System.err.println();
            System.err.println("Usage: file1.fastq file2.fastq");
            System.err.println();
            System.exit(1);
        }

        String projectName = "MGX2_TermiteGut";

        String username = "sjaenick";
        char[] password = System.console().readPassword("Password: ");
        MGXDTOMaster master = getMaster(username, password, projectName);

        DNAExtractDTO extract = null;
        Iterator<DNAExtractDTO> iter = master.DNAExtract().fetchall().getExtractList().iterator();
        while (iter != null && iter.hasNext()) {
            DNAExtractDTO ex = iter.next();
            extract = ex;
        }
        
        if (extract == null) {
            System.err.println("No such extract");
            System.exit(1);
        }

        Set<String> runNames = new HashSet<>();
        List<SeqRunDTO> seqrunList = master.SeqRun().fetchall().getSeqrunList();
        for (SeqRunDTO sr : seqrunList) {
            runNames.add(sr.getName());
        }

        TermDTO flx = master.Term().fetch(5); // miseq
        TermDTO wgs = master.Term().fetch(12); // wgs

        for (String fname : args) {
            String runName = fname.replace("_1.fastq.gz", "");
            
            String file1 = fname;
            String file2 = fname.replaceAll("_1.fastq.gz", "_2.fastq.gz");

            if (runNames.contains(runName)) {
                System.err.println("skipping " + runName + ", already present");
            } else {
                System.err.println("will upload " + runName + " with files "+file1 +" and "+file2);
                // create new seqrun
                SeqRunDTO sr = SeqRunDTO.newBuilder()
                        .setName(runName)
                        .setExtractId(extract.getId())
                        .setAccession("")
                        .setIsPaired(true)
                        .setSubmittedToInsdc(false)
                        .setSequencingMethod(wgs)
                        .setSequencingTechnology(flx)
                        .build();
                long seqrun_id = master.SeqRun().create(sr);

                System.err.print("  created seqrun " + sr.getName() + " with id " + seqrun_id + ", starting sequence import..");

                // upload sequence data
                SeqReaderI<DNAQualitySequenceI> reader1 = (SeqReaderI<DNAQualitySequenceI>) SeqReaderFactory.<DNAQualitySequenceI>getReader(file1);
                SeqReaderI<DNAQualitySequenceI> reader2 = (SeqReaderI<DNAQualitySequenceI>) SeqReaderFactory.<DNAQualitySequenceI>getReader(file2);
                
                SeqReaderI<? extends DNASequenceI> reader = new AlternatingQReader(reader1, reader2);

                master.Sequence().sendSequences(seqrun_id, true, reader);
                System.err.println("complete.");
            }
        }

    }

    private static MGXDTOMaster getMaster(String username, char[] password, String pName) throws GPMSException {

        GPMSClientI gpms = GPMSClientFactory.createClient("MyServer", "https://mgx-test.computational.bio.uni-giessen.de/MGX-rest/webresources/", true);
        if (!gpms.login(username, password)) {
            System.err.println("Login failed.");
            System.exit(1);
        }
//        Iterator<MembershipI> mIter = gpms.getMemberships();
//        while (mIter.hasNext()) {
//            MembershipI mbr = mIter.next();
//            System.out.println(mbr.getProject().getName());
//        }

        MGXDTOMaster master = null;
        Iterator<MembershipI> mIter = gpms.getMemberships();
        while (mIter.hasNext()) {
            MembershipI m = mIter.next();
            if ("MGX-2".equals(m.getProject().getProjectClass().getName()) && (pName.equals(m.getProject().getName()))) {
                master = new MGXDTOMaster(gpms.createMaster(m));
                break; // just use the first project we find
            }
        }
        if (master == null) {
            System.err.println("No project " + pName + " found.");
        }
        return master;
    }

//    public static void printObjTree(MGXDTOMaster m) throws Exception  {
//        System.out.println("\n.------------------------------------------------------------");
//        System.err.println("| DB Contents (" + m.getProject() + ")\n|");
//        for (HabitatDTO h : m.Habitat().fetchall()) {
//            System.out.println("| H: " + h.getName());
//            for (SampleDTO s : m.Sample().ByHabitat(h.getId())) {
//                System.out.println("| `--- S: " + s.getCollectiondate());
//                for (DNAExtractDTO d : m.DNAExtract().BySample(s.getId())) {
//                    System.out.println("|      `--- E: " + d.getId());
//                    for (SeqRunDTO sr : m.SeqRun().ByExtract(d.getId())) {
//                        System.out.println("|           `--- R: " + sr.getId() + " " + sr.getAccession());
//                    }
//                }
//            }
//        }
//        System.out.println("`------------------------------------------------------------");
//    }
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

    protected static List<String> split(String message, String separator) {
        return new ArrayList<>(Arrays.asList(message.split(separator)));
    }
}
