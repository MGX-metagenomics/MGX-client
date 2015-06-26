package de.cebitec.mgx.client.access.rest;

import de.cebitec.mgx.client.MGXDTOMaster;
import de.cebitec.mgx.client.datatransfer.SeqUploader;
import de.cebitec.mgx.client.exception.MGXClientException;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.client.mgxtestclient.TestMaster;
import de.cebitec.mgx.dto.dto.SequenceDTO;
import de.cebitec.mgx.osgiutils.MGXOptions;
import de.cebitec.mgx.sequence.DNASequenceI;
import de.cebitec.mgx.sequence.SeqReaderFactory;
import de.cebitec.mgx.sequence.SeqReaderI;
import de.cebitec.mgx.sequence.SeqStoreException;
import org.junit.Test;
import static org.junit.Assert.*;
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
        SeqUploader up = master.Sequence().createUploader(9999, reader);
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
        } catch (MGXClientException ex) {
            fail(ex.getMessage());
        }
        fail("Got data for invalid sequence ID");
    }
}
