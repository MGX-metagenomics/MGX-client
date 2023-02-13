package de.cebitec.mgx.client.access.rest;

import de.cebitec.mgx.client.MGXDTOMaster;
import de.cebitec.mgx.client.datatransfer.SeqDownloader;
import de.cebitec.mgx.client.datatransfer.SeqUploader;
import de.cebitec.mgx.client.datatransfer.TransferBase;
import de.cebitec.mgx.client.exception.MGXDTOException;
import de.cebitec.mgx.client.mgxtestclient.TestMaster;
import de.cebitec.mgx.dto.dto.DataRowDTO;
import de.cebitec.mgx.dto.dto.JobAndAttributeTypes;
import de.cebitec.mgx.dto.dto.JobDTO;
import de.cebitec.mgx.dto.dto.JobParameterDTO;
import de.cebitec.mgx.dto.dto.JobParameterListDTO;
import de.cebitec.mgx.dto.dto.QCResultDTO;
import de.cebitec.mgx.dto.dto.SeqRunDTO;
import de.cebitec.mgx.dto.dto.TaskDTO.TaskState;
import de.cebitec.mgx.seqstorage.FastaReader;
import de.cebitec.mgx.seqstorage.FastaWriter;
import de.cebitec.mgx.sequence.DNASequenceI;
import de.cebitec.mgx.sequence.SeqReaderFactory;
import de.cebitec.mgx.sequence.SeqReaderI;
import de.cebitec.mgx.sequence.SeqStoreException;
import de.cebitec.mgx.sequence.SeqWriterI;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import org.junit.jupiter.api.Test;

/**
 *
 * @author sjaenick
 */
//@RunWith(PaxExam.class)
public class SeqRunAccessTest {

//    @Configuration
//    public static Option[] configuration() {
//        return options(
//                junitBundles(),
//                MGXOptions.clientBundles(),
//                systemProperty("org.ops4j.pax.logging.DefaultServiceLog.level").value("WARN"),
//                bundle("reference:file:target/classes")
//        );
//    }
    @AfterAll
    public void tearDown() {
        MGXDTOMaster m = TestMaster.getRW();
        Iterator<SeqRunDTO> iter = null;
        try {
            iter = m.SeqRun().fetchall();
            assertNotNull(iter);
            while (iter.hasNext()) {
                SeqRunDTO sr = iter.next();
                if ("Unittest-Run".equals(sr.getName())) {
                    UUID taskId = m.SeqRun().delete(sr.getId());
                    TaskState ts = m.Task().get(taskId).getState();
                    while (ts != TaskState.FINISHED) {
                        Thread.sleep(500);
                        ts = m.Task().get(taskId).getState();
                    }
                }
            }
        } catch (MGXDTOException | InterruptedException ex) {
        }
    }

    @Test
    public void testFetchall() {
        System.out.println("testFetchall");
        Iterator<SeqRunDTO> iter = null;
        MGXDTOMaster master = TestMaster.getRO();
        try {
            iter = master.SeqRun().fetchall();
        } catch (MGXDTOException ex) {
            fail(ex.getMessage());
        }
        assertNotNull(iter);
        int refCnt = 0;
        while (iter.hasNext()) {
            SeqRunDTO run = iter.next();
            System.err.println(" [" + run.getId() + "] " + run.getName());
            refCnt++;
        }
        assertEquals(4, refCnt);

    }

    @Test
    public void testFetchId() {
        System.out.println("testById");
        MGXDTOMaster master = TestMaster.getRO();
        SeqRunDTO dto = null;
        try {
            dto = master.SeqRun().fetch(3);
        } catch (MGXDTOException ex) {
            fail(ex.getMessage());
        }
        assertNotNull(dto);
        assertEquals("oneseq", dto.getName());
        assertEquals(1, dto.getNumSequences());
    }

    @Test
    public void testByJob() {
        System.out.println("testByJob");
        MGXDTOMaster master = TestMaster.getRO();
        Iterator<SeqRunDTO> iter = null;
        try {
            iter = master.SeqRun().byJob(7);
        } catch (MGXDTOException ex) {
            fail(ex.getMessage());
        }
        assertNotNull(iter);
        boolean success = false;
        while (iter.hasNext()) {
            SeqRunDTO dto = iter.next();
            if (dto.getId() == 2 && dto.getName().equals("dataset2")) {
                success = true;
            }
        }
        assertTrue(success);
    }

