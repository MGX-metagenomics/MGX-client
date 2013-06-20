package de.cebitec.mgx.client.mgxtestclient;

import de.cebitec.gpms.core.MembershipI;
import de.cebitec.gpms.rest.GPMSClientI;
import de.cebitec.mgx.client.MGXDTOMaster;
import de.cebitec.mgx.restgpms.GPMS;
import static org.junit.Assert.fail;

/**
 *
 * @author sj
 */
public class TestMaster {

    public static MGXDTOMaster get() {
        MGXDTOMaster master = null;

        //GPMSClientI gpms = new GPMS("MyServer", "https://mgx.cebitec.uni-bielefeld.de/MGX-maven-web/webresources/");
        GPMSClientI gpms = new GPMS("MyServer", "http://localhost:8080/MGX-maven-web/webresources/");
        if (!gpms.login("mgx_unittest", "gut-isM5iNt")) {
            fail();
        }
        for (MembershipI m : gpms.getMemberships()) {
            if ("MGX".equals(m.getProject().getProjectClass().getName()) && ("MGX_Unittest".equals(m.getProject().getName()))) {
                master = new MGXDTOMaster(gpms, m);
                break;
            }
        }

        assert master != null;
        assert master.getProject().getName().equals("MGX_Unittest");
        return master;
    }
}
