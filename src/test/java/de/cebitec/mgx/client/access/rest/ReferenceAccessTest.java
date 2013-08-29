package de.cebitec.mgx.client.access.rest;

import de.cebitec.mgx.client.MGXDTOMaster;
import de.cebitec.mgx.client.exception.MGXClientException;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.client.mgxtestclient.TestMaster;
import de.cebitec.mgx.dto.dto.ReferenceDTO;
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
        assertEquals(0, refCnt);
    }
}
