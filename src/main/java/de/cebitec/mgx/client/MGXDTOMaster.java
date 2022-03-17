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
import de.cebitec.mgx.client.exception.MGXClientLoggedOutException;
import de.cebitec.mgx.pevents.ParallelPropertyChangeSupport;
import de.cebitec.mgx.restgpms.JAXRSRESTAccess;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sjaenick
 */
public class MGXDTOMaster implements PropertyChangeListener {

    public final static String PROP_LOGGEDIN = "mgxdtomaster_loggedInState";

    private volatile RESTMasterI restmaster;
    private final RoleI role;
    private String login;
    private final RESTAccessI restAccess;
    private static final Logger logger = Logger.getLogger("MGXDTOMaster");
    private final ParallelPropertyChangeSupport pcs = new ParallelPropertyChangeSupport(this, true);

    //
    private HabitatAccess habitatAccess = null;
    private SampleAccess sampleAccess = null;
    private DNAExtractAccess extractAccess = null;
    private SeqRunAccess runAccess = null;
    private ToolAccess toolAccess = null;
    private JobAccess jobAccess = null;

    public MGXDTOMaster(RESTMasterI restmaster) {
        this.restmaster = restmaster;
        this.role = restmaster.getRole();
        this.login = restmaster.getUser() != null ? restmaster.getUser().getLogin() : null;
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
        restAccess = new JAXRSRESTAccess(restmaster.getUser(), appServer.getURL(), restmaster.validateSSL());
    }

    public void close() {
        if (restmaster != null) {
            restmaster.removePropertyChangeListener(this);
            pcs.firePropertyChange(new PropertyChangeEvent(this, PROP_LOGGEDIN, true, false));
            pcs.close();
            restmaster = null;
            login = null;
            try {
                restAccess.close();
            } catch (IOException ex) {
            }
        }
    }

    public boolean isClosed() {
        return restmaster == null || restAccess.isClosed();
    }

    public ProjectI getProject() {
        return restmaster != null ? restmaster.getProject() : null;
    }

    public RoleI getRole() {
        return role;
    }

    public final String getLogin() {
        return login;
    }

    public final String getServerName() {
        return restmaster != null ? restmaster.getServerName() : null;
    }

    /*
     * read-based support
     */
    public HabitatAccess Habitat() throws MGXClientException {
        if (restAccess == null) {
            throw new MGXClientLoggedOutException();
        }
        if (habitatAccess == null) {
            habitatAccess = new HabitatAccess(restAccess);
        }
        return habitatAccess;
    }

    public AttributeAccess Attribute() throws MGXClientException {
        if (restAccess == null) {
            throw new MGXClientLoggedOutException();
        }
        return new AttributeAccess(restAccess);
    }

    public AttributeTypeAccess AttributeType() throws MGXClientException {
        if (restAccess == null) {
            throw new MGXClientLoggedOutException();
        }
        return new AttributeTypeAccess(restAccess);
    }

    public SampleAccess Sample() throws MGXClientException {
        if (restAccess == null) {
            throw new MGXClientLoggedOutException();
        }
        if (sampleAccess == null) {
            sampleAccess = new SampleAccess(restAccess);
        }
        return sampleAccess;
    }

    public DNAExtractAccess DNAExtract() throws MGXClientException {
        if (restAccess == null) {
            throw new MGXClientLoggedOutException();
        }
        if (extractAccess == null) {
            extractAccess = new DNAExtractAccess(restAccess);
        }
        return extractAccess;
    }

    public SeqRunAccess SeqRun() throws MGXClientException {
        if (restAccess == null) {
            throw new MGXClientLoggedOutException();
        }
        if (runAccess == null) {
            runAccess = new SeqRunAccess(restAccess);
        }
        return runAccess;
    }

    public ReferenceAccess Reference() throws MGXClientException {
        if (restAccess == null) {
            throw new MGXClientLoggedOutException();
        }
        return new ReferenceAccess(this, restAccess);
    }

