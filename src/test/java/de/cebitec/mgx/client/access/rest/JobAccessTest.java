package de.cebitec.mgx.client.access.rest;

import de.cebitec.mgx.client.MGXDTOMaster;
import de.cebitec.mgx.client.exception.MGXClientException;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.client.mgxtestclient.TestMaster;
import de.cebitec.mgx.dto.dto.JobDTO;
import de.cebitec.mgx.dto.dto.JobDTO.JobState;
import de.cebitec.mgx.dto.dto.JobParameterDTO;
import de.cebitec.mgx.dto.dto.TaskDTO;
import de.cebitec.mgx.dto.dto.TaskDTO.TaskState;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author sj
 */
public class JobAccessTest {

    private MGXDTOMaster master;

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        master = TestMaster.getRO();
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testFetchall() throws Exception {
        System.out.println("fetchall");
        Iterator<JobDTO> it = master.Job().fetchall();
        assertNotNull(it);
        Set<JobDTO> jobs = new HashSet<>();
        while (it.hasNext()) {
            jobs.add(it.next());
        }
        assertEquals(12, jobs.size());
    }

    @Test
    public void testFetch() throws Exception {
        System.out.println("fetch");
        JobDTO job = master.Job().fetch(1);
        assertNotNull(job);
    }

    @Test
    public void testDelete() {
        System.out.println("delete");
        boolean failed = false;
        try {
            master.Job().delete(1);
        } catch (MGXServerException | MGXClientException ex) {
            failed = true;
        }
        assertTrue(failed);
    }

    @Test
    public void testCreateJob() {
        System.out.println("createJob");
        MGXDTOMaster m = TestMaster.getRW();
        JobDTO dto = JobDTO.newBuilder().setCreator("Unittest")
                .setSeqrunId(2)
                .setToolId(2)
                .setState(JobState.CREATED)
                .build();
        long job_id = -1;
        try {
            job_id = m.Job().create(dto);
        } catch (MGXServerException | MGXClientException ex) {
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
        } catch (MGXServerException | MGXClientException | InterruptedException ex) {
            fail(ex.getMessage());
        }
    }

    @Test
    public void testVerifyJob() {
        System.out.println("verifyJob");
        MGXDTOMaster m = TestMaster.getRW();
        JobDTO dto = JobDTO.newBuilder().setCreator("Unittest")
                .setSeqrunId(2)
                .setToolId(2)
                .setState(JobState.CREATED)
                .build();
        long job_id = -1;
        try {
            job_id = m.Job().create(dto);
        } catch (MGXServerException | MGXClientException ex) {
            fail(ex.getMessage());
        }
        assertTrue(job_id > 0);

        //
        // verify job
        //
        boolean verified = false;
        try {
            verified = m.Job().verify(job_id);
        } catch (MGXServerException ex) {
            fail(ex.getMessage());
        }
        assertTrue(verified);
        
        
        //
        // check job state
        //
        JobDTO job = null;
        try {
            job = m.Job().fetch(job_id);
        } catch (MGXServerException | MGXClientException ex) {
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
        } catch (MGXServerException | MGXClientException | InterruptedException ex) {
            fail(ex.getMessage());
        }
    }

    @Test
    public void testBySeqRun() throws Exception {
        System.out.println("BySeqRun");
        Iterable<JobDTO> jobs = master.Job().BySeqRun(1);
        assertNotNull(jobs);
        int cnt = 0;
        for (JobDTO j : jobs) {
            cnt++;
        }
        assertEquals(9, cnt);
    }

    @Test
    public void testGetParameters() throws Exception {
        System.out.println("getParameters");
        Iterable<JobParameterDTO> parameters = master.Job().getParameters(3);
        int params = 0;
        for (JobParameterDTO d : parameters) {
            params++;
        }
        assertEquals(2, params);
    }

    @Test
    public void testSetParameters() throws Exception {
        System.out.println("setParameters");

    }

    @Test
    public void testGetError() throws Exception {
        System.out.println("getError");
    }
}
