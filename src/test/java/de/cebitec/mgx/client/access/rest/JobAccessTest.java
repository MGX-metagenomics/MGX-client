package de.cebitec.mgx.client.access.rest;

import de.cebitec.mgx.client.MGXDTOMaster;
import de.cebitec.mgx.client.exception.MGXDTOException;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.client.mgxtestclient.TestMaster;
import de.cebitec.mgx.dto.dto.JobDTO;
import de.cebitec.mgx.dto.dto.JobParameterDTO;
import de.cebitec.mgx.dto.dto.JobParameterListDTO;
import de.cebitec.mgx.dto.dto.JobState;
import de.cebitec.mgx.dto.dto.MGXString;
import de.cebitec.mgx.dto.dto.TaskDTO;
import de.cebitec.mgx.dto.dto.TaskDTO.TaskState;
import de.cebitec.mgx.dto.dto.ToolDTO;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.Test;

/**
 *
 * @author sj
 */
//@RunWith(PaxExam.class)
public class JobAccessTest {

//    @Configuration
//    public static Option[] configuration() {
//        return options(
//                junitBundles(),
//                MGXOptions.clientBundles(),
//                systemProperty("org.ops4j.pax.logging.DefaultServiceLog.level").value("WARN"),
//                bundle("reference:file:target/classes")
//        );
//    }

    @Test
    public void testFetchall() throws Exception {
        System.out.println("fetchall");
        MGXDTOMaster master = TestMaster.getRO();
        Iterator<JobDTO> it = master.Job().fetchall();
        assertNotNull(it);
        Set<JobDTO> jobs = new HashSet<>();
        while (it.hasNext()) {
            JobDTO j = it.next();
            if (j.hasParameters()) {
                List<JobParameterDTO> params = j.getParameters().getParameterList();
                for (JobParameterDTO jp : params) {
                    assertNotNull(jp.getType());
                    assertNotEquals("", jp.getType());
                }
            }
            jobs.add(j);
        }
        assertEquals(6, jobs.size());
    }

