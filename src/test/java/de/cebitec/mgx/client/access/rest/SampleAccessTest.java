/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.client.access.rest;

import de.cebitec.mgx.client.MGXDTOMaster;
import de.cebitec.mgx.client.exception.MGXDTOException;
import de.cebitec.mgx.client.mgxtestclient.TestMaster;
import de.cebitec.mgx.dto.dto.SampleDTO;
import java.util.Iterator;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.Test;

/**
 *
 * @author sj
 */
//@RunWith(PaxExam.class)
public class SampleAccessTest {

//    @Configuration
//    public static Option[] configuration() {
//        return options(
//                junitBundles(),
//                MGXOptions.clientBundles(),
//                systemProperty("org.ops4j.pax.logging.DefaultServiceLog.level").value("WARN"),
//                bundle("reference:file:target/classes")
//        );
//    }

    /**
     * Test of fetchall method, of class SampleAccess.
     */
    @Test
    public void testFetchall() throws Exception {
        System.out.println("fetchall");
        MGXDTOMaster master = TestMaster.getRO();
        Iterator<SampleDTO> iter = master.Sample().fetchall();
        assertNotNull(iter);
        int cnt = 0;
        while (iter.hasNext()) {
            SampleDTO sample = iter.next();
            assertNotNull(sample);
            cnt++;
        }
        assertEquals(1, cnt);
    }

    /**
     * Test of fetch method, of class SampleAccess.
     */
    @Test
    public void testFetchValid() throws Exception {
        System.out.println("testFetchValid");
        MGXDTOMaster master = TestMaster.getRO();
        SampleDTO s = master.Sample().fetch(1);
        assertNotNull(s);
        assertEquals(2, s.getHabitatId());
        assertEquals("unknown material", s.getMaterial());
    }

    @Test
    public void testFetchInvalid() {
        System.out.println("testFetchInvalid");
        MGXDTOMaster master = TestMaster.getRO();
        SampleDTO s = null;
        try {
            s = master.Sample().fetch(2);
        } catch (MGXDTOException ex) {
            assertEquals("No object of type Sample for ID 2.", ex.getMessage());
        }
        assertNull(s);
    }

    /**
     * Test of ByHabitat method, of class SampleAccess.
     */
    @Test
    public void testByHabitatWithSamples() throws Exception {
        System.out.println("ByHabitatWithSamples");
        MGXDTOMaster master = TestMaster.getRO();
        Iterator<SampleDTO> iter = master.Sample().byHabitat(2);
        assertNotNull(iter);
        int cnt = 0;
        while (iter.hasNext()) {
            SampleDTO s = iter.next();
            assertNotNull(s);
            cnt++;
        }
        assertEquals(1, cnt);
    }

    @Test
    public void testByHabitatWithoutSamples() throws Exception {
        System.out.println("testByHabitatWithoutSamples");
        MGXDTOMaster master = TestMaster.getRO();
        Iterator<SampleDTO> iter = master.Sample().byHabitat(1);
        assertNotNull(iter);
        int cnt = 0;
        while (iter.hasNext()) {
            SampleDTO s = iter.next();
            assertNotNull(s);
            cnt++;
        }
        assertEquals(0, cnt);
    }

    @Test
    public void testByInvalidHabitat() {
        System.out.println("testByInvalidHabitat");
        MGXDTOMaster master = TestMaster.getRO();
        try {
            master.Sample().byHabitat(3);
        } catch (MGXDTOException ex) {
            System.err.println(ex.getMessage());
            if (ex.getMessage().contains("No object of type Habitat for ID 3.")) {
                return;
            }
            fail(ex.getMessage());
        }
        fail("habitat with id 3 does not exist, an exception should have been thrown");
    }

//    /**
//     * Test of create method, of class SampleAccess.
//     */
//    @Test
//    public void testCreate() throws Exception {
//        System.out.println("create");
//        dto.SampleDTO s = null;
//        SampleAccess instance = null;
//        long expResult = 0L;
//        long result = instance.create(s);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of update method, of class SampleAccess.
//     */
//    @Test
//    public void testUpdate() throws Exception {
//        System.out.println("update");
//        SampleDTO d = null;
//        SampleAccess instance = null;
//        instance.update(d);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
    @Test
    public void testDeleteInvalid() throws Exception {
        System.out.println("testDeleteInvalid");
        MGXDTOMaster master = TestMaster.getRW();
        try {
            master.Sample().delete(100);
        } catch (MGXDTOException ex) {
            if (ex.getMessage().contains("No object of type Sample for ID 100")) {
                return;
            }
            fail(ex.getMessage());
        }
        fail("deleting a non-existing sample should produce an error");
    }

}
