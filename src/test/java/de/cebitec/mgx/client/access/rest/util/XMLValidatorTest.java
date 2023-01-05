package de.cebitec.mgx.client.access.rest.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 *
 * @author sj
 */
public class XMLValidatorTest {

    public XMLValidatorTest() {
    }

    @Test
    public void testNull() {
        System.out.println("testNull");
        XMLValidator instance = new XMLValidator();
        boolean result = instance.isValid(null);
        assertEquals(false, result);
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
        assertEquals(false, ret, "Graph without node named mgx should not be valid");
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
