package de.cebitec.mgx.client.access.rest;

import de.cebitec.mgx.client.access.rest.util.RESTPathResolver;
import de.cebitec.mgx.client.exception.MGXClientException;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.dto.dto.MGXLong;
import java.util.Iterator;
import java.util.UUID;

/**
 *
 * @author sjaenick
 * @param <T>
 * @param <U>
 */
public abstract class AccessBase<T, U> extends RESTMethods {

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
        long id = put(dto, MGXLong.class, resolve).getValue();
        return id;
    }

    protected final void update(T dto, Class<T> c) throws MGXServerException, MGXClientException {
        if (dto == null) {
            throw new MGXClientException("Cannot update with null object.");
        }
        String[] resolve = r.resolve(c, "update");
        post(dto, resolve);
    }

    protected final T fetch(long id, Class<T> c) throws MGXServerException, MGXClientException {
        if (id == -1) {
            throw new MGXClientException("Cannot fetch object with invalid identifier.");
        }
        String[] resolve = r.resolve(c, "fetch", String.valueOf(id));
        return get(c, resolve);
    }

    protected U fetchlist(Class<U> c) throws MGXServerException, MGXClientException {
        String[] resolve = r.resolve(c, "fetchall");
        return this.<U>get(c, resolve);
    }

    protected final UUID delete(long id, Class<T> c) throws MGXServerException, MGXClientException {
        if (id == -1) {
            throw new MGXClientException("Cannot delete object with invalid identifier.");
        }
        String s = delete(r.resolve(c, "delete", String.valueOf(id)));
        return UUID.fromString(s);
    }
}
