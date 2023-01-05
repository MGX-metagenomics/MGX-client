/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.client.access.rest;

import de.cebitec.mgx.client.MGXDTOMaster;
import de.cebitec.mgx.client.mgxtestclient.TestMaster;
import de.cebitec.mgx.dto.dto.ObservationDTO;
import java.util.Iterator;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;

/**
 *
 * @author sj
 */
public class ObservationAccessTest {
    
   
    @Test
    public void testByRead() throws Exception {
        System.out.println("ByRead");
        MGXDTOMaster master = TestMaster.getRO();
        Iterator<ObservationDTO> iter = master.Observation().byRead(4255);
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
