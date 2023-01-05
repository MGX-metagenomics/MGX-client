/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.client.access.rest.util;

import de.cebitec.mgx.client.exception.MGXClientException;
import de.cebitec.mgx.dto.dto.JobDTO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;

/**
 *
 * @author sj
 */
public class RESTPathResolverTest {

    public RESTPathResolverTest() {
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
