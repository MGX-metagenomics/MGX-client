package de.cebitec.mgx.client.access.rest;

import de.cebitec.mgx.client.data.Project;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.dto.MembershipDTO;
import de.cebitec.mgx.dto.MembershipDTOList;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author sjaenick
 */
public class ProjectAccess<T, U> extends AccessBase<T, U> {

    @Override
    Class getType() {
        return MembershipDTO.class;
    }

    @Override
    Class getListType() {
        return MembershipDTOList.class;
    }

    public Collection<MembershipDTO> fetchall() throws MGXServerException {
        Project save = master.getProject();
        master.setProject(new Project("GPMS"));
        List<MembershipDTO> membershipList = get("/Project/fetchall/", MembershipDTOList.class).getMembershipList();
        master.setProject(save);
        return membershipList;
    }
}
