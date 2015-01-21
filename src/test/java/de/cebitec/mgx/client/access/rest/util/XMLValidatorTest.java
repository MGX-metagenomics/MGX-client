package de.cebitec.mgx.client.access.rest.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
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

    @Test
    public void testIsValid() throws IOException {
        System.out.println("testIsValid");
        boolean ret = testValid("src/test/resources/invalid.xml");
        assertEquals(false, ret);
    }

    @Test
    public void testIsValid2() throws IOException {
        System.out.println("testIsValid2");
        boolean ret = testValid("src/test/resources/getmgxjob.xml");
        assertEquals(true, ret);
    }

    private static boolean testValid(String fName) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(fName));
        String s = "";
        String x;
        while ((x = br.readLine()) != null) {
            s = s + x;
        }
        XMLValidator instance = new XMLValidator();
        return instance.isValid(s);
    }
}
