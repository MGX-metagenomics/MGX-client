package de.cebitec.mgx.client.access.rest;

import de.cebitec.mgx.client.MGXDTOMaster;
import de.cebitec.mgx.client.mgxtestclient.TestMaster;
import de.cebitec.mgx.dto.dto.AttributeTypeDTO;
import java.util.Iterator;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;

/**
 *
 * @author sj
 */
//@RunWith(PaxExam.class)
public class AttributeTypeAccessTest {

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
    public void testBySeqRun() throws Exception {
        System.out.println("BySeqRun");
        MGXDTOMaster master = TestMaster.getRO();
        Iterator<AttributeTypeDTO> types = master.AttributeType().bySeqRun(49);
        assertNotNull(types);
        int cnt = 0;
        while (types.hasNext()) {
            AttributeTypeDTO next = types.next();
            assertNotNull(next);
            cnt++;
        }
        assertEquals(21, cnt);
    }

    @Test
    public void testFetch() throws Exception {
        System.out.println("fetch");
        MGXDTOMaster master = TestMaster.getRO();
        AttributeTypeDTO atype = master.AttributeType().fetch(1);
        assertNotNull(atype);
        assertNotNull(atype.getName());
        assertNotNull(atype.getStructure());
    }

    @Test
    public void testFetchall() throws Exception {
        System.out.println("fetchall");
        MGXDTOMaster master = TestMaster.getRO();
        Iterator<AttributeTypeDTO> types = master.AttributeType().fetchall();
        assertNotNull(types);
        int cnt = 0;
        while (types.hasNext()) {
            AttributeTypeDTO next = types.next();
            System.err.println(next.getName());
            assertNotNull(next);
            cnt++;
        }
        assertEquals(22, cnt);
    }

    @Test
    public void testByJob() throws Exception {
        System.out.println("ByJob");
        MGXDTOMaster master = TestMaster.getRO();
        Iterator<AttributeTypeDTO> types = master.AttributeType().byJob(3);
        assertNotNull(types);
        int cnt = 0;
        while (types.hasNext()) {
            AttributeTypeDTO next = types.next();
            assertNotNull(next);
            cnt++;
        }
        assertEquals(7, cnt);
    }
}
