/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.client.access.rest;

import de.cebitec.mgx.client.MGXDTOMaster;
import de.cebitec.mgx.client.mgxtestclient.TestMaster;
import de.cebitec.mgx.dto.dto;
import de.cebitec.mgx.dto.dto.ObservationDTO;
import java.util.Iterator;
import java.util.UUID;
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
public class ObservationAccessTest {
    
   
    @Test
    public void testByRead() throws Exception {
        System.out.println("ByRead");
        MGXDTOMaster master = TestMaster.getRO();
        Iterator<ObservationDTO> iter = master.Observation().ByRead(4255);
        assertNotNull(iter);
        int numObs = 0;
        while (iter.hasNext()) {
            ObservationDTO dto = iter.next();
            assertNotNull(dto);
            numObs++;
        }
        assertEquals(13, numObs);
    }
    
}
