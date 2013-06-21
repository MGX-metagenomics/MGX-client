
package de.cebitec.mgx.client.access.rest.util;

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
public class XMLValidatorTest {
    
    public XMLValidatorTest() {
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
    public void testIsInvalid() {
        System.out.println("isInvalid");
        XMLValidator instance = new XMLValidator();
        boolean result = instance.isValid("malformed xml");
        assertEquals(false, result);
    }
}