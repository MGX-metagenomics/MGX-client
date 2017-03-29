package de.cebitec.mgx.client.mgxtestclient;

import de.cebitec.gpms.core.GPMSException;
import de.cebitec.gpms.core.MembershipI;
import de.cebitec.gpms.rest.GPMSClientI;
import de.cebitec.mgx.client.MGXDTOMaster;
import de.cebitec.mgx.dto.dto.DNAExtractDTO;
import de.cebitec.mgx.dto.dto.SeqRunDTO;
import de.cebitec.mgx.dto.dto.TermDTO;
import de.cebitec.mgx.restgpms.GPMSClient;
import de.cebitec.mgx.sequence.DNASequenceI;
import de.cebitec.mgx.sequence.SeqReaderFactory;
import de.cebitec.mgx.sequence.SeqReaderI;
import java.io.Console;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class App {

    public static void main(String[] args) throws Exception {

        if (args.length < 3) {
            System.err.println("Invalid arguments.");
            System.err.println();
            System.err.println("Usage: MGX_MyProject MyDNAExtract file1.fastq file2.fastq file3.fastq");
            System.err.println();
            System.exit(1);
        }

        String pName = args[0];
        String extractName = args[1];

        Console con = System.console();
        String username = con.readLine("Username: ");
        char[] password = con.readPassword("Password: ");

        MGXDTOMaster master = getMaster(username, password, pName);

        DNAExtractDTO extract = null;
        Iterator<DNAExtractDTO> iter = master.DNAExtract().fetchall();
        while (iter != null && iter.hasNext()) {
            DNAExtractDTO ex = iter.next();
            if (ex.getName().equals(extractName)) {
                extract = ex;
                break;
            }
        }

        if (extract == null) {
            System.err.println("No DNA extract named " + extractName);
            System.exit(1);
        }

        Iterator<SeqRunDTO> runiter = master.SeqRun().fetchall();
        Set<String> runs = new HashSet<>();
        while (runiter.hasNext()) {
            runs.add(runiter.next().getName());
        }

        TermDTO flx = master.Term().fetch(5); // miseq
        TermDTO wgs = master.Term().fetch(13); // pe

        for (int argpos = 2; argpos < args.length; argpos++) {
            String fname = new File(args[argpos]).getName();
            fname = fname.replaceAll(".fastq", "");
            fname = fname.replaceAll(".fastq.gz", "");

            if (runs.contains(fname)) {
                System.err.println("skipping " + fname + ", already present");
            } else {

                // create new seqrun
                SeqRunDTO sr = SeqRunDTO.newBuilder()
                        .setName(fname)
                        .setExtractId(extract.getId())
                        .setAccession("")
                        .setSubmittedToInsdc(false)
                        .setSequencingMethod(wgs)
                        .setSequencingTechnology(flx)
                        .build();
                long seqrun_id = master.SeqRun().create(sr);
                System.err.print("  created seqrun " + sr.getName() + " with id " + seqrun_id + ", starting sequence import..");

                // upload sequence data
                SeqReaderI<? extends DNASequenceI> reader = SeqReaderFactory.<DNASequenceI>getReader(args[argpos]);
                master.Sequence().sendSequences(seqrun_id, reader);
                System.err.println("complete.");
            }
        }
    }

    private static MGXDTOMaster getMaster(String username, char[] password, String pName) throws GPMSException {

        MGXDTOMaster master = null;
        GPMSClientI gpms = new GPMSClient("MyServer", "https://mgx.cebitec.uni-bielefeld.de/MGX-maven-web/webresources/");
        if (!gpms.login(username, new String(password))) {
            System.err.println("login failed.");
            System.exit(1);
        }
        Iterator<MembershipI> mIter = gpms.getMemberships();
        while (mIter.hasNext()) {
            MembershipI m = mIter.next();
            if ("MGX".equals(m.getProject().getProjectClass().getName()) && (pName.equals(m.getProject().getName()))) {
                master = new MGXDTOMaster(gpms.createMaster(m));
                break; // just use the first project we find
            }
        }
        if (master == null) {
            System.err.println("No project " + pName + " found.");
        }
        return master;
    }

//    public static void printObjTree(MGXDTOMaster m) throws MGXServerException, MGXClientException {
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
