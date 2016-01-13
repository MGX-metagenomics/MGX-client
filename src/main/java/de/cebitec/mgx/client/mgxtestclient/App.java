package de.cebitec.mgx.client.mgxtestclient;

import de.cebitec.gpms.core.GPMSException;
import de.cebitec.gpms.core.MembershipI;
import de.cebitec.gpms.rest.GPMSClientI;
import de.cebitec.mgx.client.MGXDTOMaster;
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

        String pName = args[0];

        Console con = System.console();
        String username = "sjaenick"; //con.readLine("Username: ");
        char[] password = con.readPassword("Password: ");

        MGXDTOMaster master = getMaster(username, password, pName);
//        ToolDTO aa = null;
//        Iterator<ToolDTO> it = master.Tool().fetchall();
//        while (it.hasNext()) {
//            aa = it.next();
//            if (aa.getName().equals("BestHit-AA")) {
//                break;
//            }
//        }
//        Iterable<JobParameterDTO> parameters = master.Job().getParameters(86);
//        JobParameterListDTO.Builder params = JobParameterListDTO.newBuilder();
//        for (JobParameterDTO jp : parameters) {
//            params.addParameter(jp);
//        }
//
//        Iterator<SeqRunDTO> sit = master.SeqRun().fetchall();
//        while (sit.hasNext()) {
//            SeqRunDTO sr = sit.next();
//            Iterable<JobDTO> jobs = master.Job().BySeqRun(sr.getId());
//            boolean ok = false;
//            for (JobDTO j : jobs) {
//                if (j.getToolId() == aa.getId()) {
//                    ok = true;
//                }
//            }
//
//            if (!ok) {
//                JobDTO dto = JobDTO.newBuilder()
//                        .setToolId(aa.getId())
//                        .setSeqrunId(sr.getId())
//                        .setState(JobDTO.JobState.CREATED)
//                        .setParameters(params.build())
//                        .build();
//                Long job_id = master.Job().create(dto);
//                System.err.println("job created.");
//                boolean job_ok = master.Job().verify(job_id);
//                System.err.println("job verification: " + job_ok);
//                if (job_ok) {
//                    System.err.println("submitting job " + job_id + "..");
//                    boolean submitted = master.Job().execute(job_id);
//                    System.err.println("job execution: " + submitted);
//                }
//            }
//        }
//        System.exit(0);

//        AttributeDTO adto = master.Attribute().fetch(127400);
//        assert adto != null;
//        AttributeDTOList build = AttributeDTOList.newBuilder().addAttribute(adto).build();
//        FastaWriter writer = new FastaWriter("/tmp/foo.fas");
//        master.Sequence().fetchAnnotatedReads(build, writer);
//        writer.close();
//        System.exit(0);
        //master.Sequence().downloadSequences(2, writer);
//        for (FileDTO f : master.File().fetchall(args[1])) {
//            System.out.println(f.getName());
//        }
//        System.exit(0);
        //        MGXDTOMaster master2 = getMaster(username, password, "MGX_Stadtwerke");
        //
        //        assert master != null;
        //        System.err.println("using " + master.getProject().getName());
        //
        //        for (HabitatDTO h : master.Habitat().fetchall()) {
        //                for (HabitatDTO h2 : master2.Habitat().fetchall()) {
        //                    for (SampleDTO s2 : master2.Sample().ByHabitat(h2.getId())) {
        //                        for (DNAExtractDTO d2 : master2.DNAExtract().BySample(s2.getId())) {
        //                            for (SeqRunDTO sr2 : master2.SeqRun().ByExtract(d2.getId())) {
        //                                System.err.println(master2.getProject().getName() + " " + sr2.getName());
        //                            }
        //                        }
        //                    }
        //                }
        //            for (SampleDTO s : master.Sample().ByHabitat(h.getId())) {
        //                for (DNAExtractDTO d : master.DNAExtract().BySample(s.getId())) {
        //                    for (SeqRunDTO sr : master.SeqRun().ByExtract(d.getId())) {
        //                        System.err.println(master.getProject().getName() + " " + sr.getName());
        //                    }
        //                }
        //            }
        //        }
        //        System.exit(0);
        Iterator<SeqRunDTO> runiter = master.SeqRun().fetchall();
        Set<String> runs = new HashSet<>();
        while (runiter.hasNext()) {
            runs.add(runiter.next().getName());
        }

        long extract_id = 9; // new metagenomes, overlapped