    public ReferenceRegionAccess ReferenceRegion() throws MGXClientException {
        if (restAccess == null) {
            throw new MGXClientLoggedOutException();
        }
        return new ReferenceRegionAccess(this, restAccess);
    }

    public MappingAccess Mapping() throws MGXClientException {
        if (restAccess == null) {
            throw new MGXClientLoggedOutException();
        }
        return new MappingAccess(this, restAccess);
    }

    public SequenceAccess Sequence() throws MGXClientException {
        if (restAccess == null) {
            throw new MGXClientLoggedOutException();
        }
        return new SequenceAccess(this, restAccess);
    }

    public ToolAccess Tool() throws MGXClientException {
        if (restAccess == null) {
            throw new MGXClientLoggedOutException();
        }
        if (toolAccess == null) {
            toolAccess = new ToolAccess(restAccess);
        }
        return toolAccess;
    }

    public JobAccess Job() throws MGXClientException {
        if (restAccess == null) {
            throw new MGXClientLoggedOutException();
        }
        if (jobAccess == null) {
            jobAccess = new JobAccess(restAccess);
        }
        return jobAccess;
    }

    public ObservationAccess Observation() throws MGXClientException {
        if (restAccess == null) {
            throw new MGXClientLoggedOutException();
        }
        return new ObservationAccess(restAccess);
    }

    public FileAccess File() throws MGXClientException {
        if (restAccess == null) {
            throw new MGXClientLoggedOutException();
        }
        return new FileAccess(this, restAccess);
    }

    public TermAccess Term() throws MGXClientException {
        if (restAccess == null) {
            throw new MGXClientLoggedOutException();
        }
        return new TermAccess(restAccess);
    }

    public TaskAccess Task() throws MGXClientException {
        if (restAccess == null) {
            throw new MGXClientLoggedOutException();
        }
        return new TaskAccess(restAccess);
    }

    public StatisticsAccess Statistics() throws MGXClientException {
        if (restAccess == null) {
            throw new MGXClientLoggedOutException();
        }
        return new StatisticsAccess(restAccess);
    }

    /*
     * metagenome assembly support
     */
    public AssemblyAccess Assembly() throws MGXClientException {
        if (restAccess == null) {
            throw new MGXClientLoggedOutException();
        }
        return new AssemblyAccess(restAccess);
    }

    public BinAccess Bin() throws MGXClientException {
        if (restAccess == null) {
            throw new MGXClientLoggedOutException();
        }
        return new BinAccess(this, restAccess);
    }

    public ContigAccess Contig() throws MGXClientException {
        if (restAccess == null) {
            throw new MGXClientLoggedOutException();
        }
        return new ContigAccess(this, restAccess);
    }

    public AssembledRegionAccess AssembledRegion() throws MGXClientException {
        if (restAccess == null) {
            throw new MGXClientLoggedOutException();
        }
        return new AssembledRegionAccess(this, restAccess);
    }

    public GeneCoverageAccess GeneCoverage() throws MGXClientException {
        if (restAccess == null) {
            throw new MGXClientLoggedOutException();
        }
        return new GeneCoverageAccess(restAccess);
    }

    public GeneObservationAccess GeneObservation() throws MGXClientException {
        if (restAccess == null) {
            throw new MGXClientLoggedOutException();
        }
        return new GeneObservationAccess(restAccess);
    }

    void log(Level lvl, String msg) {
        logger.log(lvl, msg);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (restmaster.equals(evt.getSource()) && evt.getPropertyName().equals(MasterI.PROP_LOGGEDIN)) {
            if (evt.getNewValue() instanceof Boolean) {
                Boolean newVal = (Boolean) evt.getNewValue();
                if (!newVal) {
                    close();
                }
            }
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
        return Objects.equals(this.role, other.role);
    }

}
