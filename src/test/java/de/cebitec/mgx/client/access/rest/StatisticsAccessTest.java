package de.cebitec.mgx.client.access.rest;

import de.cebitec.mgx.client.MGXDTOMaster;
import de.cebitec.mgx.client.exception.MGXDTOException;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.client.mgxtestclient.TestMaster;
import de.cebitec.mgx.dto.dto.MGXDoubleList;
import de.cebitec.mgx.dto.dto.MGXMatrixDTO;
import de.cebitec.mgx.dto.dto.MGXString;
import de.cebitec.mgx.dto.dto.MGXStringList;
import de.cebitec.mgx.dto.dto.PCAResultDTO;
import de.cebitec.mgx.dto.dto.PointDTO;
import de.cebitec.mgx.dto.dto.PointDTOList;
import de.cebitec.mgx.dto.dto.ProfileDTO;
import de.cebitec.mgx.osgiutils.MGXOptions;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.junit.After;
import org.junit.Before;
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
 * @author sjaenick
 */
@RunWith(PaxExam.class)
public class StatisticsAccessTest {

    @Configuration
    public static Option[] configuration() {
        return options(
                junitBundles(),
                MGXOptions.clientBundles(),
                systemProperty("org.ops4j.pax.logging.DefaultServiceLog.level").value("WARN"),
                bundle("reference:file:target/classes")
        );
    }
    private MGXDTOMaster master;

