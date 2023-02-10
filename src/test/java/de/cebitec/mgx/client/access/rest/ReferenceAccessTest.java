package de.cebitec.mgx.client.access.rest;

import de.cebitec.mgx.client.MGXDTOMaster;
import de.cebitec.mgx.client.datatransfer.ReferenceUploader;
import de.cebitec.mgx.client.datatransfer.TransferBase;
import de.cebitec.mgx.client.exception.MGXDTOException;
import de.cebitec.mgx.client.mgxtestclient.TestMaster;
import de.cebitec.mgx.dto.dto.ReferenceDTO;
import de.cebitec.mgx.dto.dto.ReferenceRegionDTO;
import de.cebitec.mgx.dto.dto.TaskDTO;
import de.cebitec.mgx.dto.dto.TaskDTO.TaskState;
import java.io.File;
import java.util.Iterator;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.Test;

/**
 *
 * @author sjaenick
 */
//@RunWith(PaxExam.class)
public class ReferenceAccessTest {

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
    public void testListGlobal() {
        System.out.println("testListGlobal");
        MGXDTOMaster master = TestMaster.getRO();
        Iterator<ReferenceDTO> iter = null;
        try {
            iter = master.Reference().listGlobalReferences();
        } catch (MGXDTOException ex) {
            fail(ex.getMessage());
        }
        assertNotNull(iter);
        int refCnt = 0;
        while (iter.hasNext()) {
            ReferenceDTO ref = iter.next();
            System.err.println(ref.getName());
            refCnt++;
        }
        assertEquals(1663, refCnt);
    }

    @Test
    public void testInterval() {
        System.out.println("testInterval");
        MGXDTOMaster master = TestMaster.getRO();
        Iterator<ReferenceRegionDTO> iter = null;
        try {
            iter = master.ReferenceRegion().byReferenceInterval(65, 0, 99999);
        } catch (MGXDTOException ex) {
            fail(ex.getMessage());
        }
        assertNotNull(iter);
        int cnt = 0;
        while (iter.hasNext()) {
            ReferenceRegionDTO region = iter.next();
            assertEquals("CDS", region.getType().name());
            cnt++;
        }
        assertEquals(108, cnt);
    }

    @Test
    public void testFetchall() {
        System.out.println("testFetchall");
        MGXDTOMaster master = TestMaster.getRO();
        Iterator<ReferenceDTO> iter = null;
        try {
            iter = master.Reference().fetchall();
        } catch (MGXDTOException ex) {
            fail(ex.getMessage());
        }
        assertNotNull(iter);
        int refCnt = 0;
        while (iter.hasNext()) {
            iter.next();
            refCnt++;
        }
        assertEquals(2, refCnt);
    }

    @Test
    public void testCreate() {
        try {
            System.out.println("testCreate");
            MGXDTOMaster m = TestMaster.getRW();
            ReferenceDTO ref = ReferenceDTO.newBuilder()
                    .setName("testref")
                    .setLength(42)
                    .build();
            long refId;
            refId = m.Reference().create(ref);
            assertNotEquals(-1, refId);

            m.Reference().delete(refId);
        } catch (MGXDTOException ex) {
            fail(ex.getMessage());
        }
    }

