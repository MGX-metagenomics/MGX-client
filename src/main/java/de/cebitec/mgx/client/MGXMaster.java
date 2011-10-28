package de.cebitec.mgx.client;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import de.cebitec.mgx.client.access.rest.AccessBase;
import de.cebitec.mgx.client.access.rest.AttributeAccess;
import de.cebitec.mgx.client.access.rest.DNAExtractAccess;
import de.cebitec.mgx.client.access.rest.HabitatAccess;
import de.cebitec.mgx.client.access.rest.JobAccess;
import de.cebitec.mgx.client.access.rest.ProjectAccess;
import de.cebitec.mgx.client.access.rest.SampleAccess;
import de.cebitec.mgx.client.access.rest.SeqRunAccess;
import de.cebitec.mgx.client.access.rest.SequenceAccess;
import de.cebitec.mgx.client.access.rest.ToolAccess;
import de.cebitec.mgx.client.data.Project;
import de.cebitec.mgx.client.data.User;
import de.cebitec.mgx.dto.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sjaenick
 */
public class MGXMaster {

    private User user;
    private String baseuri;
    private Project project;
    private Client client = null;
    private WebResource res;
    private static final Logger logger = Logger.getLogger("MGX");
    private Map<Class, AccessBase> accessors;

    public MGXMaster(User u, String baseuri) {
        this.user = u;
        this.baseuri = baseuri;
        accessors = new HashMap<Class, AccessBase>();

        // register serializers for protocol buffers and create client
        ClientConfig cc = new DefaultClientConfig();
        cc.getClasses().add(de.cebitec.mgx.dtoserializer.PBReader.class);
        cc.getClasses().add(de.cebitec.mgx.dtoserializer.PBWriter.class);
        client = Client.create(cc);
        client.addFilter( new HTTPBasicAuthFilter( u.getName(), new String(u.getPassword()) ) );
    }

    public void setProject(Project project) {
        this.project = project;
        res = client.resource(new StringBuffer(baseuri).append(project.getName()).toString());
    }

    public Project getProject() {
        return project;
    }

    public String getLogin() {
        return user.getName();
    }

    public WebResource getResource() {
        return res;
    }

    public HabitatAccess<HabitatDTO, HabitatDTOList> Habitat() {
        return getAccessor(HabitatAccess.class);
    }

    public AttributeAccess<AttributeDTO, AttributeDTOList> Attribute() {
        return getAccessor(AttributeAccess.class);
    }

    public SampleAccess<SampleDTO, SampleDTOList> Sample() {
        return getAccessor(SampleAccess.class);
    }

    public DNAExtractAccess<DNAExtractDTO, DNAExtractDTOList> DNAExtract() {
        return getAccessor(DNAExtractAccess.class);
    }

    public SeqRunAccess<SeqRunDTO, SeqRunDTOList> SeqRun() {
        return getAccessor(SeqRunAccess.class);
    }

    public SequenceAccess<SequenceDTO, SequenceDTOList> Sequence() {
        return getAccessor(SequenceAccess.class);
    }

    public ToolAccess<ToolDTO, ToolDTOList> Tool() {
        return getAccessor(ToolAccess.class);
    }

    public JobAccess<JobDTO, JobDTOList> Job() {
        return getAccessor(JobAccess.class);
    }

    public ProjectAccess<MembershipDTO, MembershipDTOList> Project() {
        return getAccessor(ProjectAccess.class);
    }

    public void log(Level lvl, String msg) {
        logger.log(lvl, msg);
    }

    private <T extends AccessBase> T getAccessor(Class<T> clazz) {
        if (!accessors.containsKey(clazz)) {
            accessors.put(clazz, createDAO(clazz));
        }
        return (T) accessors.get(clazz);
    }

    private <T extends AccessBase> T createDAO(Class<T> clazz) {
        try {
            Constructor<T> ctor = clazz.getConstructor();
            T instance = ctor.newInstance();
            instance.setMaster(this);
            return instance;
        } catch (InstantiationException ex) {
            logger.log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            logger.log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            logger.log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            logger.log(Level.SEVERE, null, ex);
        } catch (NoSuchMethodException ex) {
            logger.log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
        throw new UnsupportedOperationException("Could not create accessor for " + clazz);
    }
}
