/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.client.access.rest;

import de.cebitec.mgx.client.MGXDTOMaster;
import de.cebitec.mgx.client.exception.MGXClientException;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.client.mgxtestclient.TestMaster;
import de.cebitec.mgx.dto.dto.PointDTO;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
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
public class StatisticsAccessTest {

    private MGXDTOMaster master;

    public StatisticsAccessTest() {
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
    public void testRarefaction() throws Exception {
        System.out.println("testRarefaction");
        Collection<Number> data = new LinkedList<>();
        data.add(1);
        data.add(2);
        data.add(3);
        data.add(3);
        data.add(4);
        data.add(5);
        Iterator<PointDTO> iter = master.Statistics().Rarefaction(data);
        assertNotNull(iter);

        int cnt = 0;
        while (iter.hasNext()) {
            PointDTO p = iter.next();
            cnt++;
        }
        assertEquals(5, cnt);
    }
}
