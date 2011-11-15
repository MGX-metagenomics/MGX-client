package de.cebitec.mgx.client.mgxtestclient;

import de.cebitec.gpms.core.MembershipI;
import de.cebitec.gpms.rest.GPMSClientI;
import de.cebitec.mgx.client.exception.MGXClientException;
import de.cebitec.mgx.client.MGXMaster;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.dto.*;
import de.cebitec.mgx.dto.JobDTO.JobState;
import de.cebitec.mgx.restgpms.GPMS;
import de.cebitec.mgx.sequence.SeqReaderFactory;
import de.cebitec.mgx.sequence.SeqReaderI;
import java.io.Console;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class App {

    public static void main(String[] args) throws Exception {
        Console con = System.console();
        String username = con.readLine("Username: ");
        char[] password = con.readPassword("Password: ");

        MGXMaster master = null;

        GPMSClientI gpms = new GPMS("http://scooter.cebitec.uni-bielefeld.de:8080/MGX-maven-web/webresources/");
        if (!gpms.login(username, new String(password))) {
            System.err.println("login failed");
            System.exit(1);
        }
        for (MembershipI m : gpms.getMemberships()) {
            if ("MGX".equals(m.getProject().getProjectClass().getName())) {
                master = new MGXMaster(gpms, m);
                break; // just use the first project we find
            }
        }

        System.err.println("using "+master.getProject().getName());

        // create habitat
        HabitatDTO h1 = HabitatDTO.newBuilder()
                .setName("Biogas Stadtwerke")
                .setGPSlocation("42'07N 23'11E")
                .setDescription("Metagenome Biogasfermenter Stadtwerke Bielefeld")
                .setAltitude(100)
                .setBiome("fermenter")
                .build();
        Long hab_id = master.Habitat().create(h1);

        System.err.println("  created habitat " + h1.getName() + " with id " + hab_id);
        //HabitatDTO fetch = master.Habitat().fetch(hab_id);
        //master.Habitat().update(fetch);
        //master.Habitat().delete(hab_id);
        //System.exit(0);


        // create sample
        SampleDTO s = SampleDTO.newBuilder()
                .setHabitatId(hab_id)
                .setMaterial("substrate")
                .setTemperature(42)
                .setVolume(100)
                .setVolumeUnit("ml")
                .setCollectiondate(Calendar.getInstance().getTime().getTime() / 1000L)
                .build();
        Long sample_id = master.Sample().create(s);
        System.err.println("  created sample " + s.getMaterial() + " with id " + sample_id);

        // create dnaextract
        DNAExtractDTO d = DNAExtractDTO.newBuilder()
                .setSampleId(sample_id)
                .setMethod("My extraction method")
                .build();
        Long extract_id = master.DNAExtract().create(d);
        System.err.println("  created extract " + d.getMethod() + " with id " + extract_id);

        // create new seqrun
        SeqRunDTO sr = SeqRunDTO.newBuilder()
                .setExtractId(extract_id)
                .setAccession("myAccession")
                .setSubmittedToInsdc(true)
                .setSequencingMethod("WGS")
                .setSequencingTechnology("454")
                .build();
        Long seqrun_id = master.SeqRun().create(sr);
        System.err.println("  created seqrun " + sr.getAccession() + " with id " + seqrun_id);

        // upload sequence data
        SeqReaderI reader = SeqReaderFactory.getReader("/tmp/test.fas");
        master.Sequence().sendSequences(seqrun_id, reader);

        //System.exit(0);

        // fetch global tool Ids
        ArrayList<Long> globalToolIDs = new ArrayList<Long>();
        System.err.println("global tools:");
        Collection<ToolDTO> globalTools = master.Tool().listGlobalTools();
        for (ToolDTO t : globalTools) {
            System.err.println("Tool: " + t.getName());
            //if (t.getName().contains("LCA"))
                globalToolIDs.add(t.getId());
        }

        // copy tools to project
        ArrayList<Long> localToolIDs = new ArrayList<Long>();
        for (Long global_tool_id : globalToolIDs) {
            System.err.println("copy global tool " + global_tool_id + " to local db");
            Long installedTool = master.Tool().installTool(global_tool_id);
            localToolIDs.add(installedTool);
        }

        // create and verify the jobs
        ArrayList<Long> jobIDs = new ArrayList<Long>();
        for (Long toolId : localToolIDs) {
            System.err.println("creating job..");
            JobDTO dto = JobDTO.newBuilder().setToolId(toolId).setSeqrunId(seqrun_id).build();
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

        // wait for jobs to finish execution
        for (Long job_id : jobIDs) {
            JobDTO job = master.Job().fetch(job_id);
            while (!((job.getState() == JobState.FINISHED) || (job.getState() == JobState.FAILED))) {
                System.out.println("state of job " + job.getId() + " is " + job.getState().name());
                Thread.sleep(5000);
                job = master.Job().fetch(job_id); //refresh
            }
            System.out.println("state of job " + job.getId() + " is " + job.getState().name());
        }

        // list generated attributes
        System.out.println("All attributes:");
        for (AttributeDTO a : master.Attribute().listTypes()) {
            System.out.print(" " + a.getType());
        }
        System.out.println();

        for (Long job_id : jobIDs) {
            System.out.println("Attributes for job " + job_id + ": ");
            for (AttributeDTO a : master.Attribute().listTypesByJob(job_id)) {
                System.out.print(" " + a.getType());
                String attr = a.getType();

                // fetch attribute distribution and write to file
                List<Long> l = new ArrayList<Long>();
                l.add(seqrun_id);
                AttributeDistribution distribution = master.Attribute().getDistribution(attr, job_id, l);

                FileWriter w = new FileWriter("dist_" + attr);
                for (AttributeCount ac : distribution.getAttributecountList()) {
                    AttributeDTO attribute = ac.getAttribute();
                    Long count = ac.getCount();
                    w.write(attribute.getType());
                    w.write("\t");
                    w.write(count.toString());
                    w.write("\n");
                }
                w.close();

            }
            System.out.println();
        }

        //System.out.println("sending cancel()");
        //master.Job().cancel(lastjob);
        // deleting the toplevel obj  will also remove everything else
        //master.Habitat().delete(hab_id);
    }

    public static void printObjTree(MGXMaster m) throws MGXServerException, MGXClientException {
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
