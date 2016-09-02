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
import de.cebitec.mgx.osgiutils.MGXOptions;
import java.util.Iterator;
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
 * @author sj
 */
@RunWith(PaxExam.class)
public class HabitatAccessTest {

    @Configuration
    public static Option[] configuration() {
        return options(
                junitBundles(),
                MGXOptions.clientBundles(),
                systemProperty("org.ops4j.pax.logging.DefaultServiceLog.level").value("WARN"),
                bundle("reference:file:target/classes")
        );
    }

    /**
     * Test of fetchall method, of class HabitatAccess.
     */
    @Test
    public void testFetchall() throws Exception {
        System.out.println("fetchall");
        MGXDTOMaster master = TestMaster.getRO();
        Iterator<HabitatDTO> iter = master.Habitat().fetchall();
        assertNotNull(iter);
        assertTrue(iter.hasNext());
        HabitatDTO habitat = iter.next();
        assertNotNull(habitat);
        assertFalse(iter.hasNext());
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
            habitat = master.Habitat().fetch(2);
        } catch (MGXDTOException ex) {
            assertEquals("No object of type Habitat for ID 2.", ex.getMessage());
        }
        assertNull(habitat);
    }
}
