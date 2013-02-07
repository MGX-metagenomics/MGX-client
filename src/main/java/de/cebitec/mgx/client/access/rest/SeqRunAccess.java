package de.cebitec.mgx.client.access.rest;

import de.cebitec.mgx.client.exception.MGXClientException;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.dto.dto.JobAndAttributeTypes;
import de.cebitec.mgx.dto.dto.JobsAndAttributeTypesDTO;
import de.cebitec.mgx.dto.dto.SeqRunDTO;
import de.cebitec.mgx.dto.dto.SeqRunDTOList;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author sjaenick
 */
public class SeqRunAccess extends AccessBase<SeqRunDTO, SeqRunDTOList> {

    @Override
    public Collection<SeqRunDTO> fetchall() throws MGXServerException, MGXClientException {
        return fetchlist(SeqRunDTOList.class).getSeqrunList();
    }
    
    @Override
    public SeqRunDTO fetch(long id) throws MGXServerException, MGXClientException {
        return super.fetch(id, SeqRunDTO.class);
    }

    public Collection<SeqRunDTO> ByExtract(long extract_id) throws MGXServerException, MGXClientException {
        return get(r.resolve(SeqRunDTO.class, "byExtract") + extract_id, SeqRunDTOList.class).getSeqrunList();
    }

    @Override
    public long create(SeqRunDTO sr) throws MGXServerException, MGXClientException {
        return super.create(sr, SeqRunDTO.class);
    }

    @Override
    public void update(SeqRunDTO d) throws MGXServerException, MGXClientException {
        super.update(d, SeqRunDTO.class);
    }

    @Override
    public boolean delete(long id) throws MGXServerException, MGXClientException {
        super.delete(id, SeqRunDTO.class);
        return true;
    }

    public List<JobAndAttributeTypes> getJobsAndAttributeTypes(long seqrun_id) throws MGXServerException, MGXClientException {
        return get(r.resolve(SeqRunDTO.class, "JobsAndAttributeTypes") + seqrun_id, JobsAndAttributeTypesDTO.class).getEntryList();
    }
}
