package de.cebitec.mgx.client.access.rest;

import de.cebitec.mgx.client.MGXDTOMaster;
import de.cebitec.mgx.client.datatransfer.ReferenceUploader;
import de.cebitec.mgx.client.exception.MGXClientException;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.client.mgxtestclient.TestMaster;
import de.cebitec.mgx.dto.dto;
import de.cebitec.mgx.dto.dto.ReferenceDTO;
import de.cebitec.mgx.dto.dto.RegionDTO;
import de.cebitec.mgx.dto.dto.TaskDTO.TaskState;
import java.io.File;
import java.util.Iterator;
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
 * @author sjaenick
 */
public class ReferenceAccessTest {

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
        master = null;
    }

    @Test
    public void testFetchall() {
        System.out.println("testFetchall");
        Iterator<ReferenceDTO> iter = null;
        try {
            iter = master.Reference().fetchall();
        } catch (MGXServerException | MGXClientException ex) {
            fail(ex.getMessage());
        }
        assertNotNull(iter);
        int refCnt = 0;
        while (iter.hasNext()) {
            iter.next();
            refCnt++;
        }
        assertEquals(1, refCnt);
    }

    @Test
    public void testInterval() {
        System.out.println("testInterval");
        Iterator<RegionDTO> iter = null;
        try {
            iter = master.Reference().byReferenceInterval(4, 0, 999999999);
        } catch (MGXClientException | MGXServerException ex) {
            Logger.getLogger(ReferenceAccessTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        assertNotNull(iter);
        int cnt = 0;
        while (iter.hasNext()) {
            iter.next();
            cnt++;
        }
        assertEquals(8563, cnt);
    }

    @Test
    public void testCreate() {
        try {
            System.out.println("testCreate");
            MGXDTOMaster m = TestMaster.get2();
            if (m == null) {
                System.err.println("  private test, skipped");
                return;
            }
            ReferenceDTO ref = ReferenceDTO.newBuilder()
                    .setName("testref")
                    .setLength(42)
                    .build();
            long refId = -1;
            refId = m.Reference().create(ref);
            assertNotEquals(-1, refId);

            m.Reference().delete(refId);
        } catch (MGXServerException | MGXClientException ex) {
            fail(ex.getMessage());
        }
    }

    @Test
    public void testUploadGBK() {
        System.out.println("testUpload");
        MGXDTOMaster m = TestMaster.get2();
        if (m == null) {
            System.err.println("  private test, skipped");
            return;
        }
        File f = new File("src/test/resources/NC_017106.gbk");
        if (!f.exists()) {
            fail();
        }
        ReferenceUploader up = m.Reference().createUploader(f);
        assertNotNull(up);
        boolean success = up.upload();
        if (!success) {
            fail(up.getErrorMessage());
        }

        assertEquals(1, up.getReferenceIDs().size());
        long refId = up.getReferenceIDs().get(0);

        try {
            Iterator<RegionDTO> iter = m.Reference().byReferenceInterval(refId, 0, 999999999);
            assertNotNull(iter);
            int cnt = 0;
            while (iter.hasNext()) {
                iter.next();
                cnt++;
            }
            assertEquals(3, cnt);
        } catch (MGXServerException | MGXClientException ex) {
            fail(ex.getMessage());
        }

        // delete it again
        try {
            UUID uuid = m.Reference().delete(refId);
            assertNotNull(uuid);
            TaskState state = m.Task().get(uuid).getState();
            while (!state.equals(TaskState.FINISHED)) {
                state = m.Task().get(uuid).getState();
                if (state.equals(TaskState.FAILED)) {
                    fail();
                }
            }
        } catch (MGXServerException | MGXClientException ex) {
            fail(ex.getMessage());
        }

        // make sure they are all gone..
        Iterator<ReferenceDTO> iter = null;
        try {
            iter = m.Reference().fetchall();
        } catch (MGXServerException | MGXClientException ex) {
            fail(ex.getMessage());
        }
        assertNotNull(iter);
        int refCnt = 0;
        while (iter.hasNext()) {
            iter.next();
            refCnt++;
        }
        assertEquals(0, refCnt);
    }

    @Test
    public void testGetSequence() {
        System.out.println("testGetSequence");
        String seq = null;
        try {
            seq = master.Reference().getSequence(4, 0, 9);
        } catch (MGXServerException | MGXClientException ex) {
            fail(ex.getMessage());
        }
        assertNotNull(seq);
        assertEquals("ttgtgcacac", seq);
    }
}