    @Before
    public void setUp() {
        master = TestMaster.getRO();
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testRarefaction() throws Exception {
        System.out.println("testRarefaction");
        Collection<Long> data = new LinkedList<>();
        data.add(1L);
        data.add(2L);
        data.add(3L);
        data.add(3L);
        data.add(4L);
        data.add(5L);
        Iterator<PointDTO> iter = master.Statistics().Rarefaction(data);
        assertNotNull(iter);

        List<PointDTO> ret = new LinkedList<>();
        while (iter.hasNext()) {
            PointDTO p = iter.next();
            //System.err.println(p.getX() + " / " + p.getY());
            ret.add(p);
        }
        assertEquals(14, ret.size());

        PointDTO p1 = ret.get(0);
        PointDTO p2 = ret.get(1);
        PointDTO p3 = ret.get(2);
        PointDTO p4 = ret.get(3);
        PointDTO p5 = ret.get(4);

        // check sample sizes
        assertEquals(0, p1.getX(), 0.0001);
        assertEquals(5, p2.getX(), 0.0001);
        assertEquals(10, p3.getX(), 0.0001);
        assertEquals(15, p4.getX(), 0.0001);
        assertEquals(18, p5.getX(), 0.0001);

        // check richness estimates
        assertEquals(0, p1.getY(), 0.0001);  // always zero
        assertEquals(3.683123, p2.getY(), 0.0001);
        assertEquals(5.205882, p3.getY(), 0.0001);
        assertEquals(5.811275, p4.getY(), 0.0001);
        assertEquals(data.size(), p5.getY(), 0.0001);  // always number of categories
    }

    @Test
    public void testRarefaction2() throws Exception {
        System.out.println("testRarefaction2");
        Collection<Long> data = new LinkedList<>();
        data.add(1000L);
        data.add(501L);
        Iterator<PointDTO> iter = master.Statistics().Rarefaction(data);
        assertNotNull(iter);

        List<PointDTO> ret = new LinkedList<>();
        while (iter.hasNext()) {
            PointDTO p = iter.next();
            ret.add(p);
        }
        assertEquals(80, ret.size());

        PointDTO p1 = ret.get(0);
        PointDTO p2 = ret.get(1);
        PointDTO p3 = ret.get(2);
        PointDTO p4 = ret.get(3);
        PointDTO p5 = ret.get(4);

        // check sample sizes
        assertEquals(0, p1.getX(), 0.0001);
        assertEquals(20, p2.getX(), 0.0001);
        assertEquals(40, p3.getX(), 0.0001);
        assertEquals(60, p4.getX(), 0.0001);
        assertEquals(80, p5.getX(), 0.0001);

        // check richness estimates
        assertEquals(0, p1.getY(), 0.0001);  // always zero
        assertEquals(1.999721681213, p2.getY(), 0.0000001);
        //assertEquals(1.999995589, p3.getY(), 0.0000001);
        //assertEquals(1.9999999917641063, p4.getY(), 0.00000000001);
        assertEquals(data.size(), p5.getY(), 0.0000001);  // always number of categories
    }

    @Test
    public void testClusteringInvalidDist() {
        System.out.println("ClusteringInvalidDist");
        MGXDTOMaster m = TestMaster.getRO();
        assertNotNull(m);
        MGXMatrixDTO.Builder matrix = MGXMatrixDTO.newBuilder();
        matrix.setColNames(MGXStringList.newBuilder()
                .addString(MGXString.newBuilder().setValue("Var1").build())
                .addString(MGXString.newBuilder().setValue("Var2").build())
                .addString(MGXString.newBuilder().setValue("Var3").build())
        );

        ProfileDTO p1 = ProfileDTO.newBuilder()
                .setName("DS1")
                .setValues(buildVector(new double[]{1, 2, 10}))
                .build();
        matrix.addRow(p1);

        ProfileDTO p2 = ProfileDTO.newBuilder()
                .setName("DS2")
                .setValues(buildVector(new double[]{11, 222, 3}))
                .build();
        matrix.addRow(p2);

        try {
            master.Statistics().Clustering(matrix.build(), "XXX", "ward");
        } catch (MGXServerException ex) {
            if (ex.getMessage().contains("Invalid distance method")) {
                return;
            }
            fail(ex.getMessage());
        } catch (MGXDTOException ex) {
            fail(ex.getMessage());
        }
        fail();
    }

    @Test
    public void testClusteringInvalidAgglo() {
        System.out.println("ClusteringInvalidAgglo");
        MGXDTOMaster m = TestMaster.getRO();
        assertNotNull(m);
        MGXMatrixDTO.Builder matrix = MGXMatrixDTO.newBuilder();
        matrix.setColNames(MGXStringList.newBuilder()
                .addString(MGXString.newBuilder().setValue("Var1").build())
                .addString(MGXString.newBuilder().setValue("Var2").build())
                .addString(MGXString.newBuilder().setValue("Var3").build())
        );

        ProfileDTO p1 = ProfileDTO.newBuilder()
                .setName("DS1")
                .setValues(buildVector(new double[]{1, 2, 10}))
                .build();
        matrix.addRow(p1);

        ProfileDTO p2 = ProfileDTO.newBuilder()
                .setName("DS2")
                .setValues(buildVector(new double[]{11, 222, 3}))
                .build();
        matrix.addRow(p2);

        try {
            master.Statistics().Clustering(matrix.build(), "euclidean", "XXX");
        } catch (MGXServerException ex) {
            if (ex.getMessage().contains("Invalid agglomeration method")) {
                return;
            }
            fail(ex.getMessage());
        } catch (MGXDTOException ex) {
            fail(ex.getMessage());
        }
        fail();
    }

    @Test
    public void testClusteringEqual() {
        System.out.println("ClusteringEqual");
        MGXDTOMaster m = TestMaster.getRO();
        assertNotNull(m);
        MGXMatrixDTO.Builder matrix = MGXMatrixDTO.newBuilder();
        matrix.setColNames(MGXStringList.newBuilder()
                .addString(MGXString.newBuilder().setValue("Var1").build())
                .addString(MGXString.newBuilder().setValue("Var2").build())
                .addString(MGXString.newBuilder().setValue("Var3").build())
        );

        ProfileDTO p1 = ProfileDTO.newBuilder()
                .setName("DS1")
                .setValues(buildVector(new double[]{1, 2, 3}))
                .build();
        matrix.addRow(p1);

        ProfileDTO p2 = ProfileDTO.newBuilder()
                .setName("DS2")
                .setValues(buildVector(new double[]{1, 2, 3}))
                .build();
        matrix.addRow(p2);
        try {
            master.Statistics().Clustering(matrix.build(), "euclidean", "ward");
        } catch (MGXServerException ex) {
            if (ex.getMessage().contains("Could not cluster data")) {
                return;
            }
            fail(ex.getMessage());
        } catch (MGXDTOException ex) {
            fail(ex.getMessage());
        }
        fail();
    }

    @Test
    public void testPCA() throws Exception {
        System.out.println("testPCA");
        MGXDTOMaster m = TestMaster.getRO();
        assertNotNull(m);

        MGXMatrixDTO.Builder matrix = MGXMatrixDTO.newBuilder();
        matrix.setColNames(MGXStringList.newBuilder()
                .addString(MGXString.newBuilder().setValue("Var1").build())
                .addString(MGXString.newBuilder().setValue("Var2").build())
                .addString(MGXString.newBuilder().setValue("Var3").build())
        );

        ProfileDTO p1 = ProfileDTO.newBuilder()
                .setName("DS1")
                .setValues(buildVector(new double[]{1, 2, 3}))
                .build();
        matrix.addRow(p1);

        ProfileDTO p2 = ProfileDTO.newBuilder()
                .setName("DS2")
                .setValues(buildVector(new double[]{2, 2, 3}))
                .build();
        matrix.addRow(p2);

        ProfileDTO p3 = ProfileDTO.newBuilder()
                .setName("DS3")
                .setValues(buildVector(new double[]{6, 1, 5}))
                .build();
        matrix.addRow(p3);

        PCAResultDTO ret = master.Statistics().PCA(matrix.build(), 1, 2);
        assertNotNull(ret);

        assertEquals(3, ret.getDatapointCount());

        PointDTO ds1 = null;
        for (PointDTO point : ret.getDatapointList()) {
            if (point.getName().equals("DS1")) {
                ds1 = point;
                break;
            }
        }
        assertNotNull(ds1);

        // check points
        for (PointDTO point : ret.getDatapointList()) {
            assertTrue(point.hasName());
            switch (point.getName()) {
                case "DS1":
                    assertEquals(1.1026783, Math.abs(point.getX()), 0.001);
                    assertEquals(0.1489825, Math.abs(point.getY()), 0.001);
                    break;
                case "DS2":
                    assertEquals(0.8853494, Math.abs(point.getX()), 0.001);
                    assertEquals(0.16025085, Math.abs(point.getY()), 0.001);
                    break;
                case "DS3":
                    assertEquals(1.9880277, Math.abs(point.getX()), 0.001);
                    assertEquals(0.01126835, Math.abs(point.getY()), 0.001);
                    break;
                default:
                    fail();
            }
        }

        // variances
        assertEquals(3, ret.getVarianceCount());
        assertEquals(2.975998e+00, ret.getVariance(0), 0.00001);
        assertEquals(2.400155e-02, ret.getVariance(1), 0.00001);
        assertEquals(9.423701e-35, ret.getVariance(2), 0.00001);

        assertEquals(3, ret.getLoadingCount());
//        for (PointDTO p : ret.getLoadingList()) {
//            System.err.println(p.getName() + ": " + p.getX() + " / " + p.getY());
//        }
    }

    @Test
    public void testPCANullValues() throws Exception {
        System.out.println("testPCANullValues");
        MGXDTOMaster m = TestMaster.getRO();
        assertNotNull(m);
        MGXMatrixDTO.Builder matrix = MGXMatrixDTO.newBuilder();
        matrix.setColNames(MGXStringList.newBuilder()
                .addString(MGXString.newBuilder().setValue("Var1").build())
                .addString(MGXString.newBuilder().setValue("Var2").build())
                .addString(MGXString.newBuilder().setValue("Var3").build())
        );

        ProfileDTO p1 = ProfileDTO.newBuilder()
                .setName("DS1")
                .setValues(buildVector(new double[]{1, 2, 0}))
                .build();
        matrix.addRow(p1);

        ProfileDTO p2 = ProfileDTO.newBuilder()
                .setName("DS2")
                .setValues(buildVector(new double[]{2, 1, 0}))
                .build();
        matrix.addRow(p2);

        PCAResultDTO ret = master.Statistics().PCA(matrix.build(), 1, 2);
        assertNotNull(ret);
    }

    @Test
    public void testPCANonExistingPC3() {
        System.out.println("testPCANonExistingPC3");
        MGXDTOMaster m = TestMaster.getRO();
        assertNotNull(m);
        MGXMatrixDTO.Builder matrix = MGXMatrixDTO.newBuilder();
        matrix.setColNames(MGXStringList.newBuilder()
                .addString(MGXString.newBuilder().setValue("Var1").build())
                .addString(MGXString.newBuilder().setValue("Var2").build())
                .addString(MGXString.newBuilder().setValue("Var3").build())
        );

        ProfileDTO p1 = ProfileDTO.newBuilder()
                .setName("DS1")
                .setValues(buildVector(new double[]{1, 2, 0}))
                .build();
        matrix.addRow(p1);

        ProfileDTO p2 = ProfileDTO.newBuilder()
                .setName("DS2")
                .setValues(buildVector(new double[]{2, 1, 0}))
                .build();
        matrix.addRow(p2);
        try {
            PCAResultDTO ret = master.Statistics().PCA(matrix.build(), 1, 3);
        } catch (MGXServerException ex) {
            if (ex.getMessage().contains("Could not access requested principal")) {
                return;
            }
            fail(ex.getMessage());
        } catch (MGXDTOException ex) {
            fail(ex.getMessage());
        }
    }

    @Test
    public void testPCAUnbalancedValues() {
        System.out.println("testPCAUnbalancedValues");
        MGXDTOMaster m = TestMaster.getRO();
        assertNotNull(m);
        MGXMatrixDTO.Builder matrix = MGXMatrixDTO.newBuilder();
        matrix.setColNames(MGXStringList.newBuilder()
                .addString(MGXString.newBuilder().setValue("Var1").build())
                .addString(MGXString.newBuilder().setValue("Var2").build())
                .addString(MGXString.newBuilder().setValue("Var3").build())
        );

        ProfileDTO p1 = ProfileDTO.newBuilder()
                .setName("DS1")
                .setValues(buildVector(new double[]{1, 2, 0}))
                .build();
        matrix.addRow(p1);

        ProfileDTO p2 = ProfileDTO.newBuilder()
                .setName("DS2")
                .setValues(buildVector(new double[]{2, 1, 0, 71}))
                .build();
        matrix.addRow(p2);

        PCAResultDTO ret;
        try {
            ret = master.Statistics().PCA(matrix.build(), 1, 2);
        } catch (MGXServerException ex) {
            if (ex.getMessage().contains("Error in data matrix")) {
                return;
            }
            fail(ex.getMessage());
        } catch (MGXDTOException ex) {
            fail(ex.getMessage());
        }
        fail();
    }

    @Test
    public void testPCAColNames() {
        System.out.println("testPCAColNames");
        MGXDTOMaster m = TestMaster.getRO();
        assertNotNull(m);
        MGXMatrixDTO.Builder matrix = MGXMatrixDTO.newBuilder();
        matrix.setColNames(MGXStringList.newBuilder()
                .addString(MGXString.newBuilder().setValue("Var1").build())
                .addString(MGXString.newBuilder().setValue("Var2").build())
                .addString(MGXString.newBuilder().setValue("Var3").build())
                .addString(MGXString.newBuilder().setValue("Var4").build())
        );

        ProfileDTO p1 = ProfileDTO.newBuilder()
                .setName("DS1")
                .setValues(buildVector(new double[]{1, 2, 0}))
                .build();
        matrix.addRow(p1);

        ProfileDTO p2 = ProfileDTO.newBuilder()
                .setName("DS2")
                .setValues(buildVector(new double[]{2, 1, 1}))
                .build();
        matrix.addRow(p2);

        PCAResultDTO ret;
        try {
            ret = master.Statistics().PCA(matrix.build(), 1, 2);
        } catch (MGXServerException ex) {
            if (ex.getMessage().contains("Error in data matrix")) {
                return;
            }
            fail(ex.getMessage());
        } catch (MGXDTOException ex) {
            fail(ex.getMessage());
        }
        fail();
    }

    @Test
    public void testPCoA() throws Exception {
        System.out.println("testPCoA");
        MGXDTOMaster m = TestMaster.getRO();
        assertNotNull(m);

        MGXMatrixDTO.Builder matrix = MGXMatrixDTO.newBuilder();
        matrix.setColNames(MGXStringList.newBuilder()
                .addString(MGXString.newBuilder().setValue("Var1").build())
                .addString(MGXString.newBuilder().setValue("Var2").build())
                .addString(MGXString.newBuilder().setValue("Var3").build())
        );

        ProfileDTO p1 = ProfileDTO.newBuilder()
                .setName("DS1")
                .setValues(buildVector(new double[]{1, 2, 3}))
                .build();
        matrix.addRow(p1);

        ProfileDTO p2 = ProfileDTO.newBuilder()
                .setName("DS2")
                .setValues(buildVector(new double[]{2, 0, 3}))
                .build();
        matrix.addRow(p2);

        ProfileDTO p3 = ProfileDTO.newBuilder()
                .setName("DS3")
                .setValues(buildVector(new double[]{2.2, 0.1, 2.3}))
                .build();
        matrix.addRow(p3);

        PointDTOList ret = master.Statistics().NMDS(matrix.build());
        assertNotNull(ret);

        assertEquals(3, ret.getPointCount());

        PointDTO ds1 = null;
        for (PointDTO point : ret.getPointList()) {
            if (point.getName().equals("DS1")) {
                ds1 = point;
                break;
            }
        }
        assertNotNull(ds1);

        // check points
        for (PointDTO point : ret.getPointList()) {
            assertTrue(point.hasName());
            switch (point.getName()) {
                case "DS1":
                    assertEquals(1.5105621, Math.abs(point.getX()), 0.001);
                    assertEquals(0.0205973, Math.abs(point.getY()), 0.001);
                    break;
                case "DS2":
                    assertEquals(0.69076839, Math.abs(point.getX()), 0.001);
                    assertEquals(0.37201421, Math.abs(point.getY()), 0.001);
                    break;
                case "DS3":
                    assertEquals(0.81979373, Math.abs(point.getX()), 0.001);
                    assertEquals(0.35141681, Math.abs(point.getY()), 0.001);
                    break;
                default:
                    fail();
            }
        }
    }

    private static MGXDoubleList buildVector(double[] data) {
        MGXDoubleList.Builder b = MGXDoubleList.newBuilder();
        for (double l : data) {
            b.addValue(l);
        }
        return b.build();
    }
}
