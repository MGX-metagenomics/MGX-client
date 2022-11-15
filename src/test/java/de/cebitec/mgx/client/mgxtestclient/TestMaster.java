package de.cebitec.mgx.client.mgxtestclient;

import de.cebitec.gpms.core.GPMSException;
import de.cebitec.gpms.core.MembershipI;
import de.cebitec.gpms.rest.GPMSClientFactory;
import de.cebitec.gpms.rest.GPMSClientI;
import de.cebitec.mgx.client.MGXDTOMaster;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;
import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
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

        String serverURI = "https://mgx-test.computational.bio.uni-giessen.de/MGX-maven-web/webresources/";

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

        GPMSClientI gpms = null;
        try {
            gpms = GPMSClientFactory.createClient("MyServer", serverURI, false);
            gpms.login("mgx_unittestRO", "gut-isM5iNt");
        } catch (GPMSException ex) {
            fail(ex.getMessage());
        }
        Iterator<MembershipI> mIter = null;
        try {
            mIter = gpms.getMemberships();
        } catch (GPMSException ex) {
            fail(ex.getMessage());
        }
        while (mIter != null && mIter.hasNext()) {
            MembershipI m = mIter.next();
            if ("MGX-2".equals(m.getProject().getProjectClass().getName()) && ("MGX2_Unittest".equals(m.getProject().getName()))) {
                try {
                    masterRO = new MGXDTOMaster(gpms.createMaster(m));
                } catch (GPMSException ex) {
                    fail(ex.getMessage());
                }
                break;
            }
        }

        assertNotNull(masterRO);
        assertEquals("MGX2_Unittest", masterRO.getProject().getName());
        return masterRO;
    }

    public static synchronized MGXDTOMaster getRW() {
        if (masterRW != null) {
            return masterRW;
        }

        String serverURI = "https://mgx-test.computational.bio.uni-giessen.de/MGX-maven-web/webresources/";
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

        GPMSClientI gpms;
        try {
            gpms = GPMSClientFactory.createClient("MyServer", serverURI, false);
        } catch (GPMSException ex) {
            fail(ex.getMessage());
            return null;
        }

        try {
            gpms.login("mgx_unittestRW", "hL0amo3oLae");
        } catch (GPMSException ex) {
            fail(ex.getMessage());
        }
        Iterator<MembershipI> mIter = null;
        try {
            mIter = gpms.getMemberships();
        } catch (GPMSException ex) {
            fail(ex.getMessage());
        }
        while (mIter != null && mIter.hasNext()) {
            MembershipI m = mIter.next();
            if ("MGX-2".equals(m.getProject().getProjectClass().getName()) && ("MGX2_Unittest".equals(m.getProject().getName()))) {
                try {
                    masterRW = new MGXDTOMaster(gpms.createMaster(m));
                } catch (GPMSException ex) {
                    fail(ex.getMessage());
                }
                break;
            }
        }

        assertNotNull(masterRW);
        assertEquals("MGX2_Unittest", masterRW.getProject().getName());
        return masterRW;
    }

    public static MGXDTOMaster getPrivate(String targetProject) {
        MGXDTOMaster master = null;

        String serverURI = "https://mgx-test.computational.bio.uni-giessen.de/MGX-maven-web/webresources/";

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
        GPMSClientI gpms;
        try {
            gpms = GPMSClientFactory.createClient("MyServer", serverURI, false);
        } catch (GPMSException ex) {
            fail(ex.getMessage());
            return null;
        }
        try {
            gpms.login(p.getProperty("username"), p.getProperty("password"));
        } catch (GPMSException ex) {
            fail(ex.getMessage());
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
            if (targetProject.equals(m.getProject().getName())) {
                try {
                    master = new MGXDTOMaster(gpms.createMaster(m));
                } catch (GPMSException ex) {
                    fail(ex.getMessage());
                }
                break;
            }
        }

        return master;
    }
}
