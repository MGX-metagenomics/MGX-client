package de.cebitec.mgx.client.access.rest;

import de.cebitec.gpms.core.MembershipI;
import de.cebitec.gpms.rest.GPMSClientI;
import de.cebitec.mgx.client.MGXDTOMaster;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.dto.dto.AttributeCount;
import de.cebitec.mgx.dto.dto.AttributeDTO;
import de.cebitec.mgx.dto.dto.AttributeDistribution;
import de.cebitec.mgx.dto.dto.AttributeTypeDTO;
import de.cebitec.mgx.restgpms.GPMS;
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

    public AttributeAccessTest() {
    }
    private MGXDTOMaster master;

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        GPMSClientI gpms = new GPMS("MyServer", "https://mgx.cebitec.uni-bielefeld.de/MGX-maven-web/webresources/");
        if (!gpms.login("mgx_unittest", "gut-isM5iNt")) {
            fail();
        }
        for (MembershipI m : gpms.getMemberships()) {
            if ("MGX".equals(m.getProject().getProjectClass().getName()) && ("MGX_Rsolani".equals(m.getProject().getName()))) {
                master = new MGXDTOMaster(gpms, m);
                break;
            }
        }

        assert master != null;
        assert master.getProject().getName().equals("MGX_Rsolani");
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testGetdistribution() {
        System.out.println("getDistribution");

        AttributeDistribution dist = null;
        try {
            dist = master.Attribute().getDistribution(8, 2);
        } catch (MGXServerException ex) {
            fail();
        }
        assertNotNull(dist);
        int cryptoCnt = 0;
        for (AttributeCount ac : dist.getAttributeCountsList()) {
            if (ac.getAttribute().getValue().equals("Cryptococcus")) {
                cryptoCnt++;
            }
        }
        assertEquals(2, cryptoCnt);
    }

    @Test
    public void testGetHierarchy() {
        System.out.println("getHierarchy");

        AttributeDistribution ad = null;

        try {
            ad = master.Attribute().getHierarchy(5, 2);
        } catch (MGXServerException ex) {
            fail();
        }
        assertNotNull(ad);
        assertEquals(ad.getAttributeTypeCount(), 8);
        assertEquals(ad.getAttributeCountsCount(), 1378);

        List<AttributeTypeDTO> attributeTypeList = ad.getAttributeTypeList();
        assertNotNull(attributeTypeList);

        int roots = 0;
        for (AttributeCount ac : ad.getAttributeCountsList()) {
            assertNotNull(ac);
            assertFalse(ac.getCount() == 0);
            assertNotNull(ac.getAttribute());
            AttributeDTO attr = ac.getAttribute();
            if (attr.getValue().equals("root")) {
                assertFalse(attr.hasParentId());
                roots ++;
            } else {
                assertTrue(attr.hasParentId());
            }
        }
        assertEquals(1, roots);

    }
}