package de.cebitec.mgx.client.mgxtestclient;

import de.cebitec.gpms.core.MembershipI;
import de.cebitec.gpms.rest.GPMSClientI;
import de.cebitec.mgx.client.MGXDTOMaster;
import de.cebitec.mgx.restgpms.GPMS;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import static org.junit.Assert.fail;

/**
 *
 * @author sj
 */
public class TestMaster {

    public static MGXDTOMaster get() {
        MGXDTOMaster master = null;
        
        String serverURI = "https://mgx.cebitec.uni-bielefeld.de/MGX-maven-web/webresources/";
        
        String config = System.getProperty("user.home") + "/.m2/mgx.junit";
        File f = new File(config);
        if (f.exists() && f.canRead()) {
            Properties p = new Properties();
            try {
                p.load(new FileInputStream(f));
                serverURI = p.getProperty("testserver");
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        }

        GPMS gpms = new GPMS("MyServer", serverURI);
        if (!gpms.login("mgx_unittest", "gut-isM5iNt")) {
            fail(gpms.getError());
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
