/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.client.access.rest;

import de.cebitec.mgx.client.MGXDTOMaster;
import de.cebitec.mgx.client.exception.MGXDTOException;
import de.cebitec.mgx.client.mgxtestclient.TestMaster;
import de.cebitec.mgx.dto.dto.DNAExtractDTO;
import de.cebitec.mgx.dto.dto.SampleDTO;
import de.cebitec.mgx.dto.dto.SeqRunDTO;
import de.cebitec.mgx.dto.dto.TaskDTO;
import java.util.Iterator;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.Test;

/**
 *
 * @author sj
 */
//@RunWith(PaxExam.class)
public class DNAExtractAccessTest {

//    @Configuration
//    public static Option[] configuration() {
//        return options(
//                junitBundles(),
//                MGXOptions.clientBundles(),
//                systemProperty("org.ops4j.pax.logging.DefaultServiceLog.level").value("WARN"),
//                bundle("reference:file:target/classes")
//        );
//    }

    /**
     * Test of fetchall method, of class DNAExtractAccess.
     */
    @Test
    public void testFetchall() throws Exception {
        System.out.println("fetchall");
        MGXDTOMaster master = TestMaster.getRO();
        Iterator<DNAExtractDTO> iter = master.DNAExtract().fetchall().getExtractList().iterator();
        assertNotNull(iter);
        int cnt = 0;
        while (iter.hasNext()) {
            DNAExtractDTO ex = iter.next();
            assertNotNull(ex);
            cnt++;
        }
        assertEquals(1, cnt);
    }

    @Test
    public void testByInvalidSample() {
        System.out.println("testByInvalidSample");
        MGXDTOMaster master = TestMaster.getRO();
        Iterator<DNAExtractDTO> iter = null;
        try {
            iter = master.DNAExtract().bySample(100);
        } catch (MGXDTOException ex) {
            if (ex.getMessage().contains("No object of type Sample for ID")) {
                return;
            }
            fail(ex.getMessage());
        }
        assertNotNull(iter);
        assertFalse(iter.hasNext());
    }

    @Test
    public void testByValidSample() {
        System.out.println("testByValidSample");
        MGXDTOMaster master = TestMaster.getRO();
        Iterator<DNAExtractDTO> iter = null;
        try {
            // sample 1: "unknown material"
            iter = master.DNAExtract().bySample(1);
        } catch (MGXDTOException ex) {
            fail(ex.getMessage());
        }
        assertNotNull(iter);
        int cnt = 0;
        while (iter.hasNext()) {
            DNAExtractDTO ex = iter.next();
            assertNotNull(ex);
            cnt++;
        }
        assertEquals(1, cnt);
    }

    /**
     * Test of fetch method, of class DNAExtractAccess.
     */
    @Test
    public void testFetch() throws Exception {
        System.out.println("fetch");
        MGXDTOMaster master = TestMaster.getRO();
        DNAExtractDTO ex = master.DNAExtract().fetch(48);
        assertNotNull(ex);
        assertEquals("unknown DNA extract", ex.getName());
    }

    /**
     * Test of create method, of class DNAExtractAccess.
     */
    @Test
    public void testCreate() throws Exception {
        System.out.println("create");
        MGXDTOMaster master = TestMaster.getRW();
        
        SampleDTO sample = master.Sample().fetch(1);
        assertNotNull(sample);
        
        DNAExtractDTO d = DNAExtractDTO.newBuilder()
                .setMethod("foo")
                .setName("unittest extract")
                .setDescription("no description")
                .setSampleId(sample.getId())
                .build();
        long id = master.DNAExtract().create(d);
        assertTrue(id > 0, "ID of new extract should be >0");
        
        Iterator<SeqRunDTO> runIter = master.SeqRun().byExtract(id);
        assertNotNull(runIter);
        assertFalse(runIter.hasNext(), "A newly created extract does not have seqruns.");

        // delete it again
        try {
            UUID delTask = master.DNAExtract().delete(id);
            TaskDTO t = master.Task().get(delTask);
            while (!(t.getState().equals(TaskDTO.TaskState.FINISHED) || t.getState().equals(TaskDTO.TaskState.FAILED))) {
                Thread.sleep(500);
                t = master.Task().get(delTask);
            }
            if (t.getState().equals(TaskDTO.TaskState.FAILED)) {
                fail("Task failed.");
            }
        } catch (MGXDTOException | InterruptedException ex) {
            fail(ex.getMessage());
        }
    }

    /**
     * Test of update method, of class DNAExtractAccess.
     */
    @Test
    public void testUpdate() {
        System.out.println("update");
        MGXDTOMaster master = TestMaster.getRW();
        DNAExtractDTO d = DNAExtractDTO.newBuilder()
                .setMethod("foo")
                .setName("bar")
                .setDescription("no description")
                .setSampleId(1)
                .build();
        try {
            master.DNAExtract().update(d);
        } catch (MGXDTOException ex) {
            if (ex.getMessage().contains("Cannot update object of type DNAExtract without an ID")) {
                return;
            }
            fail(ex.getMessage());
        }
        fail("updating a non-existing object should not succeed.");
    }

    @Test
    public void testDeleteInvalid() throws Exception {
        System.out.println("testDeleteInvalid");
        MGXDTOMaster master = TestMaster.getRW();
        try {
            master.DNAExtract().delete(100);
        } catch (MGXDTOException ex) {
            if (ex.getMessage().contains("No object of type DNAExtract for ID 100")) {
                return;
            }
            fail(ex.getMessage());
        }
        fail("deleting a non-existing DNA extract should produce an error");
    }

}
