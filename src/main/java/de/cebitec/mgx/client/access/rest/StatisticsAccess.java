package de.cebitec.mgx.client.access.rest;

import de.cebitec.mgx.client.exception.MGXClientException;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.dto.dto.MGXLong;
import de.cebitec.mgx.dto.dto.MGXLongList;
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
        for (Number n: data) {
            b.addLong(MGXLong.newBuilder().setValue(n.longValue()).build());
        }
        return put("Statistics/Rarefaction", b.build(), PointDTOList.class).getPointList().iterator();
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