//        Iterator<DNAExtractDTO> iter = master.DNAExtract().fetchall();
//        while (iter.hasNext()) {
//            extract_id = iter.next().getId();
//        }
        TermDTO flx = master.Term().fetch(5); // miseq
        TermDTO wgs = master.Term().fetch(13); // pe

        for (int argpos = 1; argpos < args.length; argpos++) {
            String fname = new File(args[argpos]).getName();
            fname = fname.replaceAll(".fastq.trimmed", "");

            if (runs.contains(fname)) {
                System.err.println("skipping " + fname + ", already present");
                continue;
            } else {

                System.err.print("extract id " + extract_id + ", attach run " + fname + " using " + args[argpos] + " (y/n)? ");
                String answer = "y"; //con.readLine();
                if ("y".equals(answer)) {
                    // create new seqrun
                    SeqRunDTO sr = SeqRunDTO.newBuilder()
                            .setName(fname)
                            .setExtractId(extract_id)
                            .setAccession("")
                            .setSubmittedToInsdc(false)
                            .setSequencingMethod(wgs)
                            .setSequencingTechnology(flx)
                            .build();
                    Long seqrun_id = master.SeqRun().create(sr);
                    System.err.println("  created seqrun " + sr.getAccession() + " with id " + seqrun_id);

                    // upload sequence data
                    SeqReaderI<? extends DNASequenceI> reader = SeqReaderFactory.<DNASequenceI>getReader(args[argpos]);
                    master.Sequence().sendSequences(seqrun_id, reader);
                }
            }
        }
        System.exit(0);
//        // fetch global tool Ids
//        Iterator<ToolDTO> globalTools = master.Tool().listGlobalTools();
//        Iterator<ToolDTO> localiter = master.Tool().fetchall();
//        Collection<ToolDTO> local = new ArrayList<>();
//        while (localiter.hasNext()) {
//            local.add(localiter.next());
//        }
//
        // copy tools to project
//        while (globalTools.hasNext()) {
//            ToolDTO globaltool = globalTools.next();
//            for (ToolDTO localtool : local) {
//                if (globaltool.getName().equals(localtool.getName())) {
//                    isPresent = true;
//                }
//            }

            //if ((!isPresent) && (globaltool.getName().equals("16S Pipeline"))) {
//            if ((globaltool.getAuthor().equals("Sebastian Jaenicke") && (globaltool.getName().equals("16S Pipeline")))) {
//                master.Tool().installGlobalTool(globaltool.getId());
//            }
//        }
//
//        // fetch all tools in project
//        Iterator<ToolDTO> localiter = master.Tool().fetchall();
//        ToolDTO tool = null;
//        while (localiter.hasNext()) {
//            tool = localiter.next();
//        }
//        assert tool != null;
//
//        Iterator<SeqRunDTO> iter = master.SeqRun().fetchall();
//        while (iter.hasNext()) {
//            SeqRunDTO seqrun = iter.next();
//
//            System.err.print("create job: " + seqrun.getName() + "/" + tool.getName() + "/" + tool.getId() + " (y/n)? ");
//            String answer = con.readLine();
//            //if (tool.getName().contains("16S")) {
//            if ("y".equals(answer)) {
//                JobDTO dto = JobDTO.newBuilder()
//                        .setToolId(tool.getId())
//                        .setSeqrunId(seqrun.getId())
//                        .setState(JobDTO.JobState.CREATED)
//                        .setParameters(JobParameterListDTO.newBuilder().build())
//                        .build();
//                Long job_id = master.Job().create(dto);
//                System.err.println("job created.");
//                boolean job_ok = master.Job().verify(job_id);
//                System.err.println("job verification: " + job_ok);
//                if (job_ok) {
//                    System.err.println("submitting job " + job_id + "..");
//                    boolean submitted = master.Job().execute(job_id);
//                    System.err.println("job execution: " + submitted);
//                }
//            }
//
//        }
//        // wait for jobs to finish execution
//        for (Long job_id : jobIDs) {
//            JobDTO job = master.Job().fetch(job_id);
//            while (!((job.getState() == JobState.FINISHED) || (job.getState() == JobState.FAILED))) {
//                System.out.println("state of job " + job.getId() + " is " + job.getState().name());
//                Thread.sleep(5000);
//                job = master.Job().fetch(job_id); //refresh
//            }
//            System.out.println("state of job " + job.getId() + " is " + job.getState().name());
//        }
        //System.out.println("sending cancel()");
        //master.Job().cancel(lastjob);
        // deleting the toplevel obj  will also remove everything else
//        //master.Habitat().delete(hab_id);
    }

    private static MGXDTOMaster getMaster(String username, char[] password, String pName) throws GPMSException {

        MGXDTOMaster master = null;
        // http://localhost:8080/MGX-maven-web/webresources/
        //GPMSClientI gpms = new GPMS("MyServer", "http://scooter.cebitec.uni-bielefeld.de:8080/MGX-maven-web/webresources/");
        GPMSClientI gpms = new GPMSClient("MyServer", "https://mgx.cebitec.uni-bielefeld.de/MGX-maven-web/webresources/");
        if (!gpms.login(username, new String(password))) {
            System.err.println("login failed");
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
//    private void fixCSF(String fname) throws SeqStoreException, IOException {
//        CSFReader r = new CSFReader(fname);
//        CSFWriter w = new CSFWriter("/tmp/tmpcsf");
//        while (r.hasMoreElements()) {
//            w.addSequence(r.nextElement().getSequence());
//        }
//        w.close();
//        r.close();
//    }
}
