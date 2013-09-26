package de.cebitec.mgx.client.access.rest;

import de.cebitec.mgx.client.MGXDTOMaster;
import de.cebitec.mgx.client.exception.MGXClientException;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.client.mgxtestclient.TestMaster;
import de.cebitec.mgx.dto.dto.JobDTO;
import de.cebitec.mgx.dto.dto.JobParameterDTO;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
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
        master = TestMaster.get();
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
        assertEquals(10, jobs.size());
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

//    @Test
//    public void testByAttributeTypeAndSeqRun() throws Exception {
//        System.out.println("ByAttributeTypeAndSeqRun");
//        Iterable<JobDTO> jobs = master.Job().ByAttributeTypeAndSeqRun(9, 1);
//        assertNotNull(jobs);
//        int cnt = 0;
//        for (JobDTO j : jobs) {
//            cnt++;
//        }
//        assertEquals(4, cnt);
//    }
    @Test
    public void testBySeqRun() throws Exception {
        System.out.println("BySeqRun");
        Iterable<JobDTO> jobs = master.Job().BySeqRun(1);
        assertNotNull(jobs);
        int cnt = 0;
        for (JobDTO j : jobs) {
            cnt++;
        }
        assertEquals(7, cnt);
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