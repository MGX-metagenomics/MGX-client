/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.client.access.rest.util;

import de.cebitec.mgx.client.exception.MGXClientException;
import de.cebitec.mgx.dto.dto.JobDTO;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author sj
 */
public class RESTPathResolverTest {

    public RESTPathResolverTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testResolver() throws MGXClientException {
        System.err.println("testVarArgs");
        RESTPathResolver rr = RESTPathResolver.getInstance();
        String[] res = rr.resolve(JobDTO.class, "fetch", "1", "2", "3");
        assertNotNull(res);
        assertEquals(5, res.length);
    }

}
