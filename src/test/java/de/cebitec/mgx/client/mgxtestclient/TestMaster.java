package de.cebitec.mgx.client.mgxtestclient;

import de.cebitec.gpms.core.GPMSException;
import de.cebitec.gpms.core.MembershipI;
import de.cebitec.gpms.rest.GPMSClientI;
import de.cebitec.mgx.client.MGXDTOMaster;
import de.cebitec.mgx.restgpms.GPMSClient;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;
import org.junit.Assert;
import static org.junit.Assert.fail;

/**
 *
 * @author sj
 */
public class TestMaster {

    private static MGXDTOMaster masterRO;
    private static MGXDTOMaster masterRW;

    public static synchronized MGXDTOMaster getRO() {
        if (masterRO != null) {
            return masterRO;
        }

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

        GPMSClient gpms = new GPMSClient("MyServer", serverURI);
        if (!gpms.login("mgx_unittestRO", "gut-isM5iNt")) {
            fail(gpms.getError());
        }
        Iterator<MembershipI> mIter = null;
        try {
            mIter = gpms.getMemberships();
        } catch (GPMSException ex) {
            fail(ex.getMessage());
        }
        while (mIter.hasNext()) {
            MembershipI m = mIter.next();
            if ("MGX".equals(m.getProject().getProjectClass().getName()) && ("MGX_Unittest".equals(m.getProject().getName()))) {
                masterRO = new MGXDTOMaster(gpms.createMaster(m));
                break;
            }
        }

        assert masterRO != null;
        assert masterRO.getProject().getName().equals("MGX_Unittest");
        return masterRO;
    }

    public static synchronized MGXDTOMaster getRW() {
        if (masterRW != null) {
            return masterRW;
        }

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
        GPMSClient gpms = new GPMSClient("MyServer", serverURI);
        if (!gpms.login("mgx_unittestRW", "hL0amo3oLae")) {
            fail(gpms.getError());
        }
        Iterator<MembershipI> mIter = null;
        try {
            mIter = gpms.getMemberships();
        } catch (GPMSException ex) {
            fail(ex.getMessage());
        }
        while (mIter.hasNext()) {
            MembershipI m = mIter.next();
            if ("MGX".equals(m.getProject().getProjectClass().getName()) && ("MGX_Unittest".equals(m.getProject().getName()))) {
                masterRW = new MGXDTOMaster(gpms.createMaster(m));
                break;
            }
        }

        assert masterRW != null;
        assert masterRW.getProject().getName().equals("MGX_Unittest");
        return masterRW;
    }

    public static MGXDTOMaster getPrivate(String targetProject) {
        MGXDTOMaster master = null;

        String serverURI = "https://mgx.cebitec.uni-bielefeld.de/MGX-maven-web/webresources/";

        Properties p = new Properties();
        String config = System.getProperty("user.home") + "/.m2/mgx.private";
        File f = new File(config);
        if (f.exists() && f.canRead()) {
            try {
                p.load(new FileInputStream(f));
                serverURI = p.getProperty("testserver");
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        }
        GPMSClientI gpms = new GPMSClient("MyServer", serverURI);
        if (!gpms.login(p.getProperty("username"), p.getProperty("password"))) {
            return null;
        }
        Iterator<MembershipI> mIter = null;
        try {
            mIter = gpms.getMemberships();
        } catch (GPMSException ex) {
            fail(ex.getMessage());
        }
        Assert.assertNotNull(mIter);

        while (mIter.hasNext()) {
            MembershipI m = mIter.next();
            if ("MGX".equals(m.getProject().getProjectClass().getName()) && (targetProject.equals(m.getProject().getName()))) {
                master = new MGXDTOMaster(gpms.createMaster(m));
                break;
            }
        }

        return master;
    }
}