    @Test
    public void testInstallGlobal() {
        System.out.println("testInstallGlobal");
        MGXDTOMaster m = TestMaster.getRW();

        String refName = null;
        TaskDTO task;
        try {

            boolean found = false;
            Iterator<ReferenceDTO> iterGlobal = m.Reference().listGlobalReferences();
            while (iterGlobal.hasNext()) {
                ReferenceDTO ref = iterGlobal.next();
                if (ref.getId() == 3) {
                    found = true;
                    refName = ref.getName();
                    break;
                }
            }
            assertTrue(found, "reference id 3 was not present in the global repository");

            UUID taskId = m.Reference().installGlobalReference(3);

            task = m.Task().get(taskId);
            assertNotNull(task);
            while ((task.getState() != TaskState.FINISHED) || (task.getState() != TaskState.FAILED)) {
                System.err.println(" --> " + task.getState());
                Thread.sleep(1000);
                if ((task.getState() == TaskState.FINISHED) || (task.getState() == TaskState.FAILED)) {
                    break;
                } else {
                    task = m.Task().get(taskId);
                }
            }
        } catch (MGXDTOException | InterruptedException ex) {
            fail(ex.getMessage());
        }

        long projRefId = -1;
        ReferenceDTO projReference = null;
        boolean installSuccess = false;
        try {
            Iterator<ReferenceDTO> iterProj = m.Reference().fetchall();
            while (iterProj.hasNext()) {
                ReferenceDTO ref = iterProj.next();
                if (ref.getName().equals(refName)) {
                    installSuccess = true;
                    projRefId = ref.getId();
                    projReference = ref;
                }
            }
            assertTrue(installSuccess);
        } catch (MGXDTOException ex) {
            fail(ex.getMessage());
        }

        String seqData = null;
        try {
            seqData = m.Reference().getSequence(projRefId, 0, 9);
        } catch (MGXDTOException ex) {
            fail(ex.getMessage());
        }

        assertNotNull(seqData);
        assertNotNull(projReference);

        int numSubregions = 0;
        try {
            Iterator<ReferenceRegionDTO> regionIter = m.ReferenceRegion().byReferenceInterval(projRefId, 0, projReference.getLength() - 1);
            while (regionIter != null && regionIter.hasNext()) {
                ReferenceRegionDTO reg = regionIter.next();
                numSubregions++;
            }
        } catch (MGXDTOException ex) {
            Logger.getLogger(ReferenceAccessTest.class.getName()).log(Level.SEVERE, null, ex);
        }

        // delete it again
        try {
            UUID uuid = m.Reference().delete(projRefId);
            assertNotNull(uuid);
            TaskState state = m.Task().get(uuid).getState();
            while (!state.equals(TaskState.FINISHED)) {
                state = m.Task().get(uuid).getState();
                if (state.equals(TaskState.FAILED)) {
                    fail();
                }
            }
        } catch (MGXDTOException ex) {
            fail(ex.getMessage());
        }

        assertEquals(553, numSubregions);
    }

    @Test
    public void testUploadGBK() {
        System.out.println("testUploadGBK");
        MGXDTOMaster m = TestMaster.getRW();

        try {
            Iterator<ReferenceDTO> it = m.Reference().fetchall();
            while (it.hasNext()) {
                ReferenceDTO ref = it.next();
                if (ref.getName().equals("Acetobacter pasteurianus IFO 3283-01-42C plasmid pAPA42C_040")) {
                    System.err.println("Test reference already contained in project, deleting..");
                    UUID taskId = m.Reference().delete(ref.getId());
                    TaskDTO task = m.Task().get(taskId);
                    while ((task.getState() != TaskState.FINISHED) || (task.getState() != TaskState.FAILED)) {
                        System.err.println(" --> " + task.getState());
                        Thread.sleep(1000);
                        if ((task.getState() == TaskState.FINISHED) || (task.getState() == TaskState.FAILED)) {
                            break;
                        } else {
                            task = m.Task().get(taskId);
                        }
                    }
                }
            }
        } catch (MGXDTOException | InterruptedException ex) {
            fail(ex.getMessage());
        }

        File f = new File("src/test/resources/NC_017106.gbk");
        if (!f.exists()) {
            fail("Cannot read test input");
        }
        ReferenceUploader up = null;
        try {
            up = m.Reference().createUploader(f);
        } catch (MGXDTOException ex) {
            fail(ex.getMessage());
        }
        assertNotNull(up);

        PropCounter pc = new PropCounter();
        up.addPropertyChangeListener(pc);
        boolean success = up.upload();
        if (!success) {
            fail(up.getErrorMessage());
        }

        assertEquals(TransferBase.MESSAGE, pc.getLastEvent().getPropertyName());
        assertEquals("Imported TEST", pc.getLastEvent().getNewValue());

        assertEquals(1, up.getReferenceIDs().size());
        long refId = up.getReferenceIDs().get(0);

        try {
            Iterator<ReferenceRegionDTO> iter = m.ReferenceRegion().byReferenceInterval(refId, 0, 3203);
            assertNotNull(iter);
            int cnt = 0;
            while (iter.hasNext()) {
                iter.next();
                cnt++;
            }
            assertEquals(3, cnt);
        } catch (MGXDTOException ex) {
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
        } catch (MGXDTOException ex) {
            fail(ex.getMessage());
        }

        // make sure they are all gone..
        Iterator<ReferenceDTO> iter = null;
        try {
            iter = m.Reference().fetchall();
        } catch (MGXDTOException ex) {
            fail(ex.getMessage());
        }
        assertNotNull(iter);
        int refCnt = 0;
        while (iter.hasNext()) {
            iter.next();
            refCnt++;
        }
        assertEquals(2, refCnt);
    }

