package de.cebitec.mgx.client;

import de.cebitec.gpms.core.MembershipI;
import de.cebitec.gpms.core.ProjectI;
import de.cebitec.gpms.rest.GPMSClientI;
import de.cebitec.gpms.rest.RESTMasterI;
import de.cebitec.mgx.client.access.rest.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
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
public class MGXDTOMaster {

    private final RESTMasterI restmaster;
    private final MembershipI membership;
    private static final Logger logger = Logger.getLogger("MGXDTOMaster");
    private final Map<Class, AccessBase> accessors;
    private final String resource;
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    public MGXDTOMaster(GPMSClientI gpms, MembershipI mbr) {
        restmaster = gpms.createMaster(mbr);
        membership = mbr;
        accessors = new HashMap<>();

        restmaster.registerSerializer(de.cebitec.mgx.dtoserializer.PBReader.class);
        restmaster.registerSerializer(de.cebitec.mgx.dtoserializer.PBWriter.class);

        resource = new StringBuilder(gpms.getBaseURI()).append(mbr.getProject().getName()).toString();
    }

    public MembershipI getMembership() {
        return membership;
    }

    public ProjectI getProject() {
        return restmaster.getProject();
    }

    public String getLogin() {
        return restmaster.getUser().getLogin();
    }

    public HabitatAccess Habitat() {
        return getAccessor(HabitatAccess.class);
    }

    public AttributeAccess Attribute() {
        return getAccessor(AttributeAccess.class);
    }

    public AttributeTypeAccess AttributeType() {
        return getAccessor(AttributeTypeAccess.class);
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

    public ReferenceAccess Reference() {
        return getAccessor(ReferenceAccess.class);
    }

    public MappingAccess Mapping() {
        return getAccessor(MappingAccess.class);
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

    public ObservationAccess Observation() {
        return getAccessor(ObservationAccess.class);
    }

    public FileAccess File() {
        return getAccessor(FileAccess.class);
    }

    public TermAccess Term() {
        return getAccessor(TermAccess.class);
    }

    public TaskAccess Task() {
        return getAccessor(TaskAccess.class);
    }
    
    public StatisticsAccess Statistics() {
        return getAccessor(StatisticsAccess.class);
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
//            instance.setWebResource(restmaster.getClient().resource(resource));
            instance.setClient(restmaster.getClient(), resource);
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

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }

    private void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        pcs.firePropertyChange(propertyName, oldValue, newValue);
    }

    private void firePropertyChange(String propertyName, int oldValue, int newValue) {
        pcs.firePropertyChange(propertyName, oldValue, newValue);
    }

    private void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {
        pcs.firePropertyChange(propertyName, oldValue, newValue);
    }

    private void firePropertyChange(PropertyChangeEvent event) {
        pcs.firePropertyChange(event);
    }
}
