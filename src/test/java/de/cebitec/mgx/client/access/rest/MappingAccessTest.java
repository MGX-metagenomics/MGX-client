/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.client.access.rest;

import de.cebitec.mgx.client.MGXDTOMaster;
import de.cebitec.mgx.client.access.rest.util.MapFetcher;
import de.cebitec.mgx.client.exception.MGXClientException;
import de.cebitec.mgx.client.exception.MGXDTOException;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.client.mgxtestclient.TestMaster;
import de.cebitec.mgx.dto.dto.MappedSequenceDTO;
import de.cebitec.mgx.dto.dto.MappingDTO;
import de.cebitec.mgx.osgiutils.MGXOptions;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import static org.ops4j.pax.exam.CoreOptions.bundle;
import static org.ops4j.pax.exam.CoreOptions.junitBundles;
import static org.ops4j.pax.exam.CoreOptions.options;
import static org.ops4j.pax.exam.CoreOptions.systemProperty;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;

/**
 *
 * @author sjaenick
 */
@RunWith(PaxExam.class)
public class MappingAccessTest {

    @Configuration
    public static Option[] configuration() {
        return options(
                junitBundles(),
                MGXOptions.clientBundles(),
                systemProperty("org.ops4j.pax.logging.DefaultServiceLog.level").value("WARN"),
                bundle("reference:file:target/classes")
        );
    }

    @Test
    public void testFetchall() throws Exception {
        System.out.println("fetchall");
        MGXDTOMaster master = TestMaster.getRO();
        Iterator<MappingDTO> it = master.Mapping().fetchall();
        assertNotNull(it);
        Set<MappingDTO> data = new HashSet<>();
        while (it.hasNext()) {
            MappingDTO m = it.next();
            //System.err.println(m.getId());
            data.add(m);
        }
        assertEquals(1, data.size());
    }

    @Test
    public void testByValidSeqRun() throws Exception {
        System.out.println("testByValidSeqRun");
        MGXDTOMaster master = TestMaster.getRO();
        Iterator<MappingDTO> it = master.Mapping().bySeqRun(1);
        assertNotNull(it);
        int cnt = 0;
        while (it.hasNext()) {
            MappingDTO next = it.next();
            assertNotNull(next);
            cnt++;
        }
        assertEquals(1, cnt);
    }

    @Test
    public void testByInvalidSeqRun() {
        System.out.println("testByInvalidSeqRun");
        MGXDTOMaster master = TestMaster.getRO();
        Iterator<MappingDTO> iter = null;
        try {
            iter = master.Mapping().bySeqRun(100);
        } catch (MGXDTOException ex) {
            if (ex.getMessage().contains("No object of type SeqRun for ID 100.")) {
                return;
            }
            fail(ex.getMessage());
        }
        assertNull(iter);
    }

    @Test
    public void testByValidReference() throws Exception {
        System.out.println("testByValidReference");
        MGXDTOMaster master = TestMaster.getRO();
        Iterator<MappingDTO> it = master.Mapping().byReference(8);
        assertNotNull(it);
        int cnt = 0;
        while (it.hasNext()) {
            MappingDTO next = it.next();
            assertNotNull(next);
            cnt++;
        }
        assertEquals(1, cnt);
    }

    @Test
    public void testByInvalidReference() {
        System.out.println("testByInvalidReference");
        MGXDTOMaster master = TestMaster.getRO();
        Iterator<MappingDTO> iter = null;
        try {
            iter = master.Mapping().byReference(100);
        } catch (MGXDTOException ex) {
            if (ex.getMessage().contains("No object of type Reference for ID 100.")) {
                return;
            }
            fail(ex.getMessage());
        }
        assertNull(iter);
    }

    @Test
    public void testByValidJob() throws Exception {
        System.out.println("testByValidJob");
        MGXDTOMaster master = TestMaster.getRO();
        Iterator<MappingDTO> it = master.Mapping().byJob(124);
        assertNotNull(it);
        int cnt = 0;
        while (it.hasNext()) {
            MappingDTO next = it.next();
            assertNotNull(next);
            cnt++;
        }
        assertEquals(1, cnt);
    }

    @Test
    public void testByInvalidJob() {
        System.out.println("testByInvalidJob");
        MGXDTOMaster master = TestMaster.getRO();
        Iterator<MappingDTO> iter = null;
        try {
            Iterator<MappingDTO> it = master.Mapping().byJob(100);
        } catch (MGXDTOException ex) {
            if (ex.getMessage().contains("No object of type Job for ID 100.")) {
                return;
            }
            fail(ex.getMessage());
        }
        assertNull(iter);
    }

    @Test
    public void testOpenMapping() throws Exception {
        System.out.println("openMapping");
        MGXDTOMaster master = TestMaster.getRO();
        UUID uuid = master.Mapping().openMapping(30);
        assertNotNull(uuid);
        master.Mapping().closeMapping(uuid);
    }

