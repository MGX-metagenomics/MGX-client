package de.cebitec.mgx.client.access.rest;

import de.cebitec.gpms.rest.RESTAccessI;
import de.cebitec.gpms.rest.RESTDisconnectedException;
import de.cebitec.gpms.rest.RESTException;
import de.cebitec.mgx.client.access.rest.util.RESTPathResolver;
import de.cebitec.mgx.client.exception.MGXClientException;
import de.cebitec.mgx.client.exception.MGXClientLoggedOutException;
import de.cebitec.mgx.client.exception.MGXDTOException;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.dto.dto.MGXLong;
import de.cebitec.mgx.dto.dto.MGXString;
import java.util.Iterator;
import java.util.UUID;

/**
 *
 * @author sjaenick
 * @param <T>
 * @param <U>
 */
public abstract class AccessBase<T, U> {

    private final RESTAccessI restAccess;

    public AccessBase(RESTAccessI restAccess) {
        this.restAccess = restAccess;
    }

    protected final RESTAccessI getRESTAccess() throws MGXClientLoggedOutException {
        if (restAccess.isClosed()) {
            throw new MGXClientLoggedOutException();
        }
        return restAccess;
    }

    public final static long INVALID_IDENTIFIER = -1;
    protected final static RESTPathResolver r = RESTPathResolver.getInstance();

    public abstract T fetch(long id) throws MGXDTOException;

    public abstract Iterator<T> fetchall() throws MGXDTOException;

    public abstract long create(T t) throws MGXDTOException;

    public abstract void update(T t) throws MGXDTOException;

    public abstract UUID delete(long id) throws MGXDTOException;

    protected final long create(T dto, Class<T> c) throws MGXDTOException {
        if (dto == null) {
            throw new MGXClientException("Cannot create null object.");
        }
        String[] resolve = r.resolve(c, "create");
        long id;
        try {
            id = getRESTAccess().put(dto, MGXLong.class, resolve).getValue();
        } catch (RESTDisconnectedException rde) {
            throw new MGXClientLoggedOutException();
        } catch (RESTException ex) {
            throw new MGXServerException(ex.getMessage());
        }
        return id;
    }

    protected final void update(T dto, Class<T> c) throws MGXDTOException {
        if (dto == null) {
            throw new MGXClientException("Cannot update with null object.");
        }
        String[] resolve = r.resolve(c, "update");
        try {
            getRESTAccess().post(dto, resolve);
        } catch (RESTDisconnectedException rde) {
            throw new MGXClientLoggedOutException();
        } catch (RESTException ex) {
            throw new MGXServerException(ex.getMessage());
        }
    }

    protected final T fetch(long id, Class<T> c) throws MGXDTOException {
        if (id == -1) {
            throw new MGXClientException("Cannot fetch object with invalid identifier.");
        }
        String[] resolve = r.resolve(c, "fetch", String.valueOf(id));
        try {
            return getRESTAccess().get(c, resolve);
        } catch (RESTDisconnectedException rde) {
            throw new MGXClientLoggedOutException();
        } catch (RESTException ex) {
            throw new MGXServerException(ex.getMessage());
        }
    }

    protected U fetchlist(Class<U> c) throws MGXDTOException {
        String[] resolve = r.resolve(c, "fetchall");
        try {
            return getRESTAccess().<U>get(c, resolve);
        } catch (RESTDisconnectedException rde) {
            throw new MGXClientLoggedOutException();
        } catch (RESTException ex) {
            throw new MGXServerException(ex.getMessage());
        }
    }

    protected final UUID delete(long id, Class<T> c) throws MGXDTOException {
        if (id == -1) {
            throw new MGXClientException("Cannot delete object with invalid identifier.");
        }
        String s;
        try {
            s = getRESTAccess().delete(MGXString.class, r.resolve(c, "delete", String.valueOf(id))).getValue();
        } catch (RESTDisconnectedException rde) {
            throw new MGXClientLoggedOutException();
        } catch (RESTException ex) {
            throw new MGXServerException(ex.getMessage());
        }
        return UUID.fromString(s);
    }

    protected final void put(Object obj, String... path) throws MGXDTOException {
        try {
            getRESTAccess().put(obj, path);
        } catch (RESTDisconnectedException rde) {
            throw new MGXClientLoggedOutException();
        } catch (RESTException ex) {
            throw new MGXServerException(ex.getMessage());
        }
    }

    protected final <U> U put(Object obj, Class<U> c, String... path) throws MGXDTOException {
        try {
            return getRESTAccess().put(obj, c, path);
        } catch (RESTDisconnectedException rde) {
            throw new MGXClientLoggedOutException();
        } catch (RESTException ex) {
            throw new MGXServerException(ex.getMessage());
        }
    }

    protected final void get(String... path) throws MGXDTOException {
        try {
            getRESTAccess().get(path);
        } catch (RESTDisconnectedException rde) {
            throw new MGXClientLoggedOutException();
        } catch (RESTException ex) {
            throw new MGXServerException(ex.getMessage());
        }
    }

    protected final <U> U get(Class<U> c, String... path) throws MGXDTOException {
        try {
            return getRESTAccess().get(c, path);
        } catch (RESTDisconnectedException rde) {
            throw new MGXClientLoggedOutException();
        } catch (RESTException ex) {
            throw new MGXServerException(ex.getMessage());
        }
    }

    protected final <U> U delete(Class<U> clazz, String... path) throws MGXDTOException {
        try {
            return getRESTAccess().delete(clazz, path);
        } catch (RESTDisconnectedException rde) {
            throw new MGXClientLoggedOutException();
        } catch (RESTException ex) {
            throw new MGXServerException(ex.getMessage());
        }
    }

    protected final void delete(String... path) throws MGXDTOException {
        try {
            getRESTAccess().delete(path);
        } catch (RESTDisconnectedException rde) {
            throw new MGXClientLoggedOutException();
        } catch (RESTException ex) {
            throw new MGXServerException(ex.getMessage());
        }
    }

    protected final <U> void post(U obj, String... path) throws MGXDTOException {
        try {
            getRESTAccess().post(obj, path);
        } catch (RESTDisconnectedException rde) {
            throw new MGXClientLoggedOutException();
        } catch (RESTException ex) {
            throw new MGXServerException(ex.getMessage());
        }
    }

}
