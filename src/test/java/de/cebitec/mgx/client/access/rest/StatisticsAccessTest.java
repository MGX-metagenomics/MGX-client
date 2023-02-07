package de.cebitec.mgx.client.access.rest;

import de.cebitec.mgx.client.MGXDTOMaster;
import de.cebitec.mgx.client.exception.MGXClientException;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 *
 * @author sjaenick
 */
//@RunWith(PaxExam.class)
public class StatisticsAccessTest {

//    @Configuration
//    public static Option[] configuration() {
//        return options(
//                junitBundles(),
//                MGXOptions.clientBundles(),
//                systemProperty("org.ops4j.pax.logging.DefaultServiceLog.level").value("WARN"),
//                bundle("reference:file:target/classes")
//        );
//    }
    private static MGXDTOMaster master;

    @BeforeAll
    public static void setUp() {
        master = TestMaster.getRO();
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
        } catch (MGXClientException ex) {
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
        } catch (MGXClientException ex) {
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
    public void testNewickToSVG() {
        System.out.println("NewickToSVG");
        try {
            String svgData = master.Statistics().newickToSVG("(Group 1:47.6812332055286,Group 2:47.6812332055286);");
            assertNotNull(svgData);
            assertFalse(svgData.isEmpty());
            assertTrue(svgData.contains("Group 1"));
            assertTrue(svgData.contains("Group 2"));
            assertTrue(svgData.contains("</svg>"));
        } catch (MGXDTOException ex) {
            fail(ex.getMessage());
        }
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
            assertNotEquals("", point.getName());
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

        assertEquals(6, ret.getPointCount());

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
            assertNotEquals("", point.getName());
            switch (point.getName()) {
                case "DS1":
                    assertEquals(0.2093366, Math.abs(point.getX()), 0.001);
                    assertEquals(0.0077758, Math.abs(point.getY()), 0.001);
                    break;
                case "DS2":
                    assertEquals(0.0605400, Math.abs(point.getX()), 0.001);
                    assertEquals(0.0315531, Math.abs(point.getY()), 0.001);
                    break;
                case "DS3":
                    assertEquals(0.14879663, Math.abs(point.getX()), 0.001);
                    assertEquals(0.02377733, Math.abs(point.getY()), 0.001);
                    break;

                case "Var1":
                case "Var2":
                case "Var3":
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
