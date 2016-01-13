package de.cebitec.mgx.client.access.rest;

import de.cebitec.gpms.rest.RESTAccessI;
import de.cebitec.gpms.rest.RESTException;
import de.cebitec.mgx.client.access.rest.util.RESTPathResolver;
import de.cebitec.mgx.client.exception.MGXClientException;
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

    public final RESTAccessI getRESTAccess() {
        return restAccess;
    }

    public final static long INVALID_IDENTIFIER = -1;
    protected final static RESTPathResolver r = RESTPathResolver.getInstance();

    public abstract T fetch(long id) throws MGXServerException, MGXClientException;

    public abstract Iterator<T> fetchall() throws MGXServerException, MGXClientException;

    public abstract long create(T t) throws MGXServerException, MGXClientException;

    public abstract void update(T t) throws MGXServerException, MGXClientException;

    public abstract UUID delete(long id) throws MGXServerException, MGXClientException;

    protected final long create(T dto, Class<T> c) throws MGXServerException, MGXClientException {
        if (dto == null) {
            throw new MGXClientException("Cannot create null object.");
        }
        String[] resolve = r.resolve(c, "create");
        long id;
        try {
            id = restAccess.put(dto, MGXLong.class, resolve).getValue();
        } catch (RESTException ex) {
            throw new MGXServerException(ex.getMessage());
        }
        return id;
    }

    protected final void update(T dto, Class<T> c) throws MGXServerException, MGXClientException {
        if (dto == null) {
            throw new MGXClientException("Cannot update with null object.");
        }
        String[] resolve = r.resolve(c, "update");
        try {
            restAccess.post(dto, resolve);
        } catch (RESTException ex) {
            throw new MGXServerException(ex.getMessage());
        }
    }

    protected final T fetch(long id, Class<T> c) throws MGXServerException, MGXClientException {
        if (id == -1) {
            throw new MGXClientException("Cannot fetch object with invalid identifier.");
        }
        String[] resolve = r.resolve(c, "fetch", String.valueOf(id));
        try {
            return restAccess.get(c, resolve);
        } catch (RESTException ex) {
            throw new MGXServerException(ex.getMessage());
        }
    }

    protected U fetchlist(Class<U> c) throws MGXServerException, MGXClientException {
        String[] resolve = r.resolve(c, "fetchall");
        try {
            return restAccess.<U>get(c, resolve);
        } catch (RESTException ex) {
            throw new MGXServerException(ex.getMessage());
        }
    }

    protected final UUID delete(long id, Class<T> c) throws MGXServerException, MGXClientException {
        if (id == -1) {
            throw new MGXClientException("Cannot delete object with invalid identifier.");
        }
        String s;
        try {
            s = restAccess.delete(MGXString.class, r.resolve(c, "delete", String.valueOf(id))).getValue();
        } catch (RESTException ex) {
            throw new MGXServerException(ex.getMessage());
        }
        return UUID.fromString(s);
    }

    protected final <U> U put(Object obj, Class<U> c, String... path) throws MGXServerException {
        try {
            return restAccess.put(obj, c, path);
        } catch (RESTException ex) {
            throw new MGXServerException(ex.getMessage());
        }
    }

    protected final void get(String... path) throws MGXServerException {
        try {
            restAccess.get(path);
        } catch (RESTException ex) {
            throw new MGXServerException(ex.getMessage());
        }
    }

    protected final <U> U get(Class<U> c, String... path) throws MGXServerException {
        try {
            return restAccess.get(c, path);
        } catch (RESTException ex) {
            throw new MGXServerException(ex.getMessage());
        }
    }

    protected final <U> U delete(Class<U> clazz, String... path) throws MGXServerException {
        try {
            return restAccess.delete(clazz, path);
        } catch (RESTException ex) {
            throw new MGXServerException(ex.getMessage());
        }
    }

    protected final void delete(String... path) throws MGXServerException {
        try {
            restAccess.delete(path);
        } catch (RESTException ex) {
            throw new MGXServerException(ex.getMessage());
        }
    }

    protected final <U> void post(U obj, String... path) throws MGXServerException {
        try {
            restAccess.post(obj, path);
        } catch (RESTException ex) {
            throw new MGXServerException(ex.getMessage());
        }
    }

}
