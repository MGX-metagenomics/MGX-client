package de.cebitec.mgx.client.mgxtestclient;

import de.cebitec.gpms.core.MembershipI;
import de.cebitec.gpms.rest.GPMSClientI;
import de.cebitec.mgx.client.MGXDTOMaster;
import de.cebitec.mgx.client.exception.MGXClientException;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.dto.dto;
import de.cebitec.mgx.dto.dto.DNAExtractDTO;
import de.cebitec.mgx.dto.dto.HabitatDTO;
import de.cebitec.mgx.dto.dto.JobDTO;
import de.cebitec.mgx.dto.dto.JobParameterListDTO;
import de.cebitec.mgx.dto.dto.SampleDTO;
import de.cebitec.mgx.dto.dto.SeqRunDTO;
import de.cebitec.mgx.dto.dto.ToolDTO;
import de.cebitec.mgx.restgpms.GPMS;
import java.io.Console;
import java.util.*;

public class App {

    public static void main(String[] args) throws Exception {

        String pName = args[0];
        //String seqFile = args[1];

        Console con = System.console();

        System.err.println("using project " + pName);
        String username = con.readLine("Username: ");
        char[] password = con.readPassword("Password: ");

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

        assert master != null;
        System.err.println("using " + master.getProject().getName());

//        // create habitat
//        HabitatDTO h2 = HabitatDTO.newBuilder()
//                .setName("Biogas Upmeier")
//                .setGpsLatitude(12.1)
//                .setGpsLongitude(23.1)
//                .setDescription("Metagenome Biogasfermenter Upmeier Bielefeld")
//                .setAltitude(100)
//                .setBiome("fermenter")
//                .build();
//        Long hab_id = master.Habitat().create(h2);
//
//        // create sample
//        SampleDTO s = SampleDTO.newBuilder()
//                .setHabitatId(hab_id)
//                .setMaterial("substrate")
//                .setTemperature(42)
//                .setVolume(100)
//                .setVolumeUnit("ml")
//                .setCollectiondate(Calendar.getInstance().getTime().getTime() / 1000L)
//                .build();
//        Long sample_id = master.Sample().create(s);
//        System.err.println("  created sample " + s.getMaterial() + " with id " + sample_id);
//
//        // create dnaextract
//        DNAExtractDTO d = DNAExtractDTO.newBuilder()
//                .setSampleId(sample_id)
//                .setMethod("My extraction method")
//                .build();
//        Long extract_id = master.DNAExtract().create(d);
//        System.err.println("  created extract " + d.getMethod() + " with id " + extract_id);
//
//        // create new seqrun
//        SeqRunDTO sr = SeqRunDTO.newBuilder()
//                .setExtractId(extract_id)
//                .setAccession("myAccession")
//                .setSubmittedToInsdc(true)
//                .setSequencingMethod("WGS")
//                .setSequencingTechnology("454")
//                .build();
//        Long seqrun_id = master.SeqRun().create(sr);
//        System.err.println("  created seqrun " + sr.getAccession() + " with id " + seqrun_id);
//
//        // upload sequence data
//        SeqReaderI reader = SeqReaderFactory.getReader(args[1]);
//        master.Sequence().sendSequences(seqrun_id, reader);

        //System.exit(0);

        // fetch global tool Ids
        Collection<ToolDTO> globalTools = master.Tool().listGlobalTools();
        Collection<ToolDTO> local = master.Tool().fetchall();

        List<Long> toolIds = new ArrayList<>();

        // copy tools to project
        for (ToolDTO globaltool : globalTools) {
            boolean isPresent = false;
            for (ToolDTO localtool : local) {
                if (globaltool.getName().equals(localtool.getName())) {
                    isPresent = true;
                }
            }

            if (!isPresent) {
                //if ((!isPresent) && (globaltool.getName().equals("MetaPhlAn"))) {
                if (globaltool.getAuthor().equals("Sebastian Jaenicke")) {
                    Long installedToolId = master.Tool().installGlobalTool(globaltool.getId());
                    toolIds.add(installedToolId);
                }
            }
        }

        for (SeqRunDTO seqrun : master.SeqRun().fetchall()) {
            // create and verify the jobs
            ArrayList<Long> jobIDs = new ArrayList<>();
            for (Long toolId : toolIds) {
                System.err.println("creating job..");
                JobParameterListDTO paramDTO = dto.JobParameterListDTO.newBuilder().build();
                JobDTO dto = JobDTO.newBuilder()
                        .setToolId(toolId)
                        .setSeqrunId(seqrun.getId())
                        .setState(JobDTO.JobState.CREATED)
                        .setParameters(paramDTO)
                        .build();
                Long job_id = master.Job().create(dto);

                boolean job_ok = master.Job().verify(job_id);
                System.err.println("job verification: " + job_ok);
                jobIDs.add(job_id);
            }

            for (Long job_id : jobIDs) {
                System.err.println("submitting job " + job_id + "..");
                boolean submitted = master.Job().execute(job_id);
                System.err.println("job execution: " + submitted);
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
