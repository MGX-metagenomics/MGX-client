package de.cebitec.mgx.client.access.rest;

import de.cebitec.mgx.client.MGXDTOMaster;
import de.cebitec.mgx.client.datatransfer.SeqDownloader;
import de.cebitec.mgx.client.datatransfer.SeqUploader;
import de.cebitec.mgx.client.datatransfer.TransferBase;
import de.cebitec.mgx.client.exception.MGXClientException;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.client.mgxtestclient.TestMaster;
import de.cebitec.mgx.dto.dto.DataRowDTO;
import de.cebitec.mgx.dto.dto.JobAndAttributeTypes;
import de.cebitec.mgx.dto.dto.JobDTO;
import de.cebitec.mgx.dto.dto.JobParameterDTO;
import de.cebitec.mgx.dto.dto.JobParameterListDTO;
import de.cebitec.mgx.dto.dto.QCResultDTO;
import de.cebitec.mgx.dto.dto.SeqRunDTO;
import de.cebitec.mgx.dto.dto.TaskDTO.TaskState;
import de.cebitec.mgx.osgiutils.MGXOptions;
import de.cebitec.mgx.seqstorage.FastaReader;
import de.cebitec.mgx.seqstorage.FastaWriter;
import de.cebitec.mgx.sequence.DNASequenceI;
import de.cebitec.mgx.sequence.SeqReaderFactory;
import de.cebitec.mgx.sequence.SeqReaderI;
import de.cebitec.mgx.sequence.SeqStoreException;
import de.cebitec.mgx.sequence.SeqWriterI;
import de.cebitec.mgx.testutils.PropCounter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Assume;
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
public class SeqRunAccessTest {

    @Configuration
    public static Option[] configuration() {
        return options(
                junitBundles(),
                MGXOptions.clientBundles(),
                systemProperty("org.ops4j.pax.logging.DefaultServiceLog.level").value("WARN"),
                bundle("reference:file:target/classes")
        );
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
        MGXDTOMaster m = TestMaster.getRW();
        Iterator<SeqRunDTO> iter = null;
        try {
            iter = m.SeqRun().fetchall();
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
        } catch (MGXServerException | MGXClientException | InterruptedException ex) {
        }
    }

    @Test
    public void testFetchall() {
        System.out.println("testFetchall");
        Iterator<SeqRunDTO> iter = null;
        MGXDTOMaster master = TestMaster.getRO();
        try {
            iter = master.SeqRun().fetchall();
        } catch (MGXServerException | MGXClientException ex) {
            fail(ex.getMessage());
        }
        assertNotNull(iter);
        int refCnt = 0;
        while (iter.hasNext()) {
            iter.next();
            refCnt++;
        }
        assertEquals(3, refCnt);
    }

    @Test
    public void testFetchId() {
        System.out.println("testById");
        MGXDTOMaster master = TestMaster.getRO();
        SeqRunDTO dto = null;
        try {
            dto = master.SeqRun().fetch(3);
        } catch (MGXServerException | MGXClientException ex) {
            fail(ex.getMessage());
        }
        assertNotNull(dto);
        assertEquals("oneseq", dto.getName());
        assertEquals(1, dto.getNumSequences());
    }

    @Test
    public void testJobsAndAttributeTypes() {
        System.out.println("testJobsAndAttributeTypes");
        MGXDTOMaster master = TestMaster.getRO();
        List<JobAndAttributeTypes> jat = null;
        try {
            jat = master.SeqRun().getJobsAndAttributeTypes(1);
        } catch (MGXServerException | MGXClientException ex) {
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
            SeqUploader up = master.Sequence().createUploader(999999, reader);
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

        SeqUploader up = m.Sequence().createUploader(999999, reader);
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

        SeqUploader up = m.Sequence().createUploader(run_id, reader);
        up.addPropertyChangeListener(pc);
        boolean success = up.upload();

        tmpFile.delete();

        assertTrue(up.getErrorMessage(), success);
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
    public void testQC() throws MGXServerException, MGXClientException {
        System.out.println("testQC");
        MGXDTOMaster master = TestMaster.getRO();
        SeqRunDTO dto = null;
        try {
            dto = master.SeqRun().fetch(1);
        } catch (MGXServerException | MGXClientException ex) {
            fail(ex.getMessage());
        }
        assertNotNull(dto);
        List<QCResultDTO> qc = master.SeqRun().getQC(dto.getId());
        Assume.assumeTrue(qc.size() == 3);
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
        } catch (MGXServerException | MGXClientException ex) {
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
        assertEquals("SeqRun without sequences should have returned zero QC reports", 0, qc.size());
    }
}