    @Test
    public void testCloseInvalidSession() {
        System.out.println("testCloseInvalidSession");
        MGXDTOMaster master = TestMaster.getRO();
        try {
            master.Mapping().closeMapping(UUID.randomUUID());
        } catch (MGXDTOException ex) {
            if (ex.getMessage().startsWith("No mapping session for")) {
                // ok
                return;
            }
            fail(ex.getMessage());
        }
        fail("Closing a non-existing session should indicate a possible timeout.");
    }

    @Test
    public void testInvalidUUID() {
        System.out.println("invalidUUID");
        MGXDTOMaster master = TestMaster.getRO();
        try {
            master.Mapping().byReferenceInterval(UUID.randomUUID(), 0, 500000);
        } catch (MGXServerException ex) {
            if (ex.getMessage().startsWith("No mapping session for")) {
                return;
            }
            fail(ex.getMessage());
        } catch (MGXClientException ex) {
            fail(ex.getMessage());
        } catch (MGXDTOException ex) {
            fail(ex.getMessage());
        }
    }

    @Test
    public void testMappedSeqs() throws Exception {
        System.out.println("testMappedSeqs");
        MGXDTOMaster master = TestMaster.getRO();
        UUID uuid = master.Mapping().openMapping(30);
        assertNotNull(uuid);
        int numMappedReads = 0;
        Iterator<MappedSequenceDTO> iter = master.Mapping().byReferenceInterval(uuid, 566470, 566480);
        assertNotNull(iter);

        while (iter.hasNext()) {
            MappedSequenceDTO ms = iter.next();
            System.err.println(ms.getSeqId());
            numMappedReads++;

        }
        master.Mapping().closeMapping(uuid);
        assertEquals(3, numMappedReads);
    }

    @Test
    public void testMappedSeqs2() throws Exception {
        System.out.println("testMappedSeqs2");
        MGXDTOMaster master = TestMaster.getRO();
        UUID uuid = master.Mapping().openMapping(30);
        assertNotNull(uuid);
        int numMappedReads = 0;
        Iterator<MappedSequenceDTO> iter = master.Mapping().byReferenceInterval(uuid, 0, 1000);
        assertNotNull(iter);

        while (iter.hasNext()) {
            MappedSequenceDTO ms = iter.next();
            numMappedReads++;

        }
        master.Mapping().closeMapping(uuid);
        assertEquals(0, numMappedReads);
    }

    @Test
    public void testMappingData() throws Exception {
        System.out.println("MappingData");
        MGXDTOMaster master = TestMaster.getRO();
        UUID uuid = master.Mapping().openMapping(30);
        assertNotNull(uuid);
        int numMappedReads = 0;
        Iterator<MappedSequenceDTO> iter = master.Mapping().byReferenceInterval(uuid, 0, 500000);
        assertNotNull(iter);

        MappedSequenceDTO testms = null;
        while (iter.hasNext()) {
            MappedSequenceDTO ms = iter.next();
            numMappedReads++;
            if (ms.getSeqId() == 55550) {
                testms = ms;
            }
        }
        master.Mapping().closeMapping(uuid);
        assertEquals(94, numMappedReads);

        assertNotNull(testms);
        assertEquals(23010, testms.getStart());
        assertEquals(22867, testms.getStop());
        assertEquals(71, testms.getIdentity(), 0.9);
    }

    @Test
    public void testMappingMaxCoverage() {
        System.out.println("mappingMaxCoverage");
        MGXDTOMaster master = TestMaster.getRO();
        UUID uuid = null;
        try {
            uuid = master.Mapping().openMapping(30);
        } catch (MGXDTOException ex) {
            fail(ex.getMessage());
        }
        long cov = -1;
        try {
            cov = master.Mapping().getMaxCoverage(uuid);
        } catch (MGXDTOException ex) {
            fail(ex.getMessage());
        }

        assertEquals(3, cov);

        try {
            master.Mapping().closeMapping(uuid);
        } catch (MGXDTOException ex) {
            fail(ex.getMessage());
        }
    }

    @Test
    public void testMappingConcurrentAccess() {
        System.out.println("MappingConcurrentAccess");
        MGXDTOMaster master = TestMaster.getRO();
        UUID uuid = null;
        try {
            uuid = master.Mapping().openMapping(30);
        } catch (MGXDTOException ex) {
            fail(ex.getMessage());
        }
        assertNotNull(uuid);
        CountDownLatch latch = new CountDownLatch(1);
        List<MapFetcher> l = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            MapFetcher f = new MapFetcher(latch, master, uuid);
            l.add(f);
            new Thread(f).start();
            //f.execute();
        }

        latch.countDown();

        for (MapFetcher f : l) {
            Iterator<MappedSequenceDTO> i = null;
            try {
                i = f.get();
            } catch (InterruptedException | ExecutionException ex) {
                fail(ex.getMessage());
            }
            assertNotNull(i);
        }
        try {
            master.Mapping().closeMapping(uuid);
        } catch (MGXDTOException ex) {
            fail(ex.getMessage());
        }
    }

}
