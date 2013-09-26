/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.client.access.rest;

import de.cebitec.mgx.client.MGXDTOMaster;
import de.cebitec.mgx.client.mgxtestclient.TestMaster;
import de.cebitec.mgx.dto.dto.MappingDTO;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
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
public class MappingAccessTest {

    private MGXDTOMaster master;

    public MappingAccessTest() {
    }

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
    }

    @Test
    public void testFetchall() throws Exception {
        System.out.println("fetchall");
        Iterator<MappingDTO> it = master.Mapping().fetchall();
        assertNotNull(it);
        Set<MappingDTO> data = new HashSet<>();
        while (it.hasNext()) {
            data.add(it.next());
        }
        assertEquals(0, data.size());
    }

    @Test
    public void testBySeqRun() throws Exception {
        System.out.println("BySeqRun");
        Iterator<MappingDTO> it = master.Mapping().BySeqRun(1);
        assertNotNull(it);
        Set<MappingDTO> data = new HashSet<>();
        while (it.hasNext()) {
            data.add(it.next());
        }
        assertEquals(0, data.size());
    }
//
//    @Test
//    public void testByReference() throws Exception {
//        System.out.println("ByReference");
//        Iterator<MappingDTO> it = master.Mapping().ByReference(1);
//        assertNotNull(it);
//        Set<MappingDTO> data = new HashSet<>();
//        while (it.hasNext()) {
//            data.add(it.next());
//        }
//        assertEquals(0, data.size());
//    }
//
//    @Test
//    public void testFetch() throws Exception {
//        System.out.println("fetch");
//    }
}