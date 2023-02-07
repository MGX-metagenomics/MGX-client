/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.client.access.rest;

import de.cebitec.mgx.client.MGXDTOMaster;
import de.cebitec.mgx.client.exception.MGXDTOException;
import de.cebitec.mgx.client.mgxtestclient.TestMaster;
import de.cebitec.mgx.dto.dto.HabitatDTO;
import java.util.Iterator;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author sj
 */
//@RunWith(PaxExam.class)
public class HabitatAccessTest {

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
     * Test of fetchall method, of class HabitatAccess.
     */
    @Test
    public void testFetchall() throws Exception {
        System.out.println("fetchall");
        MGXDTOMaster master = TestMaster.getRO();
        Iterator<HabitatDTO> iter = master.Habitat().fetchall();
        assertNotNull(iter);
        int cnt = 0;
        while (iter.hasNext()) {
            HabitatDTO hab = iter.next();
            assertNotNull(hab);
            cnt++;
        }
        assertEquals(2, cnt);
    }

    @Test
    public void testFetchValid() throws Exception {
        System.out.println("testFetchValid");
        MGXDTOMaster master = TestMaster.getRO();
        HabitatDTO habitat = master.Habitat().fetch(1);
        assertNotNull(habitat);
        assertEquals("unknown", habitat.getBiome());
    }

    @Test
    public void testFetchInvalid() {
        System.out.println("testFetchInvalid");
        MGXDTOMaster master = TestMaster.getRO();
        HabitatDTO habitat = null;
        try {
            habitat = master.Habitat().fetch(100);
        } catch (MGXDTOException ex) {
            assertEquals("No object of type Habitat for ID 100.", ex.getMessage());
        }
        assertNull(habitat);
    }

    @Test
    public void testDeleteInvalid() throws Exception {
        System.out.println("testDeleteInvalid");
        MGXDTOMaster master = TestMaster.getRW();
        try {
            master.Habitat().delete(100);
        } catch (MGXDTOException ex) {
            if (ex.getMessage().contains("No object of type Habitat for ID 100")) {
                return;
            }
            fail(ex.getMessage());
        }
        fail("deleting a non-existing habitat should produce an error");
    }
}
