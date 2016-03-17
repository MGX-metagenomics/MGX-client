package de.cebitec.mgx.client;

import de.cebitec.gpms.core.DataSourceI;
import de.cebitec.gpms.core.DataSource_ApplicationServerI;
import de.cebitec.gpms.core.MasterI;
import de.cebitec.gpms.core.ProjectI;
import de.cebitec.gpms.core.RoleI;
import de.cebitec.gpms.rest.RESTAccessI;
import de.cebitec.gpms.rest.RESTMasterI;
import de.cebitec.mgx.client.access.rest.*;
import de.cebitec.mgx.client.exception.MGXClientException;
import de.cebitec.mgx.pevents.ParallelPropertyChangeSupport;
import de.cebitec.mgx.restgpms.Jersey1RESTAccess;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sjaenick
 */
public class MGXDTOMaster implements PropertyChangeListener {

    public final static String PROP_LOGGEDIN = "mgxdtomaster_loggedInState";

    private final RESTMasterI restmaster;
    private final RoleI role;
    private String login;
    private RESTAccessI restAccess;
    private static final Logger logger = Logger.getLogger("MGXDTOMaster");
    private final PropertyChangeSupport pcs = new ParallelPropertyChangeSupport(this, true);

    public MGXDTOMaster(RESTMasterI restmaster) {
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

        restmaster.addPropertyChangeListener(this);
        restAccess = new Jersey1RESTAccess(restmaster.getUser(), appServer, false);
    }

    public void logout() {
        restmaster.logout();
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

    public final String getServerName() {
        return restmaster.getServerName();
    }

    public HabitatAccess Habitat() throws MGXClientException {
        if (restAccess == null) {
            throw new MGXClientException("You are logged out.");
        }
        return new HabitatAccess(restAccess);
    }

    public AttributeAccess Attribute() throws MGXClientException {
        if (restAccess == null) {
            throw new MGXClientException("You are logged out.");
        }
        return new AttributeAccess(restAccess);
    }

    public AttributeTypeAccess AttributeType() throws MGXClientException {
        if (restAccess == null) {
            throw new MGXClientException("You are logged out.");
        }
        return new AttributeTypeAccess(restAccess);
    }

    public SampleAccess Sample() throws MGXClientException {
        if (restAccess == null) {
            throw new MGXClientException("You are logged out.");
        }
        return new SampleAccess(restAccess);
    }

    public DNAExtractAccess DNAExtract() throws MGXClientException {
        if (restAccess == null) {
            throw new MGXClientException("You are logged out.");
        }
        return new DNAExtractAccess(restAccess);
    }

    public SeqRunAccess SeqRun() throws MGXClientException {
        if (restAccess == null) {
            throw new MGXClientException("You are logged out.");
        }
        return new SeqRunAccess(restAccess);
    }

    public ReferenceAccess Reference() throws MGXClientException {
        if (restAccess == null) {
            throw new MGXClientException("You are logged out.");
        }
        return new ReferenceAccess(restAccess);
    }

    public MappingAccess Mapping() throws MGXClientException {
        if (restAccess == null) {
            throw new MGXClientException("You are logged out.");
        }
        return new MappingAccess(restAccess);
    }

    public SequenceAccess Sequence() throws MGXClientException {
        if (restAccess == null) {
            throw new MGXClientException("You are logged out.");
        }
        return new SequenceAccess(restAccess);
    }

    public ToolAccess Tool() throws MGXClientException {
        if (restAccess == null) {
            throw new MGXClientException("You are logged out.");
        }
        return new ToolAccess(restAccess);
    }

    public JobAccess Job() throws MGXClientException {
        if (restAccess == null) {
            throw new MGXClientException("You are logged out.");
        }
        return new JobAccess(restAccess);
    }

    public ObservationAccess Observation() throws MGXClientException {
        if (restAccess == null) {
            throw new MGXClientException("You are logged out.");
        }
        return new ObservationAccess(restAccess);
    }

    public FileAccess File() throws MGXClientException {
        if (restAccess == null) {
            throw new MGXClientException("You are logged out.");
        }
        return new FileAccess(restAccess);
    }

    public TermAccess Term() throws MGXClientException {
        if (restAccess == null) {
            throw new MGXClientException("You are logged out.");
        }
        return new TermAccess(restAccess);
    }

    public TaskAccess Task() throws MGXClientException {
        if (restAccess == null) {
            throw new MGXClientException("You are logged out.");
        }
        return new TaskAccess(restAccess);
    }

    public StatisticsAccess Statistics() throws MGXClientException {
        if (restAccess == null) {
            throw new MGXClientException("You are logged out.");
        }
        return new StatisticsAccess(restAccess);
    }

    void log(Level lvl, String msg) {
        logger.log(lvl, msg);
    }

    @Override
    public synchronized void propertyChange(PropertyChangeEvent evt) {
        if (restmaster.equals(evt.getSource()) && evt.getPropertyName().equals(MasterI.PROP_LOGGEDIN)) {
            restmaster.removePropertyChangeListener(this);
            login = null;
            restAccess = null;
            pcs.firePropertyChange(new PropertyChangeEvent(this, PROP_LOGGEDIN, evt.getOldValue(), evt.getNewValue()));
        }
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + Objects.hashCode(this.restmaster);
        hash = 37 * hash + Objects.hashCode(this.role);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final MGXDTOMaster other = (MGXDTOMaster) obj;
        if (!Objects.equals(this.restmaster, other.restmaster)) {
            return false;
        }
        if (!Objects.equals(this.role, other.role)) {
            return false;
        }
        return true;
    }

}