    @Test
    public void testGetSequence() {
        System.out.println("testGetSequence");
        MGXDTOMaster master = TestMaster.getRO();
        String seq = null;
        try {
            // ref ID 68 is Streptomyces violaceusniger Tu 4113 chromosome
            seq = master.Reference().getSequence(68, 0, 9);
        } catch (MGXDTOException ex) {
            fail(ex.getMessage());
        }
        assertNotNull(seq);
        assertEquals("TTGTGCACAC", seq);
    }

//    @Test
//    public void testUploadGBKRegression() {
//        System.out.println("testUploadGBKRegression");
//        MGXDTOMaster m = TestMaster.getPrivate("MGX_Mammoth");
//
//        File f = new File("/vol/biodb/ncbi_genomes/Bacteria/Variovorax_paradoxus_B4_uid218005/NC_022234.gbk");
//        if (!f.exists()) {
//            fail();
//        }
//        ReferenceUploader up = m.Reference().createUploader(f);
//        assertNotNull(up);
//        boolean success = up.upload();
//        if (!success) {
//            fail(up.getErrorMessage());
//        }
//
//        assertEquals(1, up.getReferenceIDs().size());
//        long refId = up.getReferenceIDs().get(0);
//
//        // delete it again
//        try {
//            UUID uuid = m.Reference().delete(refId);
//            assertNotNull(uuid);
//            TaskState state = m.Task().get(uuid).getState();
//            while (!state.equals(TaskState.FINISHED)) {
//                state = m.Task().get(uuid).getState();
//                if (state.equals(TaskState.FAILED)) {
//                    fail();
//                }
//            }
//        } catch (MGXDTOException ex) {
//            fail(ex.getMessage());
//        }
//        
//    }
//    @Test
//    public void testUploadGBKRegression() {
//        System.out.println("testUploadGBKRegression");
//        MGXDTOMaster m = TestMaster.getRW();
//
//        try {
//            Iterator<ReferenceDTO> it = m.Reference().fetchall();
//            while (it.hasNext()) {
//                ReferenceDTO ref = it.next();
//                if (ref.getName().contains("Flavobacterium johnsoniae")) {
//                    System.err.println("Test reference already contained in project, deleting..");
//                    UUID taskId = m.Reference().delete(ref.getId());
//                    TaskDTO task = m.Task().get(taskId);
//                    while ((task.getState() != TaskState.FINISHED) || (task.getState() != TaskState.FAILED)) {
//                        System.err.println(" --> " + task.getState());
//                        Thread.sleep(1000);
//                        if ((task.getState() == TaskState.FINISHED) || (task.getState() == TaskState.FAILED)) {
//                            break;
//                        } else {
//                            task = m.Task().get(taskId);
//                        }
//                    }
//                }
//            }
//        } catch (MGXDTOException | InterruptedException ex) {
//            fail(ex.getMessage());
//        }
//
//        File f = new File("/vol/biodb/ncbi_genomes/Bacteria/Flavobacterium_johnsoniae_UW101_uid58493/NC_009441.gbk");
//        if (!f.exists()) {
//            fail("Missing input file");
//        }
//        ReferenceUploader up = m.Reference().createUploader(f);
//        assertNotNull(up);
//        boolean success = up.upload();
//        if (!success) {
//            fail(up.getErrorMessage());
//        }
//
//        assertEquals(1, up.getReferenceIDs().size());
//        long refId = up.getReferenceIDs().get(0);
//
//        try {
//            Iterator<RegionDTO> iter = m.Reference().byReferenceInterval(refId, 0, 3203);
//            assertNotNull(iter);
//            int cnt = 0;
//            while (iter.hasNext()) {
//                iter.next();
//                cnt++;
//            }
//            assertEquals(5, cnt);
//        } catch (MGXDTOException ex) {
//            fail(ex.getMessage());
//        }
//
//        // delete it again
//        try {
//            UUID uuid = m.Reference().delete(refId);
//            assertNotNull(uuid);
//            TaskState state = m.Task().get(uuid).getState();
//            while (!state.equals(TaskState.FINISHED)) {
//                state = m.Task().get(uuid).getState();
//                if (state.equals(TaskState.FAILED)) {
//                    fail();
//                }
//            }
//        } catch (MGXDTOException ex) {
//            fail(ex.getMessage());
//        }
//
//        // make sure they are all gone..
//        Iterator<ReferenceDTO> iter = null;
//        try {
//            iter = m.Reference().fetchall();
//        } catch (MGXDTOException ex) {
//            fail(ex.getMessage());
//        }
//        assertNotNull(iter);
//        int refCnt = 0;
//        while (iter.hasNext()) {
//            iter.next();
//            refCnt++;
//        }
//        assertEquals(2, refCnt);
//    }
}
