package de.cebitec.mgx.client.access.rest;

import de.cebitec.mgx.client.MGXDTOMaster;
import de.cebitec.mgx.client.datatransfer.SeqDownloader;
import de.cebitec.mgx.client.datatransfer.TransferBase;
import de.cebitec.mgx.client.exception.MGXClientException;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.client.mgxtestclient.TestMaster;
import de.cebitec.mgx.dto.dto.SeqRunDTO;
import de.cebitec.mgx.seqstorage.FastaWriter;
import de.cebitec.mgx.sequence.SeqWriterI;
import java.io.File;
import java.util.Iterator;
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
}
