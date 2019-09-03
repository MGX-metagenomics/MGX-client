package de.cebitec.mgx.client.access.rest;

import de.cebitec.gpms.rest.RESTAccessI;
import de.cebitec.mgx.client.exception.MGXClientException;
import de.cebitec.mgx.client.exception.MGXDTOException;
import de.cebitec.mgx.dto.dto.GeneObservationDTO;
import de.cebitec.mgx.dto.dto.GeneObservationDTOList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.UUID;

/**
 *
 * @author sj
 */
public class GeneObservationAccess extends AccessBase<GeneObservationDTO, GeneObservationDTOList> {

    public GeneObservationAccess(RESTAccessI restAccess) {
        super(restAccess);
    }

    public Iterator<GeneObservationDTO> byGene(long seqId) throws MGXDTOException {
        return get(GeneObservationDTOList.class, r.resolve(GeneObservationDTO.class, "byGene", String.valueOf(seqId))).getObservationList().iterator();
    }

    @Override
    public GeneObservationDTO fetch(long id) throws MGXDTOException {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public Iterator<GeneObservationDTO> fetchall() throws MGXDTOException {
        throw new UnsupportedOperationException("Not supported.");
    }

    public void create(long seqId, long attrId, GeneObservationDTO dto) throws MGXDTOException {
        if (dto == null) {
            throw new MGXClientException("Cannot create null object.");
        }
        String[] resolve = r.resolve(GeneObservationDTO.class, "create");
        resolve = Arrays.copyOf(resolve, resolve.length + 2);
        resolve[resolve.length - 2] = String.valueOf(seqId);
        resolve[resolve.length - 1] = String.valueOf(attrId);
        put(dto, resolve);
    }

    @Override
    public void update(GeneObservationDTO t) throws MGXDTOException {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public UUID delete(long id) throws MGXDTOException {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public long create(GeneObservationDTO t) throws MGXDTOException {
        throw new UnsupportedOperationException("Not supported.");
    }

    public void delete(long seqId, long attrId, int start, int stop) throws MGXDTOException {
        String[] resolve = r.resolve(GeneObservationDTO.class, "delete");
        resolve = Arrays.copyOf(resolve, resolve.length + 4);
        resolve[resolve.length - 4] = String.valueOf(seqId);
        resolve[resolve.length - 3] = String.valueOf(attrId);
        resolve[resolve.length - 2] = String.valueOf(start);
        resolve[resolve.length - 1] = String.valueOf(stop);
        delete(resolve);
    }
}
