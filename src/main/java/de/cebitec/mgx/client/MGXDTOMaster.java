package de.cebitec.mgx.client;

import de.cebitec.gpms.core.DataSourceI;
import de.cebitec.gpms.core.DataSource_ApplicationServerI;
import de.cebitec.gpms.core.MasterI;
import de.cebitec.gpms.core.ProjectI;
import de.cebitec.gpms.core.RoleI;
import de.cebitec.gpms.rest.RESTAccessI;
import de.cebitec.mgx.client.access.rest.*;
import de.cebitec.mgx.pevents.ParallelPropertyChangeSupport;
import de.cebitec.mgx.restgpms.Jersey1RESTAccess;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sjaenick
 */
public class MGXDTOMaster {

    private final MasterI restmaster;
    private final RoleI role;
    private final String login;
    private RESTAccessI restAccess;
    private static final Logger logger = Logger.getLogger("MGXDTOMaster");
    private final PropertyChangeSupport pcs = new ParallelPropertyChangeSupport(this, true);

    public MGXDTOMaster(MasterI restmaster) {
        this.restmaster = restmaster;
        this.role = restmaster.getRole();
        this.login = restmaster.getUser().getLogin();
        DataSource_ApplicationServerI appServer = null;
        for (DataSourceI rds : restmaster.getProject().getDataSources()) {
            if (rds instanceof DataSource_ApplicationServerI) {
                appServer = (DataSource_ApplicationServerI) rds;
                break;
            }
        }
        if (appServer == null) {
            throw new RuntimeException("No suitable REST application server found.");
        }
        restAccess = new Jersey1RESTAccess(restmaster.getUser(), appServer, false);
    }

    public ProjectI getProject() {
        return restmaster.getProject();
    }

    public RoleI getRole() {
        return role;
    }

    public final String getLogin() {
        return login;
    }

    public HabitatAccess Habitat() {
        return new HabitatAccess(restAccess);
    }

    public AttributeAccess Attribute() {
        return new AttributeAccess(restAccess);
    }

    public AttributeTypeAccess AttributeType() {
        return new AttributeTypeAccess(restAccess);
    }

    public SampleAccess Sample() {
        return new SampleAccess(restAccess);
    }

    public DNAExtractAccess DNAExtract() {
        return new DNAExtractAccess(restAccess);
    }

    public SeqRunAccess SeqRun() {
        return new SeqRunAccess(restAccess);
    }

    public ReferenceAccess Reference() {
        return new ReferenceAccess(restAccess);
    }

    public MappingAccess Mapping() {
        return new MappingAccess(restAccess);
    }

    public SequenceAccess Sequence() {
        return new SequenceAccess(restAccess);
    }

    public ToolAccess Tool() {
        return new ToolAccess(restAccess);
    }

    public JobAccess Job() {
        return new JobAccess(restAccess);
    }

    public ObservationAccess Observation() {
        return new ObservationAccess(restAccess);
    }

    public FileAccess File() {
        return new FileAccess(restAccess);
    }

    public TermAccess Term() {
        return new TermAccess(restAccess);
    }

    public TaskAccess Task() {
        return new TaskAccess(restAccess);
    }

    public StatisticsAccess Statistics() {
        return new StatisticsAccess(restAccess);
    }

    void log(Level lvl, String msg) {
        logger.log(lvl, msg);
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