    @Test
    public void testFetch() throws Exception {
        System.out.println("fetch");
        MGXDTOMaster master = TestMaster.getRO();
        JobDTO job = master.Job().fetch(9);
        assertNotNull(job);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));

        assertEquals("2023-02-10T10:13:32Z", sdf.format(new Date(1000L * job.getStartDate())));
        assertEquals("2023-02-10T10:21:33Z", sdf.format(new Date(1000L * job.getFinishDate())));
    }


    @Test
    public void testDelete() {
        System.out.println("delete");
        MGXDTOMaster master = TestMaster.getRO();
        boolean failed = false;
        try {
            master.Job().delete(1);
        } catch (MGXDTOException ex) {
            failed = true;
        }
        assertTrue(failed);
    }

    @Test
    public void testDeleteInvalid() throws Exception {
        System.out.println("testDeleteInvalid");
        MGXDTOMaster master = TestMaster.getRW();
        try {
            master.Job().delete(999999999);
        } catch (MGXDTOException ex) {
            System.err.println(ex.getMessage());
            if (ex.getMessage().contains("No object of type Job for ID 999999999")) {
                return;
            }
        }
        fail("deleting a non-existing job should produce an error");
    }

    @Test
    public void testCancel() {
        System.out.println("testCancelGuest");
        MGXDTOMaster master = TestMaster.getRO();
        boolean failed = false;
        try {
            master.Job().cancel(666);
        } catch (MGXDTOException ex) {
            failed = ex.getMessage().contains("access denied");
        }
        assertTrue(failed);
    }

    @Test
    public void testCreateJob() {
        System.out.println("createJob");
        MGXDTOMaster m = TestMaster.getRW();
        JobDTO dto = JobDTO.newBuilder().setCreator("Unittest")
                .addSeqrun(2)
                .setToolId(18)
                .setState(JobState.CREATED)
                .setParameters(JobParameterListDTO.newBuilder().build())
                .build();
        long job_id = -1;
        try {
            job_id = m.Job().create(dto);
        } catch (MGXDTOException ex) {
            fail(ex.getMessage());
        }
        assertTrue(job_id > 0);

        try {
            UUID delTask = m.Job().delete(job_id);
            TaskDTO t = m.Task().get(delTask);
            while (!(t.getState().equals(TaskState.FINISHED) || t.getState().equals(TaskState.FAILED))) {
                Thread.sleep(500);
                t = m.Task().get(delTask);
            }
            if (t.getState().equals(TaskState.FAILED)) {
                fail("Task failed.");
            }
        } catch (MGXDTOException | InterruptedException ex) {
            fail(ex.getMessage());
        }
    }

    @Test
    public void testVerifyJob() {
        System.out.println("verifyJob");
        MGXDTOMaster m = TestMaster.getRW();
        JobDTO dto = JobDTO.newBuilder().setCreator("Unittest")
                .addSeqrun(49)
                .setToolId(18)
                .setState(JobState.CREATED)
                .setParameters(JobParameterListDTO.newBuilder().build())
                .build();
        long job_id = -1;
        try {
            job_id = m.Job().create(dto);
        } catch (MGXDTOException ex) {
            fail(ex.getMessage());
        }
        assertTrue(job_id > 0);

        //
        // verify job
        //
        boolean verified = false;
        try {
            verified = m.Job().verify(job_id);
        } catch (MGXDTOException ex) {
            fail(ex.getMessage());
        }
        assertTrue(verified);

        //
        // check job state
        //
        JobDTO job = null;
        try {
            job = m.Job().fetch(job_id);
        } catch (MGXDTOException ex) {
            fail(ex.getMessage());
        }
        assertNotNull(job);
        assertEquals(JobState.VERIFIED, job.getState());

        // cleanup
        try {
            UUID delTask = m.Job().delete(job_id);
            TaskDTO t = m.Task().get(delTask);
            while (!(t.getState().equals(TaskState.FINISHED) || t.getState().equals(TaskState.FAILED))) {
                Thread.sleep(500);
                t = m.Task().get(delTask);
            }
            if (t.getState().equals(TaskState.FAILED)) {
                fail("Task failed.");
            }
        } catch (MGXDTOException | InterruptedException ex) {
            fail(ex.getMessage());
        }
    }

    @Test
    public void testBySeqRun() {
        System.out.println("BySeqRun");
        MGXDTOMaster master = TestMaster.getRO();
        Iterable<JobDTO> jobs = null;
        try {
            jobs = master.Job().bySeqRun(49);
        } catch (MGXDTOException ex) {
            Logger.getLogger(JobAccessTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        assertNotNull(jobs);
        int cnt = 0;
        for (JobDTO j : jobs) {
            System.err.println("Job " + j.getId() + " has " + j.getParameters().getParameterCount() + " parameters.");
            cnt++;
            if (j.hasParameters()) {
                List<JobParameterDTO> params = j.getParameters().getParameterList();
                for (JobParameterDTO jp : params) {
                    assertNotNull(jp.getType());
                    assertNotEquals("", jp.getType());
                }
            }
        }
        assertEquals(4, cnt);
    }

    @Test
    public void testByNonExistingSeqRun() {
        System.out.println("testByNonExistingSeqRun");
        MGXDTOMaster master = TestMaster.getRO();
        try {
            master.Job().bySeqRun(1000);
        } catch (MGXDTOException ex) {
            if (ex.getMessage().contains("No object of type SeqRun for ID 1000.")) {
                return;
            }
            fail(ex.getMessage());
        }
        fail("seqrun with id 1000 does not exist, an exception should have been thrown");
    }

    @Test
    public void testGetParameters() throws Exception {
        System.out.println("getParameters");
        MGXDTOMaster master = TestMaster.getRO();
        Iterable<JobParameterDTO> parameters = master.Job().getParameters(9);
        int params = 0;
        for (JobParameterDTO d : parameters) {
            params++;
        }
        assertEquals(2, params);
    }

    @Test
    public void testGetError() throws Exception {
        System.out.println("getError");
        MGXDTOMaster master = TestMaster.getRO();
        JobDTO job = master.Job().fetch(9);
        assertNotNull(job);
        MGXString error = master.Job().getError(9);
        assertNotNull(error);
        assertEquals("Job is not in FAILED state.", error.getValue());
    }

    @Test
    public void testParams() throws Exception {
        System.out.println("testParams");
        MGXDTOMaster master = TestMaster.getRO();
        Iterator<JobDTO> iter = master.Job().fetchall();
        JobDTO job = null;
        while (iter.hasNext()) {
            JobDTO curJob = iter.next();
            ToolDTO tool = master.Tool().byJob(curJob.getId());
            assertNotNull(tool);
            if ("Bowtie2".equals(tool.getName())) {
                job = curJob;
                break;
            }
        }
        assertNotNull(job);
        JobParameterListDTO params = job.getParameters();
        assertNotNull(params);
        assertEquals(1, params.getParameterCount());

        JobParameterDTO parameter = params.getParameter(0);
        assertNotNull(parameter);
        assertEquals(23, parameter.getId());
        assertEquals("GetMGXReference", parameter.getDisplayName());
        assertEquals("ConfigMGXReference", parameter.getType());
        assertEquals("reference", parameter.getUserName());
        assertEquals("reference sequence to map reads against", parameter.getUserDesc());
        assertFalse(parameter.getIsOptional());
        assertEquals(3, parameter.getNodeId());
        assertEquals("refId", parameter.getParameterName());
        assertEquals("65", parameter.getParameterValue());
        assertEquals("Conveyor.MGX.GetMGXReference", parameter.getClassName());
    }
}
