/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.client.access.rest;

import de.cebitec.mgx.client.MGXDTOMaster;
import de.cebitec.mgx.client.exception.MGXClientException;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.client.mgxtestclient.TestMaster;
import de.cebitec.mgx.dto.dto.MappedSequenceDTO;
import de.cebitec.mgx.dto.dto.MappingDTO;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import javax.swing.SwingWorker;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author sjaenick
 */
public class MappingAccessTest {

    private MGXDTOMaster master;

    public MappingAccessTest() {
    }

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
    public void testBySeqRun() throws Exception {
        System.out.println("BySeqRun");
        Iterator<MappingDTO> it = master.Mapping().BySeqRun(1);
        assertNotNull(it);
        Set<MappingDTO> data = new HashSet<>();
        while (it.hasNext()) {
            data.add(it.next());
        }
        assertEquals(1, data.size());
    }

    @Test
    public void testOpenMapping() throws Exception {
        System.out.println("openMapping");
        UUID uuid = master.Mapping().openMapping(30);
        assertNotNull(uuid);
        master.Mapping().closeMapping(uuid);
    }

    @Test
    public void testInvalidUUID() {
        System.out.println("invalidUUID");
        try {
            master.Mapping().byReferenceInterval(UUID.randomUUID(), 0, 500000);
        } catch (MGXServerException ex) {
            if (ex.getMessage().startsWith("No mapping session for")) {
                return;
            }
            fail(ex.getMessage());
        } catch (MGXClientException ex) {
            fail(ex.getMessage());
        }
    }

    @Test
    public void testMappedSeqs() throws Exception {
        System.out.println("testMappedSeqs");
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
    public void testMappingData() throws Exception {
        System.out.println("MappingData");
        UUID uuid = master.Mapping().openMapping(30);
        assertNotNull(uuid);
        int numMappedReads = 0;
        Iterator<MappedSequenceDTO> iter = master.Mapping().byReferenceInterval(uuid, 0, 500000);
        assertNotNull(iter);

        MappedSequenceDTO testms = null;
        while (iter.hasNext()) {
            MappedSequenceDTO ms = iter.next();
            numMappedReads++;
            if (ms.getStart() == 22868) {
                testms = ms;
            }
        }
        master.Mapping().closeMapping(uuid);
        assertEquals(94, numMappedReads);

        assertNotNull(testms);
        assertEquals(55550, testms.getSeqId());
        assertEquals(23011, testms.getStop());
        assertEquals(71, testms.getIdentity());
    }

    @Test
    public void testMappingMaxCoverage() {
        System.out.println("mappingMaxCoverage");
        UUID uuid = null;
        try {
            uuid = master.Mapping().openMapping(30);
        } catch (MGXServerException ex) {
            fail(ex.getMessage());
        }
        long cov = -1;
        try {
            cov = master.Mapping().getMaxCoverage(uuid);
        } catch (MGXServerException ex) {
            fail(ex.getMessage());
        }

        assertEquals(3, cov);

        try {
            master.Mapping().closeMapping(uuid);
        } catch (MGXServerException ex) {
            fail(ex.getMessage());
        }
    }

    @Test
    public void testMappingConcurrentAccess() {
        System.out.println("MappingConcurrentAccess");
        UUID uuid = null;
        try {
            uuid = master.Mapping().openMapping(30);
        } catch (MGXServerException ex) {
            fail(ex.getMessage());
        }
        assertNotNull(uuid);
        CountDownLatch latch = new CountDownLatch(1);
        List<Fetcher> l = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Fetcher f = new Fetcher(latch, master, uuid);
            l.add(f);
            f.execute();
        }

        latch.countDown();

        for (Fetcher f : l) {
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
        } catch (MGXServerException ex) {
            fail(ex.getMessage());
        }
    }

    private class Fetcher extends SwingWorker<Iterator<MappedSequenceDTO>, Void> {

        private final CountDownLatch latch;
        private final MGXDTOMaster master;
        private final UUID session;

        public Fetcher(CountDownLatch latch, MGXDTOMaster master, UUID session) {
            this.latch = latch;
            this.master = master;
            this.session = session;
        }

        @Override
        protected Iterator<MappedSequenceDTO> doInBackground() throws Exception {
            latch.await();
            return master.Mapping().byReferenceInterval(session, 0, 500000);
        }
    }
//
//    @Test
//    public void testByReference() throws Exception {
//        System.out.println("ByReference");
//        Iterator<MappingDTO> it = master.Mapping().ByReference(1);
//        assertNotNull(it);
//        Set<MappingDTO> data = new HashSet<>();
//        while (it.hasNext()) {
//            data.add(it.next());
//        }
//        assertEquals(0, data.size());
//    }
//
//    @Test
//    public void testFetch() throws Exception {
//        System.out.println("fetch");
//    }
}
