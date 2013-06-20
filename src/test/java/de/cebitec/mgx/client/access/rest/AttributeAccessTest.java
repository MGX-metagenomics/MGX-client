package de.cebitec.mgx.client.access.rest;

import de.cebitec.mgx.client.MGXDTOMaster;
import de.cebitec.mgx.client.exception.MGXClientException;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.client.mgxtestclient.TestMaster;
import de.cebitec.mgx.dto.dto.AttributeCount;
import de.cebitec.mgx.dto.dto.AttributeDTO;
import de.cebitec.mgx.dto.dto.AttributeDistribution;
import de.cebitec.mgx.dto.dto.AttributeTypeDTO;
import java.util.List;
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
public class AttributeAccessTest {

    private MGXDTOMaster master;

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
        master = null;
    }

    @Test
    public void testGetAttribute() {
        System.out.println("testGetAttribute");
        AttributeDTO attr = null;
        try {
            attr = master.Attribute().fetch(1);
        } catch (MGXServerException | MGXClientException ex) {
            fail(ex.getMessage());
        }
        assertNotNull(attr);
        assertNotNull(attr.getValue());
        assertEquals("50.8", attr.getValue());
        assertTrue(attr.hasAttributeTypeId());
        assertEquals(1, attr.getAttributeTypeId());
    }

    @Test
    public void testGetDistribution() {
        System.out.println("getDistribution");

        AttributeDistribution dist = null;
        try {
            dist = master.Attribute().getDistribution(6, 3);
        } catch (MGXServerException ex) {
            fail(ex.getMessage());
        }
        assertNotNull(dist);
        assertEquals(5, dist.getAttributeCountsCount());
    }

    @Test
    public void testGetHierarchy() {
        System.out.println("getHierarchy");

        AttributeDistribution ad = null;

        try {
            ad = master.Attribute().getHierarchy(6, 3);
        } catch (MGXServerException ex) {
            fail(ex.getMessage());
        }
        assertNotNull(ad);
        assertEquals(7, ad.getAttributeTypeCount());
        assertEquals(30, ad.getAttributeCountsCount());

        List<AttributeTypeDTO> attributeTypeList = ad.getAttributeTypeList();
        assertNotNull(attributeTypeList);

        int roots = 0;
        for (AttributeCount ac : ad.getAttributeCountsList()) {
            assertNotNull(ac);
            assertNotEquals(0, ac.getCount());
            assertNotNull(ac.getAttribute());
            AttributeDTO attr = ac.getAttribute();
            if (attr.getValue().equals("Root")) {
                assertFalse(attr.hasParentId());
                roots++;
            } else {
                assertTrue(attr.hasParentId());
            }
        }
        assertEquals(1, roots);

    }
}