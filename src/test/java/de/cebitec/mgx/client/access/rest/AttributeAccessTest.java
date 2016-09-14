package de.cebitec.mgx.client.access.rest;

import de.cebitec.mgx.client.MGXDTOMaster;
import de.cebitec.mgx.client.exception.MGXDTOException;
import de.cebitec.mgx.client.mgxtestclient.TestMaster;
import de.cebitec.mgx.dto.dto.AttributeCount;
import de.cebitec.mgx.dto.dto.AttributeDTO;
import de.cebitec.mgx.dto.dto.AttributeDistribution;
import de.cebitec.mgx.dto.dto.AttributeTypeDTO;
import de.cebitec.mgx.dto.dto.SearchRequestDTO;
import de.cebitec.mgx.osgiutils.MGXOptions;
import java.util.Iterator;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import static org.ops4j.pax.exam.CoreOptions.bundle;
import static org.ops4j.pax.exam.CoreOptions.junitBundles;
import static org.ops4j.pax.exam.CoreOptions.options;
import static org.ops4j.pax.exam.CoreOptions.systemProperty;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;

/**
 *
 * @author sj
 */
@RunWith(PaxExam.class)
public class AttributeAccessTest {

    @Configuration
    public static Option[] configuration() {
        return options(
                junitBundles(),
                MGXOptions.clientBundles(),
                systemProperty("org.ops4j.pax.logging.DefaultServiceLog.level").value("WARN"),
                bundle("reference:file:target/classes")
        );
    }

    @Test
    public void testGetAttribute() {
        System.out.println("testGetAttribute");
        MGXDTOMaster master = TestMaster.getRO();
        AttributeDTO attr = null;
        try {
            attr = master.Attribute().fetch(1);
        } catch (MGXDTOException ex) {
            fail(ex.getMessage());
        }
        assertNotNull(attr);
        assertNotNull(attr.getValue());
        assertEquals("50.8", attr.getValue());
        assertTrue(attr.hasAttributeTypeId());
        assertEquals(1, attr.getAttributeTypeId());
        assertTrue(attr.hasJobid());
        assertEquals(1, attr.getJobid());
    }

    @Test
    public void testGetDistribution() {
        System.out.println("getDistribution");

        MGXDTOMaster master = TestMaster.getRO();

        AttributeDistribution dist = null;
        try {
            dist = master.Attribute().getDistribution(6, 3);
        } catch (MGXDTOException ex) {
            fail(ex.getMessage());
        }
        assertNotNull(dist);
        assertEquals(5, dist.getAttributeCountsCount());
    }

    @Test
    public void testGetHierarchy() {
        System.out.println("getHierarchy");
        MGXDTOMaster master = TestMaster.getRO();

        AttributeDistribution ad = null;

        try {
            ad = master.Attribute().getHierarchy(6, 3);
        } catch (MGXDTOException ex) {
            fail(ex.getMessage());
        }
        assertNotNull(ad);
        assertEquals(7, ad.getAttributeTypeCount());
        assertEquals(30, ad.getAttributeCountsCount());

        List<AttributeTypeDTO> attributeTypeList = ad.getAttributeTypeList();
        assertNotNull(attributeTypeList);

        int roots = 0;
        long total = 0;
        for (AttributeCount ac : ad.getAttributeCountsList()) {
            assertNotNull(ac);
            assertNotEquals(0, ac.getCount());
            total += ac.getCount();
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
        assertEquals(339, total);

    }

    @Test
    public void testFind() {
        System.out.println("testFind");
        MGXDTOMaster master = TestMaster.getRO();

        SearchRequestDTO req = SearchRequestDTO.newBuilder()
                .setExact(false)
                .setTerm("meth")
                .addSeqrunId(2)
                .build();

        Iterator<String> iter = null;
        try {
            iter = master.Attribute().find(req);
        } catch (MGXDTOException ex) {
            fail(ex.getMessage());
        }
        assertNotNull(iter);
        int cnt = 0;
        while (iter.hasNext()) {
            String s = iter.next();
            cnt++;
            System.err.println(s);
        }
        assertEquals(179, cnt);
    }

    @Test
    public void testByJob() {
        System.out.println("testBySeqRun");
        MGXDTOMaster master = TestMaster.getRO();

        Iterator<AttributeDTO> iter = null;
        try {
            iter = master.Attribute().byJob(3);
        } catch (MGXDTOException ex) {
            fail(ex.getMessage());
        }
        assertNotNull(iter);
        int cnt = 0;
        while (iter.hasNext()) {
            AttributeDTO attr = iter.next();
            cnt++;
            System.err.println(attr.getValue());
        }
        assertEquals(30, cnt);
    }
}
