package de.cebitec.mgx.client.access.rest;

import de.cebitec.mgx.client.MGXDTOMaster;
import de.cebitec.mgx.client.datatransfer.SeqUploader;
import de.cebitec.mgx.client.exception.MGXClientException;
import de.cebitec.mgx.client.exception.MGXDTOException;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.client.mgxtestclient.TestMaster;
import de.cebitec.mgx.dto.dto.SequenceDTO;
import de.cebitec.mgx.dto.dto.SequenceDTOList;
import de.cebitec.mgx.osgiutils.MGXOptions;
import de.cebitec.mgx.sequence.DNASequenceI;
import de.cebitec.mgx.sequence.SeqReaderFactory;
import de.cebitec.mgx.sequence.SeqReaderI;
import de.cebitec.mgx.sequence.SeqStoreException;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;
import org.ops4j.pax.exam.Configuration;
import static org.ops4j.pax.exam.CoreOptions.bundle;
import static org.ops4j.pax.exam.CoreOptions.junitBundles;
import static org.ops4j.pax.exam.CoreOptions.options;
import static org.ops4j.pax.exam.CoreOptions.systemProperty;
import org.ops4j.pax.exam.Option;

/**
 *
 * @author sjaenick
 */
//@RunWith(PaxExam.class)
public class SequenceAccessTest {

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
            up = master.Sequence().createUploader(9999, reader);
        } catch (MGXClientException ex) {
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
        SequenceDTO result = master.Sequence().fetch(109902);
        assertNotNull(result);
        assertEquals("seq1", result.getName());
        assertEquals(23, result.getLength());
        assertEquals(23, result.getSequence().length());
        assertEquals("AAATTTATATATAAAACTCTCTC", result.getSequence());
    }

    @Test
    public void testFetchInvalid() {
        System.out.println("fetchInvalid");
        MGXDTOMaster master = TestMaster.getRW();
        try {
            SequenceDTO result = master.Sequence().fetch(999999);
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
            assertTrue(ex.getMessage() != null && ex.getMessage().contains("Not found"));
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
            result = master.Sequence().byName(1, "FI5LW4G01EJ7FZ");
        } catch (MGXDTOException ex) {
            fail(ex.getMessage());
        }
        assertNotNull(result);
        assertEquals(23, result.getId());
    }

    @Test
    public void testFetchByIDs() {
        System.out.println("testFetchByIDs");
        MGXDTOMaster master = TestMaster.getRO();
        SequenceDTOList result = null;
        try {
            result = master.Sequence().fetchByIds(new long[]{1, 2});
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

//    @Test
//    public void testFetchByIDsListPerformance() {
//        System.out.println("testFetchByIDsListPerformance");
//        MGXDTOMaster master = TestMaster.getRO();
//        long[] ids = new long[59482];
//        for (int i = 0; i < 59482; i++) {
//            ids[i] = i + 1;
//        }
//
//        int from = 0;
//        int size = 50_000;
//        long[] chunk;
//
//        while (from + size < ids.length) {
//            chunk = Arrays.copyOfRange(ids, from, from+size);
//
//            System.err.println("fetching interval " + chunk[0] + "-" + chunk[chunk.length-1]);
//            SequenceDTOList result = null;
//            try {
//                result = master.Sequence().fetchByIds(chunk);
//            } catch (MGXDTOException ex) {
//                fail(ex.getMessage());
//            }
//            assertNotNull(result);
//            assertEquals(chunk.length, result.getSeqCount());
//            
//            from += size;
//        }
//
//    }
}
