package de.cebitec.mgx.client.access.rest;

import de.cebitec.gpms.rest.RESTAccessI;
import de.cebitec.mgx.client.exception.MGXClientException;
import de.cebitec.mgx.client.exception.MGXDTOException;
import de.cebitec.mgx.dto.dto.MGXDouble;
import de.cebitec.mgx.dto.dto.MGXDoubleList;
import de.cebitec.mgx.dto.dto.MGXMatrixDTO;
import de.cebitec.mgx.dto.dto.MGXString;
import de.cebitec.mgx.dto.dto.PCAResultDTO;
import de.cebitec.mgx.dto.dto.PointDTO;
import de.cebitec.mgx.dto.dto.PointDTOList;
import de.cebitec.mgx.dto.dto.ProfileDTO;
import java.util.Iterator;
import java.util.UUID;

/**
 *
 * @author sj
 */
public class StatisticsAccess extends AccessBase<PointDTO, PointDTOList> {

    public StatisticsAccess(RESTAccessI restAccess) {
        super(restAccess);
    }

    private static final String[] AGGLO = new String[]{"ward", "single", "complete", "average", "mcquitty", "median", "centroid"};
    private static final String[] DIST = new String[]{"aitchison", "euclidean", "maximum", "manhattan", "canberra", "binary", "minkowski"};

    public String Clustering(MGXMatrixDTO dto, String distMethod, String aggloMethod) throws MGXDTOException {
        if (!ArrayContains(DIST, distMethod)) {
            throw new MGXClientException("Invalid distance method: " + distMethod);
        }
        if (!ArrayContains(AGGLO, aggloMethod)) {
            throw new MGXClientException("Invalid agglomeration method: " + aggloMethod);
        }
        return put(dto, MGXString.class, "Statistics", "Clustering", distMethod, aggloMethod).getValue();
    }

    public PCAResultDTO PCA(MGXMatrixDTO dto, int pc1, int pc2) throws MGXDTOException {
        if (pc1 < 1 || pc1 > 3) {
            throw new MGXClientException("Invalid principal component: " + pc1);
        }
        if (pc2 < 1 || pc2 > 3) {
            throw new MGXClientException("Invalid principal component: " + pc2);
        }
        if (pc1 == pc2) {
            throw new MGXClientException("Need different principal components.");
        }
        if (dto.getRowCount() < 2) {
            throw new MGXClientException("Number of datasets too small.");
        }
        return put(dto, PCAResultDTO.class, "Statistics", "PCA", String.valueOf(pc1), String.valueOf(pc2));
    }

    public PointDTOList NMDS(MGXMatrixDTO dto) throws MGXDTOException {
        if (dto.getRowCount() < 3) {
            throw new MGXClientException("Number of datasets too small.");
        }
        return put(dto, PointDTOList.class, "Statistics", "NMDS");
    }

    public double aitchisonDistance(double[] d1, double[] d2) throws MGXDTOException {
        if (d1.length != d2.length) {
            throw new MGXClientException("Arrays do not have equal length.");
        }
        ProfileDTO.Builder d1dto = ProfileDTO.newBuilder();
        MGXDoubleList.Builder d1b = MGXDoubleList.newBuilder();
        for (double d : d1) {
            d1b = d1b.addValue(d);
        }
        d1dto.setName("d1");
        d1dto.setValues(d1b.build());

        ProfileDTO.Builder d2dto = ProfileDTO.newBuilder();
        MGXDoubleList.Builder d2b = MGXDoubleList.newBuilder();
        for (double d : d2) {
            d2b = d2b.addValue(d);
        }
        d2dto.setName("d2");
        d2dto.setValues(d2b.build());

        MGXMatrixDTO.Builder m = MGXMatrixDTO.newBuilder();
        m.addRow(d1dto.build());
        m.addRow(d2dto.build());
        return put(m.build(), MGXDouble.class, "Statistics", "aitchisonDistance").getValue();
    }

    public double[] toCLR(double[] counts) throws MGXDTOException {
        MGXDoubleList.Builder b = MGXDoubleList.newBuilder();
        for (double n : counts) {
            b.addValue(n);
        }

        MGXDoubleList result = put(b.build(), MGXDoubleList.class, "Statistics", "toCLR");

        double[] clr = new double[counts.length];
        for (int i = 0; i < clr.length; i++) {
            clr[i] = result.getValue(i);
        }
        return clr;
    }

    @Override
    public PointDTO fetch(long id) throws MGXDTOException {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public Iterator<PointDTO> fetchall() throws MGXDTOException {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public long create(PointDTO t) throws MGXDTOException {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public void update(PointDTO t) throws MGXDTOException {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public UUID delete(long id) throws MGXDTOException {
        throw new UnsupportedOperationException("Not supported.");
    }

    private static boolean ArrayContains(String[] options, String value) {
        for (String o : options) {
            if (o.equals(value)) {
                return true;
            }
        }
        return false;
    }

}
