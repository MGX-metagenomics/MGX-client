package de.cebitec.mgx.client.access.rest;

import de.cebitec.mgx.client.MGXDTOMaster;
import de.cebitec.mgx.client.datatransfer.SeqDownloader;
import de.cebitec.mgx.client.datatransfer.SeqUploader;
import de.cebitec.mgx.client.datatransfer.TransferBase;
import de.cebitec.mgx.client.exception.MGXClientException;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.client.mgxtestclient.TestMaster;
import de.cebitec.mgx.dto.dto.SeqRunDTO;
import de.cebitec.mgx.dto.dto.TaskDTO.TaskState;
import de.cebitec.mgx.seqstorage.FastaWriter;
import de.cebitec.mgx.sequence.DNASequenceI;
import de.cebitec.mgx.sequence.SeqReaderFactory;
import de.cebitec.mgx.sequence.SeqReaderI;
import de.cebitec.mgx.sequence.SeqWriterI;
import java.io.File;
import java.io.FileWriter;
import java.util.Iterator;
import java.util.UUID;
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
public class SeqRunAccessTest {

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
        } catch (Exception ex) {
        }
    }

    @Test
    public void testFetchall() {
        System.out.println("testFetchall");
        Iterator<SeqRunDTO> iter = null;
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
    public void testDownload() throws Exception {
        System.out.println("testDownload");
        File tmpFile = File.createTempFile("down", "xx");
        final SeqWriterI writer = new FastaWriter(tmpFile.getAbsolutePath());

        SeqRunDTO sr1 = master.SeqRun().fetch(1);
        PropCounter pc = new PropCounter();
        final SeqDownloader downloader = master.Sequence().createDownloader(sr1.getId(), writer, true);
        downloader.addPropertyChangeListener(pc);
        boolean success = downloader.download();

        tmpFile.delete();

        assertTrue(success);
        assertNotNull(pc.getLastEvent());
        assertEquals(TransferBase.TRANSFER_COMPLETED, pc.getLastEvent().getPropertyName());
    }

    @Test
    public void testUploadFail() throws Exception {
        // upload as guest should fail
        System.out.println("testUploadFail");
        File tmpFile = File.createTempFile("down", "xx");
        FileWriter fw = new FileWriter(tmpFile);
        fw.write(">seq1\nAAAAAAAA\n");
        fw.close();

        SeqReaderI<DNASequenceI> reader = SeqReaderFactory.getReader(tmpFile.getAbsolutePath());
        assertNotNull(reader);

        PropCounter pc = new PropCounter();
        SeqUploader up = master.Sequence().createUploader(999999, reader);
        up.addPropertyChangeListener(pc);
        boolean success = up.upload();

        tmpFile.delete();

        assertFalse(success);
        assertTrue(up.getErrorMessage().contains("access denied"));
        assertNotNull(pc.getLastEvent());
        assertEquals(TransferBase.TRANSFER_FAILED, pc.getLastEvent().getPropertyName());
    }

    @Test
    public void testUploadInvalidSeqRun() throws Exception {
        // upload with invalid seqrun id should fail
        System.out.println("testUploadInvalidSeqRun");
        File tmpFile = File.createTempFile("down", "xx");
        FileWriter fw = new FileWriter(tmpFile);
        fw.write(">seq1\nAAAAAAAA\n");
        fw.close();

        SeqReaderI<DNASequenceI> reader = SeqReaderFactory.getReader(tmpFile.getAbsolutePath());
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

        SeqReaderI<DNASequenceI> reader = SeqReaderFactory.getReader(tmpFile.getAbsolutePath());
        assertNotNull(reader);

        PropCounter pc = new PropCounter();

        SeqUploader up = m.Sequence().createUploader(run_id, reader);
        up.addPropertyChangeListener(pc);
        boolean success = up.upload();

        tmpFile.delete();

        assertTrue(success);
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
}
