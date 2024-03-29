package de.cebitec.mgx.client.access.rest;

import de.cebitec.mgx.client.MGXDTOMaster;
import de.cebitec.mgx.client.exception.MGXClientException;
import de.cebitec.mgx.client.exception.MGXDTOException;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.client.mgxtestclient.TestMaster;
import de.cebitec.mgx.dto.dto.JobParameterDTO;
import de.cebitec.mgx.dto.dto.ToolDTO;
import java.util.Iterator;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.Test;

/**
 *
 * @author sj
 */
//@RunWith(PaxExam.class)
public class ToolAccessTest {

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
    public void testGetAvailableParameters_long_boolean() throws Exception {
        System.out.println("getAvailableParameters");
        MGXDTOMaster master = TestMaster.getRO();
        Iterable<JobParameterDTO> it = master.Tool().getAvailableParameters(18, false);
        assertNotNull(it);
        int cnt = 0;
        for (JobParameterDTO jp : it) {
            assertNotNull(jp.getDisplayName());
            cnt++;
        }
        assertEquals(2, cnt);
    }

    @Test
    public void testGetAvailableParameters_bowtie() throws Exception {
        System.out.println("testGetAvailableParameters_bowtie");
        MGXDTOMaster master = TestMaster.getRO();
        Iterable<JobParameterDTO> it = master.Tool().getAvailableParameters(19, false);
        assertNotNull(it);
        int cnt = 0;
        for (JobParameterDTO jp : it) {
            assertNotNull(jp.getDisplayName());
            assertEquals("Conveyor.MGX.GetMGXReference", jp.getClassName());
            assertEquals("GetMGXReference", jp.getDisplayName());
            assertEquals("ConfigMGXReference", jp.getType());
            assertEquals(-1L, jp.getId()); // invalid identifier
            assertEquals(3, jp.getNodeId());
            assertEquals("refId", jp.getParameterName());
            assertEquals("", jp.getParameterValue());
            assertFalse(jp.getIsOptional());
            cnt++;
        }
        assertEquals(1, cnt);
    }

    @Test
    public void testFetchall() throws Exception {
        System.out.println("fetchall");
        MGXDTOMaster master = TestMaster.getRO();
        Iterator<ToolDTO> it = master.Tool().fetchall().getToolList().iterator();
        assertNotNull(it);
        int cnt = 0;
        while (it.hasNext()) {
            cnt++;
            ToolDTO tool = it.next();
            //System.err.println(tool.getName());
        }
        assertEquals(4, cnt);
    }

    @Test
    public void testListGlobalTools() throws Exception {
        System.out.println("listGlobalTools");
        MGXDTOMaster master = TestMaster.getRO();
        Iterator<ToolDTO> it = master.Tool().listGlobalTools();
        assertNotNull(it);
        int cnt = 0;
        while (it.hasNext()) {
            cnt++;
            it.next();
        }
        assertTrue(cnt > 25);
    }

    @Test
    public void testDelete() {
        System.out.println("delete");
        MGXDTOMaster master = TestMaster.getRO();
        boolean failed = false;
        try {
            master.Tool().delete(1);
        } catch (MGXDTOException ex) {
            failed = true;
        }
        // read-only access is not allowed to delete anything, thus
        // deleting a tool has to fail
        assertTrue(failed);
    }

    @Test
    public void testDeleteInvalidId() {
        System.out.println("deleteInvalidId");
        MGXDTOMaster master = TestMaster.getRO();
        boolean failed = false;
        try {
            master.Tool().delete(-1);
        } catch (MGXServerException ex) {
            fail(ex.getMessage());
        } catch (MGXClientException ex) {
            failed = true;
        } catch (MGXDTOException ex) {
        }
        assertTrue(failed);
    }

    @Test
    public void testFetch() throws Exception {
        System.out.println("fetch");
        MGXDTOMaster master = TestMaster.getRO();
        ToolDTO t = master.Tool().fetch(17);
        assertNotNull(t);
        assertNotNull(t.getName());
    }

    @Test
    public void testFetchXML() throws Exception {
        System.out.println("fetchXML");
        MGXDTOMaster master = TestMaster.getRO();
        String xmlData = master.Tool().getDefinition(17);
        assertNotNull(xmlData);
        assertTrue(xmlData.startsWith("<?xml version"));
    }

    @Test
    public void testCreate() {
        System.out.println("create");
        MGXDTOMaster master = TestMaster.getRO();
        ToolDTO t = null;
        boolean failed = false;
        try {
            master.Tool().create(t);
        } catch (MGXServerException ex) {
            fail(ex.getMessage());
        } catch (MGXClientException ex) {
            failed = true;
        } catch (MGXDTOException ex) {
        }
        assertTrue(failed);
    }

    @Test
    public void testByJob() throws Exception {
        System.out.println("ByJob");
        MGXDTOMaster master = TestMaster.getRO();
        ToolDTO t = master.Tool().byJob(9);
        assertNotNull(t);
        assertNotNull(t.getName());
    }
}
