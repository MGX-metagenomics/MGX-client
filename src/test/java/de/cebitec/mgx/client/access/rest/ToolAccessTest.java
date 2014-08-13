package de.cebitec.mgx.client.access.rest;

import de.cebitec.mgx.client.MGXDTOMaster;
import de.cebitec.mgx.client.exception.MGXClientException;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.client.mgxtestclient.TestMaster;
import de.cebitec.mgx.dto.dto.JobParameterDTO;
import de.cebitec.mgx.dto.dto.ToolDTO;
import java.util.Iterator;
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
public class ToolAccessTest {

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
    public void testGetAvailableParameters_long_boolean() throws Exception {
        System.out.println("getAvailableParameters");
        Iterable<JobParameterDTO> it = master.Tool().getAvailableParameters(3, false);
        assertNotNull(it);
        int cnt = 0;
        for (JobParameterDTO jp : it) {
            assertNotNull(jp.getDisplayName());
            cnt++;
        }
        assertEquals(2, cnt);
    }

    @Test
    public void testGetAvailableParameters_frhit() throws Exception {
        System.out.println("testGetAvailableParameters_frhit");
        Iterable<JobParameterDTO> it = master.Tool().getAvailableParameters(17, false);
        assertNotNull(it);
        int cnt = 0;
        for (JobParameterDTO jp : it) {
            assertNotNull(jp.getDisplayName());
            assertEquals("ConfigMGXReference", jp.getType());
            assertFalse(jp.getIsOptional());
            cnt++;
        }
        assertEquals(1, cnt);
    }
//
//    @Test
//    public void testGetAvailableParameters_dtoToolDTO() throws Exception {
//        System.out.println("getAvailableParameters");
//        dto.ToolDTO dto = null;
//        ToolAccess instance = new ToolAccess();
//        Iterable expResult = null;
//        Iterable result = instance.getAvailableParameters(dto);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }

    @Test
    public void testFetchall() throws Exception {
        System.out.println("fetchall");
        Iterator<ToolDTO> it = master.Tool().fetchall();
        assertNotNull(it);
        int cnt = 0;
        while (it.hasNext()) {
            cnt++;
            it.next();
        }
        assertEquals(17, cnt);
    }

    @Test
    public void testListGlobalTools() throws Exception {
        System.out.println("listGlobalTools");
        Iterator<ToolDTO> it = master.Tool().listGlobalTools();
        assertNotNull(it);
        int cnt = 0;
        while (it.hasNext()) {
            cnt++;
            it.next();
        }
        assertEquals(26, cnt);
    }

    @Test
    public void testDelete() {
        System.out.println("delete");
        boolean failed = false;
        try {
            master.Tool().delete(1);
        } catch (MGXServerException | MGXClientException ex) {
            failed = true;
        }
        assertTrue(failed);
    }

    @Test
    public void testDeleteInvalidId() {
        System.out.println("deleteInvalidId");
        boolean failed = false;
        try {
            master.Tool().delete(-1);
        } catch (MGXServerException ex) {
            fail(ex.getMessage());
        } catch (MGXClientException ex) {
            failed = true;
        }
        assertTrue(failed);
    }

    @Test
    public void testFetch() throws Exception {
        System.out.println("fetch");
        ToolDTO t = master.Tool().fetch(1);
        assertNotNull(t);
        assertNotNull(t.getName());
    }

    @Test
    public void testCreate() {
        System.out.println("create");
        ToolDTO t = null;
        boolean failed = false;
        try {
            master.Tool().create(t);
        } catch (MGXServerException ex) {
            fail(ex.getMessage());
        } catch (MGXClientException ex) {
            failed = true;
        }
        assertTrue(failed);
    }

    @Test
    public void testByJob() throws Exception {
        System.out.println("ByJob");
        ToolDTO t = master.Tool().ByJob(1);
        assertNotNull(t);
        assertNotNull(t.getName());
    }
}
