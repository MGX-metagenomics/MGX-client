package de.cebitec.mgx.client.access.rest;

import de.cebitec.mgx.client.MGXDTOMaster;
import de.cebitec.mgx.client.exception.MGXDTOException;
import de.cebitec.mgx.client.mgxtestclient.TestMaster;
import de.cebitec.mgx.dto.dto.AttributeCount;
import de.cebitec.mgx.dto.dto.AttributeDTO;
import de.cebitec.mgx.dto.dto.AttributeDistribution;
import de.cebitec.mgx.dto.dto.AttributeTypeDTO;
import de.cebitec.mgx.dto.dto.SearchRequestDTO;
import java.util.Iterator;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.Test;

/**
 *
 * @author sj
 */
//@RunWith(PaxExam.class)
public class AttributeAccessTest {

//    @Configuration
//    public static Option[] configuration() {
//        return options(
//                junitBundles(),
//                MGXOptions.clientBundles(),
//                systemProperty("org.ops4j.pax.logging.DefaultServiceLog.level").value("WARN"),
//                bundle("reference:file:target/classes")
//        );
//    }

    @Test
    public void testGetAttribute() {
        System.out.println("testGetAttribute");
        MGXDTOMaster master = TestMaster.getRO();
        AttributeDTO attr = null;
        try {
            attr = master.Attribute().fetch(9229);
        } catch (MGXDTOException ex) {
            fail(ex.getMessage());
        }
        assertNotNull(attr);
        assertNotNull(attr.getValue());
        assertEquals("Bacteria", attr.getValue());
        assertEquals(2, attr.getAttributeTypeId());
        assertEquals(7, attr.getJobId());
    }

    @Test
    public void testGetDistribution() {
        System.out.println("getDistribution");

        MGXDTOMaster master = TestMaster.getRO();

        AttributeDistribution dist = null;
        try {
            dist = master.Attribute().getDistribution(3, 7, 49);
        } catch (MGXDTOException ex) {
            fail(ex.getMessage());
        }
        assertNotNull(dist);
        assertEquals(54, dist.getAttributeCountsCount());
    }

    @Test
    public void testGetHierarchy() {
        System.out.println("getHierarchy");
        MGXDTOMaster master = TestMaster.getRO();

        AttributeDistribution ad = null;

        try {
            ad = master.Attribute().getHierarchy(1, 7, 49);
        } catch (MGXDTOException ex) {
            fail(ex.getMessage());
        }
        assertNotNull(ad);

        List<AttributeTypeDTO> attributeTypeList = ad.getAttributeTypeList();
        assertNotNull(attributeTypeList);
//        for (AttributeTypeDTO  at : attributeTypeList) {
//            System.err.println(at.getName());
//        }

        assertEquals(9, ad.getAttributeTypeCount());
        assertEquals(3256, ad.getAttributeCountsCount());

        int roots = 0;
        long total = 0;
        for (AttributeCount ac : ad.getAttributeCountsList()) {
            assertNotNull(ac);
            assertNotEquals(0, ac.getCount());
            total += ac.getCount();
            assertNotNull(ac.getAttribute());
            AttributeDTO attr = ac.getAttribute();
            if (attr.getValue().equals("root")) {
                assertEquals(0, attr.getParentId());
                roots++;
            } else {
                assertNotEquals(1, attr.getParentId());
            }
        }
        assertEquals(1, roots);
        assertEquals(135097, total);

    }

    @Test
    public void testFind() {
        System.out.println("testFind");
        MGXDTOMaster master = TestMaster.getRO();

        SearchRequestDTO req = SearchRequestDTO.newBuilder()
                .setExact(false)
                .setTerm("meth")
                .setSeqrunId(49)
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
        assertEquals(319, cnt);
    }

    @Test
    public void testFindCaseInsensitive() {
        System.out.println("testFindCaseInsensitive");
        MGXDTOMaster master = TestMaster.getRO();

        Iterator<String> iter = null;
        
        
        // 
        // lower-case search
        //
        try {
            iter = master.Attribute().find(SearchRequestDTO.newBuilder()
                    .setExact(false)
                    .setTerm("alcohol")
                    .setSeqrunId(49)
                    .build());
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
        assertEquals(10, cnt);
        
        // 
        // upper-case search
        //
        try {
            iter = master.Attribute().find(SearchRequestDTO.newBuilder()
                    .setExact(false)
                    .setTerm("ALCOHOL")
                    .setSeqrunId(49)
                    .build());
        } catch (MGXDTOException ex) {
            fail(ex.getMessage());
        }
        assertNotNull(iter);
        cnt = 0;
        while (iter.hasNext()) {
            String s = iter.next();
            cnt++;
            System.err.println(s);
        }
        assertEquals(10, cnt);
    }

    @Test
    public void testByJob() {
        System.out.println("testBySeqRun");
        MGXDTOMaster master = TestMaster.getRO();

        Iterator<AttributeDTO> iter = null;
        try {
            iter = master.Attribute().byJob(7);
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
        assertEquals(3256, cnt);
    }
}
