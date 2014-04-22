package de.cebitec.mgx.client.access.rest;

import de.cebitec.mgx.client.exception.MGXClientException;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.dto.dto.MGXLongList;
import de.cebitec.mgx.dto.dto.MGXMatrixDTO;
import de.cebitec.mgx.dto.dto.MGXString;
import de.cebitec.mgx.dto.dto.PCAResultDTO;
import de.cebitec.mgx.dto.dto.PointDTO;
import de.cebitec.mgx.dto.dto.PointDTOList;
import java.util.Collection;
import java.util.Iterator;
import java.util.UUID;

/**
 *
 * @author sj
 */
public class StatisticsAccess extends AccessBase<PointDTO, PointDTOList> {

    public Iterator<PointDTO> Rarefaction(Collection<Number> data) throws MGXServerException, MGXClientException {
        MGXLongList.Builder b = MGXLongList.newBuilder();
        for (Number n : data) {
            b.addLong(n.longValue());
        }
        return put("Statistics/Rarefaction", b.build(), PointDTOList.class).getPointList().iterator();
    }

    public String Clustering(MGXMatrixDTO dto, String distMethod, String aggloMethod) throws MGXServerException, MGXClientException {
        return put("Statistics/Clustering/" + distMethod + "/" + aggloMethod, dto, MGXString.class).getValue();
    }

    public PCAResultDTO PCA(MGXMatrixDTO dto) throws MGXServerException, MGXClientException {
        return put("Statistics/PCA/", dto, PCAResultDTO.class);
    }

    @Override
    public PointDTO fetch(long id) throws MGXServerException, MGXClientException {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public Iterator<PointDTO> fetchall() throws MGXServerException, MGXClientException {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public long create(PointDTO t) throws MGXServerException, MGXClientException {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public void update(PointDTO t) throws MGXServerException, MGXClientException {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public UUID delete(long id) throws MGXServerException, MGXClientException {
        throw new UnsupportedOperationException("Not supported.");
    }

}
