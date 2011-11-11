package de.cebitec.mgx.client;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import de.cebitec.gpms.core.MembershipI;
import de.cebitec.gpms.core.ProjectI;
import de.cebitec.gpms.rest.GPMSClientI;
import de.cebitec.gpms.rest.RESTMasterI;
import de.cebitec.mgx.client.access.rest.AccessBase;
import de.cebitec.mgx.client.access.rest.AttributeAccess;
import de.cebitec.mgx.client.access.rest.DNAExtractAccess;
import de.cebitec.mgx.client.access.rest.HabitatAccess;
import de.cebitec.mgx.client.access.rest.JobAccess;
import de.cebitec.mgx.client.access.rest.SampleAccess;
import de.cebitec.mgx.client.access.rest.SeqRunAccess;
import de.cebitec.mgx.client.access.rest.SequenceAccess;
import de.cebitec.mgx.client.access.rest.ToolAccess;
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

    private RESTMasterI restmaster;
    private static final Logger logger = Logger.getLogger("MGX");
    private Map<Class, AccessBase> accessors;
    private WebResource wr;

    public MGXMaster(GPMSClientI gpms, MembershipI m) {
        restmaster = gpms.createMaster(m);
        accessors = new HashMap<Class, AccessBase>();

        restmaster.registerSerializer(de.cebitec.mgx.dtoserializer.PBReader.class);
        restmaster.registerSerializer(de.cebitec.mgx.dtoserializer.PBWriter.class);

        StringBuilder projecturi = new StringBuilder(gpms.getBaseURI());
        if (!gpms.getBaseURI().endsWith("/"))
            projecturi.append("/");

        wr = restmaster.getClient().resource(projecturi.append(m.getProject().getName()).toString());
    }

    public ProjectI getProject() {
        return restmaster.getProject();
    }

    public String getLogin() {
        return restmaster.getUser().getLogin();
    }

    WebResource getResource() {
        return wr;
    }

    public HabitatAccess Habitat() {
        return getAccessor(HabitatAccess.class);
    }

    public AttributeAccess Attribute() {
        return getAccessor(AttributeAccess.class);
    }

    public SampleAccess Sample() {
        return getAccessor(SampleAccess.class);
    }

    public DNAExtractAccess DNAExtract() {
        return getAccessor(DNAExtractAccess.class);
    }

    public SeqRunAccess SeqRun() {
        return getAccessor(SeqRunAccess.class);
    }

    public SequenceAccess Sequence() {
        return getAccessor(SequenceAccess.class);
    }

    public ToolAccess Tool() {
        return getAccessor(ToolAccess.class);
    }

    public JobAccess Job() {
        return getAccessor(JobAccess.class);
    }

    void log(Level lvl, String msg) {
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
            instance.setWebResource(wr);
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
