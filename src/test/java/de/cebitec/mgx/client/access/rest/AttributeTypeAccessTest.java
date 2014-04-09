package de.cebitec.mgx.client.access.rest;

import de.cebitec.mgx.client.MGXDTOMaster;
import de.cebitec.mgx.client.mgxtestclient.TestMaster;
import de.cebitec.mgx.dto.dto.AttributeTypeDTO;
import java.util.Iterator;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author sj
 */
public class AttributeTypeAccessTest {

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
    public void testBySeqRun() throws Exception {
        System.out.println("BySeqRun");
        Iterator<AttributeTypeDTO> types = master.AttributeType().BySeqRun(1);
        assertNotNull(types);
        int cnt = 0;
        while (types.hasNext()) {
            AttributeTypeDTO next = types.next();
            assertNotNull(next);
            cnt++;
        }
        assertEquals(21, cnt);
    }

    @Test
    public void testFetch() throws Exception {
        System.out.println("fetch");
        AttributeTypeDTO atype = master.AttributeType().fetch(1);
        assertNotNull(atype);
    }

    @Test
    public void testFetchall() throws Exception {
        System.out.println("fetchall");
        Iterator<AttributeTypeDTO> types = master.AttributeType().fetchall();
        assertNotNull(types);
        int cnt = 0;
        while (types.hasNext()) {
            AttributeTypeDTO next = types.next();
            assertNotNull(next);
            cnt++;
        }
        assertEquals(21, cnt);
    }

    @Test
    public void testByJob() throws Exception {
        System.out.println("ByJob");
        Iterator<AttributeTypeDTO> types = master.AttributeType().ByJob(3);
        assertNotNull(types);
        int cnt = 0;
        while (types.hasNext()) {
            AttributeTypeDTO next = types.next();
            assertNotNull(next);
            cnt++;
        }
        assertEquals(7, cnt);
    }
}