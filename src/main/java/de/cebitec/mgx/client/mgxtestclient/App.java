package de.cebitec.mgx.client.mgxtestclient;

import de.cebitec.gpms.core.MembershipI;
import de.cebitec.gpms.rest.GPMSClientI;
import de.cebitec.mgx.client.MGXDTOMaster;
import de.cebitec.mgx.client.exception.MGXClientException;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.dto.dto.DNAExtractDTO;
import de.cebitec.mgx.dto.dto.HabitatDTO;
import de.cebitec.mgx.dto.dto.JobDTO;
import de.cebitec.mgx.dto.dto.JobParameterListDTO;
import de.cebitec.mgx.dto.dto.SampleDTO;
import de.cebitec.mgx.dto.dto.SeqRunDTO;
import de.cebitec.mgx.dto.dto.TermDTO;
import de.cebitec.mgx.dto.dto.ToolDTO;
import de.cebitec.mgx.restgpms.GPMS;
import de.cebitec.mgx.sequence.SeqReaderFactory;
import de.cebitec.mgx.sequence.SeqReaderI;
import java.io.Console;
import java.util.*;

public class App {

    public static void main(String[] args) throws Exception {

        String pName = args[0];
        //String seqFile = args[1];

        Console con = System.console();

        String username = "sjaenick"; //con.readLine("Username: ");
        char[] password = con.readPassword("Password: ");

        MGXDTOMaster master = getMaster(username, password, pName);
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

//        long extract_id = 0;
//        for (DNAExtractDTO extract : master.DNAExtract().fetchall()) {
//            extract_id = extract.getId();
//        }
//        TermDTO flx = master.Term().fetch(3); // FLX tit
//        TermDTO wgs = master.Term().fetch(12); // WGS
//
//        for (int argpos = 1; argpos < args.length; argpos++) {
//            String fname = args[argpos];
//            fname = fname.replaceAll("_ITv3.2.fas", "");
//
//            System.err.print("extract id " + extract_id + ", attach run " + fname + " using " + args[argpos] + " (y/n)? ");
//            String answer = con.readLine();
//            if ("y".equals(answer)) {
//                // create new seqrun
//                SeqRunDTO sr = SeqRunDTO.newBuilder()
//                        .setName(fname)
//                        .setExtractId(extract_id)
//                        .setAccession("myAccession")
//                        .setSubmittedToInsdc(false)
//                        .setSequencingMethod(wgs)
//                        .setSequencingTechnology(flx)
//                        .build();
//                Long seqrun_id = master.SeqRun().create(sr);
//                System.err.println("  created seqrun " + sr.getAccession() + " with id " + seqrun_id);
//
//                // upload sequence data
//                SeqReaderI reader = SeqReaderFactory.getReader(args[argpos]);
//                master.Sequence().sendSequences(seqrun_id, reader);
//            }
//        }
//        System.exit(0);

        // fetch global tool Ids
        Collection<ToolDTO> globalTools = master.Tool().listGlobalTools();
        Collection<ToolDTO> local = master.Tool().fetchall();

        // copy tools to project
        for (ToolDTO globaltool : globalTools) {
            boolean isPresent = false;
            for (ToolDTO localtool : local) {
                if (globaltool.getName().equals(localtool.getName())) {
                    isPresent = true;
                }
            }

            if (!isPresent) {
                //if ((!isPresent) && (globaltool.getName().equals("16S Pipeline"))) {
                if ((globaltool.getAuthor().equals("Sebastian Jaenicke") && (!globaltool.getName().equals("PKS Screen")))) {
                    master.Tool().installGlobalTool(globaltool.getId());
                }
            }
        }

        // fetch all tools in project
        local = master.Tool().fetchall();

        for (SeqRunDTO seqrun : master.SeqRun().fetchall()) {
            Iterable<JobDTO> jobs = master.Job().BySeqRun(seqrun.getId());

            // create and verify the jobs
            for (ToolDTO tool : local) {

                boolean isUsed = false;
                for (JobDTO j : jobs) {
                    if (j.getToolId() == tool.getId()) {
                        isUsed = true;
                    }
                }

                if (!isUsed) {
                    System.err.print("create job: " + seqrun.getName() + "/" + tool.getName() + " (y/n)? ");
                    String answer = con.readLine();
                    //if (tool.getName().contains("16S")) {
                    if ("y".equals(answer)) {
                        JobDTO dto = JobDTO.newBuilder()
                                .setToolId(tool.getId())
                                .setSeqrunId(seqrun.getId())
                                .setState(JobDTO.JobState.CREATED)
                                .setParameters(JobParameterListDTO.newBuilder().build())
                                .build();
                        Long job_id = master.Job().create(dto);

                        boolean job_ok = master.Job().verify(job_id);
                        System.err.println("job verification: " + job_ok);
                        if (job_ok) {
                            System.err.println("submitting job " + job_id + "..");
                            boolean submitted = master.Job().execute(job_id);
                            System.err.println("job execution: " + submitted);
                        }
                    }
                }
            }

        }

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
        //master.Habitat().delete(hab_id);
    }

    private static MGXDTOMaster getMaster(String username, char[] password, String pName) {

        MGXDTOMaster master = null;
        // http://localhost:8080/MGX-maven-web/webresources/
        GPMSClientI gpms = new GPMS("MyServer", "http://scooter.cebitec.uni-bielefeld.de:8080/MGX-maven-web/webresources/");
        if (!gpms.login(username, new String(password))) {
            System.err.println("login failed");
            System.exit(1);
        }
        for (MembershipI m : gpms.getMemberships()) {
            if ("MGX".equals(m.getProject().getProjectClass().getName()) && (pName.equals(m.getProject().getName()))) {
                master = new MGXDTOMaster(gpms, m);
                break; // just use the first project we find
            }
        }
        return master;
    }

    public static void printObjTree(MGXDTOMaster m) throws MGXServerException, MGXClientException {
        System.out.println("\n.------------------------------------------------------------");
        System.err.println("| DB Contents (" + m.getProject() + ")\n|");
        for (HabitatDTO h : m.Habitat().fetchall()) {
            System.out.println("| H: " + h.getName());
            for (SampleDTO s : m.Sample().ByHabitat(h.getId())) {
                System.out.println("| `--- S: " + s.getCollectiondate());
                for (DNAExtractDTO d : m.DNAExtract().BySample(s.getId())) {
                    System.out.println("|      `--- E: " + d.getId());
                    for (SeqRunDTO sr : m.SeqRun().ByExtract(d.getId())) {
                        System.out.println("|           `--- R: " + sr.getId() + " " + sr.getAccession());
                    }
                }
            }
        }
        System.out.println("`------------------------------------------------------------");
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

    protected static List<String> split(String message, String separator) {
        return new ArrayList<String>(Arrays.asList(message.split(separator)));
    }
}