    @Test
    public void testJobsAndAttributeTypes() {
        System.out.println("testJobsAndAttributeTypes");
        MGXDTOMaster master = TestMaster.getRO();
        List<JobAndAttributeTypes> jat = null;
        try {
            jat = master.SeqRun().getJobsAndAttributeTypes(1);
        } catch (MGXDTOException ex) {
            fail(ex.getMessage());
        }
        assertNotNull(jat);
        assertEquals(10, jat.size());

        int paramCnt = 0;

        for (JobAndAttributeTypes jaa : jat) {
            JobDTO job = jaa.getJob();
            assertTrue(job.hasParameters()); //non-null, but maybe empty list
            paramCnt += job.getParameters().getParameterCount();

            JobParameterListDTO parameters = job.getParameters();
            for (JobParameterDTO jp : parameters.getParameterList()) {
                assertNotNull(jp.getType());
            }
        }
        assertEquals(7, paramCnt);
    }

    @Test
    public void testDownload() throws IOException {
        System.out.println("testDownload");
        File tmpFile = File.createTempFile("down", "xx");
        try {
            final SeqWriterI<DNASequenceI> writer = new FastaWriter(tmpFile.getAbsolutePath());

            MGXDTOMaster master = TestMaster.getRO();

            SeqRunDTO sr1 = master.SeqRun().fetch(1);
            PropCounter pc = new PropCounter();
            final SeqDownloader downloader = master.Sequence().createDownloader(sr1.getId(), writer, true);
            downloader.addPropertyChangeListener(pc);
            boolean success = downloader.download();

            assertTrue(success);
            assertNotNull(pc.getLastEvent());
            assertEquals(TransferBase.TRANSFER_COMPLETED, pc.getLastEvent().getPropertyName());

            long cnt = 0;
            SeqReaderI reader = new FastaReader(tmpFile.getAbsolutePath(), false);
            while (reader.hasMoreElements()) {
                reader.nextElement();
                cnt++;
            }
            reader.close();
            assertEquals(sr1.getNumSequences(), cnt);
        } catch (Exception ex) {
            fail(ex.getMessage());
        } finally {
            tmpFile.delete();
        }
    }

    @Test
    public void testUploadFail() throws IOException {
        // upload as guest should fail
        System.out.println("testUploadFail");
        File tmpFile = File.createTempFile("down", "xx");

        try {
            FileWriter fw = new FileWriter(tmpFile);
            fw.write(">seq1\nAAAAAAAA\n");
            fw.close();

            SeqReaderI<? extends DNASequenceI> reader = SeqReaderFactory.<DNASequenceI>getReader(tmpFile.getAbsolutePath());
            assertNotNull(reader);

            MGXDTOMaster master = TestMaster.getRO();

            PropCounter pc = new PropCounter();
            SeqUploader up = null;
            try {
                up = master.Sequence().createUploader(999999, false, reader);
            } catch (MGXDTOException ex) {
                fail(ex.getMessage());
            }
            assertNotNull(up);
            up.addPropertyChangeListener(pc);
            boolean success = up.upload();

            tmpFile.delete();

            assertFalse(success);
            assertTrue(up.getErrorMessage().contains("access denied"));
            assertNotNull(pc.getLastEvent());
            assertEquals(TransferBase.TRANSFER_FAILED, pc.getLastEvent().getPropertyName());
        } catch (IOException | SeqStoreException ex) {
            fail(ex.getMessage());
        } finally {
            tmpFile.delete();
        }
    }

    @Test
    public void testUploadInvalidSeqRun() throws Exception {
        // upload with invalid seqrun id should fail
        System.out.println("testUploadInvalidSeqRun");
        File tmpFile = File.createTempFile("down", "xx");
        FileWriter fw = new FileWriter(tmpFile);
        fw.write(">seq1\nAAAAAAAA\n");
        fw.close();

        SeqReaderI<? extends DNASequenceI> reader = SeqReaderFactory.<DNASequenceI>getReader(tmpFile.getAbsolutePath());
        assertNotNull(reader);

        PropCounter pc = new PropCounter();
        MGXDTOMaster m = TestMaster.getRW();

        SeqUploader up = m.Sequence().createUploader(999999, false, reader);
        up.addPropertyChangeListener(pc);
        boolean success = up.upload();

        tmpFile.delete();

        assertFalse(success);
        assertNotNull(up.getErrorMessage());
        assertEquals("No object of type SeqRun for ID 999999.", up.getErrorMessage());
        assertNotNull(pc.getLastEvent());
        assertEquals(TransferBase.TRANSFER_FAILED, pc.getLastEvent().getPropertyName());
    }

