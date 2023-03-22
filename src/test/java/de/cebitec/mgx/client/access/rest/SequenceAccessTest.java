package de.cebitec.mgx.client.access.rest;

import de.cebitec.mgx.client.MGXDTOMaster;
import de.cebitec.mgx.client.datatransfer.SeqByAttributeDownloader;
import de.cebitec.mgx.client.datatransfer.SeqUploader;
import de.cebitec.mgx.client.exception.MGXDTOException;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.client.mgxtestclient.TestMaster;
import de.cebitec.mgx.dto.dto.AttributeDTO;
import de.cebitec.mgx.dto.dto.AttributeDTOList;
import de.cebitec.mgx.dto.dto.SequenceDTO;
import de.cebitec.mgx.dto.dto.SequenceDTOList;
import de.cebitec.mgx.seqcompression.FourBitEncoder;
import de.cebitec.mgx.sequence.DNASequenceI;
import de.cebitec.mgx.sequence.SeqReaderFactory;
import de.cebitec.mgx.sequence.SeqReaderI;
import de.cebitec.mgx.sequence.SeqStoreException;
import de.cebitec.mgx.sequence.SeqWriterI;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.Test;

/**
 *
 * @author sjaenick
 */
//@RunWith(PaxExam.class)
public class SequenceAccessTest {

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
    public void testCreateUploaderForInvalidID() {
        System.out.println("createUploaderForInvalidID");
        MGXDTOMaster master = TestMaster.getRW();
        SeqReaderI<? extends DNASequenceI> reader = null;
        try {
            reader = SeqReaderFactory.<DNASequenceI>getReader("src/test/resources/sample.fas");
        } catch (SeqStoreException ex) {
            fail(ex.getMessage());
        }
        assertNotNull(reader);
        SeqUploader up = null;
        try {
            up = master.Sequence().createUploader(9999, false, reader);
        } catch (MGXDTOException ex) {
            fail(ex.getMessage());
        }
        assertNotNull(up);
        long numElem = up.getProgress();
        assertEquals(0, numElem);

        boolean success = up.upload();
        assertFalse(success);

        numElem = up.getProgress();
        assertEquals(0, numElem);
    }

    @Test
    public void testFetch() throws Exception {
        System.out.println("fetch");
        MGXDTOMaster master = TestMaster.getRW();
        SequenceDTO result = master.Sequence().fetch(4219806);
        assertNotNull(result);
        assertEquals("seq1", result.getName());
        assertEquals(23, result.getLength());
        byte[] decoded = FourBitEncoder.decode(result.getSequence().toByteArray());
        assertEquals(23, new String(decoded).length());
        assertEquals("AAATTTATATATAAAACTCTCTC", new String(decoded));
    }

    @Test
    public void testFetchInvalid() {
        System.out.println("fetchInvalid");
        MGXDTOMaster master = TestMaster.getRW();
        try {
            SequenceDTO result = master.Sequence().fetch(99999999);
        } catch (MGXServerException ex) {
            return; // ok
        } catch (MGXDTOException ex) {
            fail(ex.getMessage());
        }
        fail("Got data for invalid sequence ID");
    }

    @Test
    public void testFetchInvalidName() {
        System.out.println("testFetchInvalidName");
        MGXDTOMaster master = TestMaster.getRW();
        try {
            SequenceDTO result = master.Sequence().byName(1, "doesNotExist");
        } catch (MGXServerException ex) {
            assertNotNull(ex.getMessage());
            assertTrue(ex.getMessage().contains(" not found"));
            return; // ok
        } catch (MGXDTOException ex) {
            fail(ex.getMessage());
        }
        fail("Got data for invalid sequence name");
    }

    @Test
    public void testFetchValidName() {
        System.out.println("testFetchValidName");
        MGXDTOMaster master = TestMaster.getRW();
        SequenceDTO result = null;
        try {
            result = master.Sequence().byName(49, "FI5LW4G01EJ7FZ");
        } catch (MGXDTOException ex) {
            fail(ex.getMessage());
        }
        assertNotNull(result);
        assertEquals(2109927, result.getId());
    }

    @Test
    public void testFetchByIDs() {
        System.out.println("testFetchByIDs");
        MGXDTOMaster master = TestMaster.getRO();
        SequenceDTOList result = null;
        try {
            result = master.Sequence().fetchByIds(new long[]{2109905, 2109906});
        } catch (MGXDTOException ex) {
            fail(ex.getMessage());
        }
        assertNotNull(result);
        List<SequenceDTO> seqList = result.getSeqList();
        assertEquals(2, seqList.size());
        SequenceDTO seq1 = seqList.get(0);
        assertEquals("FI5LW4G01DZDXZ", seq1.getName());
        assertEquals(63, seq1.getLength());
        SequenceDTO seq2 = seqList.get(1);
        assertEquals("FI5LW4G01AM15A", seq2.getName());
        assertEquals(121, seq2.getLength());
    }

    @Test
    public void testDownloadSequencesForAttribute() throws MGXDTOException {
        System.out.println("testDownloadSequencesForAttribute");
        MGXDTOMaster master = TestMaster.getRO();

        // COG, COG2718 Uncharacterized conserved protein
        AttributeDTO attr = master.Attribute().fetch(14227);
        assertNotNull(attr);
        AttributeDTOList set = AttributeDTOList.newBuilder()
                .addAttribute(attr)
                .build();

        AtomicInteger cnt = new AtomicInteger(0);
        AtomicBoolean closed = new AtomicBoolean(false);
        closed.set(Boolean.FALSE);

        SeqWriterI<DNASequenceI> dummy = new SeqWriterI<DNASequenceI>() {
            @Override
            public void addSequence(DNASequenceI seq) {
                cnt.incrementAndGet();
            }

            @Override
            public void close() {
                closed.set(Boolean.TRUE);
            }
        };
        SeqByAttributeDownloader downloader = master.Sequence().createDownloaderByAttributes(set, dummy, true);
        boolean success = downloader.download();

        assertTrue(success);

        assertTrue(closed.get());
        assertEquals(5, cnt.get());
    }
}
