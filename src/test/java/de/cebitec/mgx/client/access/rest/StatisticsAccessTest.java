/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.client.access.rest;

import de.cebitec.mgx.client.MGXDTOMaster;
import de.cebitec.mgx.client.mgxtestclient.TestMaster;
import de.cebitec.mgx.dto.dto.PointDTO;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
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
        master = TestMaster.getRO();
    }

    @After
    public void tearDown() {
    }

    @Test
    @SuppressWarnings("deprecation")
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

        List<PointDTO> ret = new LinkedList<>();
        while (iter.hasNext()) {
            PointDTO p = iter.next();
            ret.add(p);
        }
        assertEquals(5, ret.size());

        PointDTO p1 = ret.get(0);
        PointDTO p2 = ret.get(1);
        PointDTO p3 = ret.get(2);
        PointDTO p4 = ret.get(3);
        PointDTO p5 = ret.get(4);

        // check sample sizes
        assertEquals(0, p1.getX(), 0.0001);
        assertEquals(5, p2.getX(), 0.0001);
        assertEquals(10, p3.getX(), 0.0001);
        assertEquals(15, p4.getX(), 0.0001);
        assertEquals(18, p5.getX(), 0.0001);

        // check richness estimaes
        assertEquals(0, p1.getY(), 0.0001);  // always zero
        assertEquals(3.683123, p2.getY(), 0.0001);
        assertEquals(5.205882, p3.getY(), 0.0001);
        assertEquals(5.811275, p4.getY(), 0.0001);
        assertEquals(data.size(), p5.getY(), 0.0001);  // always number of categories
    }
}