    @Test
    public void testUploadSeqRun() throws Exception {
        System.out.println("testUploadSeqRun");
        MGXDTOMaster m = TestMaster.getRW();

        SeqRunDTO sr = SeqRunDTO.newBuilder()
                .setExtractId(1)
                .setName("Unittest-Run")
                .setSequencingMethod(m.Term().fetch(12))
                .setSequencingTechnology(m.Term().fetch(1))
                .setSubmittedToInsdc(false)
                .build();

        long run_id = m.SeqRun().create(sr);
        assertNotEquals(-1, run_id);

        File tmpFile = File.createTempFile("down", "xx");
        FileWriter fw = new FileWriter(tmpFile);
        fw.write(">seq1\nAAAAAAAA\n");
        fw.close();

        SeqReaderI<? extends DNASequenceI> reader = SeqReaderFactory.<DNASequenceI>getReader(tmpFile.getAbsolutePath());
        assertNotNull(reader);

        PropCounter pc = new PropCounter();

        SeqUploader up = m.Sequence().createUploader(run_id, false, reader);
        up.addPropertyChangeListener(pc);
        boolean success = up.upload();

        tmpFile.delete();

        assertTrue(success, up.getErrorMessage());
        assertNotNull(pc.getLastEvent());
        assertEquals(TransferBase.TRANSFER_COMPLETED, pc.getLastEvent().getPropertyName());

        UUID taskId = m.SeqRun().delete(run_id);
        TaskState ts = m.Task().get(taskId).getState();
        while (ts != TaskState.FINISHED) {
            Thread.sleep(500);
            ts = m.Task().get(taskId).getState();
        }
        assertEquals(ts, TaskState.FINISHED);

    }

    @Test
    public void testQC() throws MGXDTOException {
        System.out.println("testQC");
        MGXDTOMaster master = TestMaster.getRO();
        SeqRunDTO dto = null;
        try {
            dto = master.SeqRun().fetch(1);
        } catch (MGXDTOException ex) {
            fail(ex.getMessage());
        }
        assertNotNull(dto);
        List<QCResultDTO> qc = master.SeqRun().getQC(dto.getId());
        assumeTrue(qc.size() == 3);
        QCResultDTO gc = null;
        for (QCResultDTO q : qc) {
            if (q.getName().equals("GC")) {
                gc = q;
                break;
            }
        }
        assertNotNull(gc);
        assertEquals(1, gc.getRowCount());
        DataRowDTO row = gc.getRow(0);
        assertNotNull(row);
        List<Float> values = row.getValueList();
        assertEquals(101, values.size());
        for (Float f : values) {
            assertNotEquals(Float.NaN, f);
        }
    }

    @Test
    public void testRegressionEmptySeqRun() throws Exception {
        System.out.println("testRegressionEmptySeqRun");
        MGXDTOMaster m = TestMaster.getRW();

        SeqRunDTO sr = SeqRunDTO.newBuilder()
                .setExtractId(1)
                .setName("Unittest-Run")
                .setSequencingMethod(m.Term().fetch(12))
                .setSequencingTechnology(m.Term().fetch(1))
                .setSubmittedToInsdc(false)
                .build();

        long run_id = m.SeqRun().create(sr);
        assertNotEquals(-1, run_id);

        List<QCResultDTO> qc = null;

        try {
            qc = m.SeqRun().getQC(run_id);
        } catch (MGXDTOException ex) {
            fail(ex.getMessage());
        } finally {
            UUID taskId = m.SeqRun().delete(run_id);
            TaskState ts = m.Task().get(taskId).getState();
            while (ts != TaskState.FINISHED) {
                Thread.sleep(500);
                ts = m.Task().get(taskId).getState();
            }
            assertEquals(ts, TaskState.FINISHED);
        }

        assertNotNull(qc);
        assertEquals(0, qc.size(), "SeqRun without sequences should have returned zero QC reports");
    }

    @Test
    public void testDeleteInvalid() {
        System.out.println("testDeleteInvalid");
        MGXDTOMaster master = TestMaster.getRW();
        try {
            master.SeqRun().delete(100);
        } catch (MGXDTOException ex) {
            if (ex.getMessage().contains("No object of type SeqRun for ID 100")) {
                return;
            }
            fail(ex.getMessage());
        }
        fail("deleting a non-existing seqrun should produce an error");
    }

    @Test
    public void testHasQuality() {
        System.out.println("testHasQuality");
        MGXDTOMaster master = TestMaster.getRO();
        boolean hasQ = true;
        try {
            hasQ = master.SeqRun().hasQuality(3);
        } catch (MGXDTOException ex) {
            fail(ex.getMessage());
        }
        assertEquals(false, hasQ);
    }

    @Test
    public void testWrongSeqRunHasQuality() {
        System.out.println("testWrongSeqRunHasQuality");
        MGXDTOMaster master = TestMaster.getRO();
        try {
            master.SeqRun().hasQuality(42);
            fail("Got reply for nonexisting seqrun");
        } catch (MGXDTOException ex) {
            if (!ex.getMessage().contains("No object of type SeqRun")) {
                fail(ex.getMessage());
            }
        }
    }
}
