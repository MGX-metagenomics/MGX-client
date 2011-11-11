package de.cebitec.mgx.client.access.rest;

import de.cebitec.mgx.client.exception.MGXClientException;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.dto.MGXLong;
import de.cebitec.mgx.client.access.rest.util.RESTPathResolver;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author sjaenick
 */
public abstract class AccessBase<T, U> extends RESTMethods {

    protected final static RESTPathResolver r = RESTPathResolver.getInstance();

    protected final Long create(T dto, Class<T> c) throws MGXServerException, MGXClientException {
        String resolve = r.resolve(c, "create");
        return put(resolve, dto, MGXLong.class).getValue();
    }

    protected final void update(T dto, Class<T> c) throws MGXServerException, MGXClientException {
        String resolve = r.resolve(c, "update");
        post(resolve, dto);
    }

    protected final T fetch(Long id, Class<T> c) throws MGXServerException, MGXClientException {
        String resolve = r.resolve(c, "fetch");
        return (T) get(resolve + id, c);
    }

    protected U fetchlist(Class<U> c) throws MGXServerException, MGXClientException {
        String resolve = r.resolve(c, "fetchall");
        return this.<U>get(resolve, c);
    }

    protected final void delete(Long id, Class<T> c) throws MGXServerException, MGXClientException {
        String resolve = r.resolve(c, "delete");
        delete(resolve + id);
    }

    /*
     * from http://snippets.dzone.com/posts/show/91
     */
    protected static String join(Iterable< ? extends Object> pColl, String separator) {
        Iterator< ? extends Object> oIter;
        if (pColl == null || (!(oIter = pColl.iterator()).hasNext())) {
            return "";
        }
        StringBuilder oBuilder = new StringBuilder(String.valueOf(oIter.next()));
        while (oIter.hasNext()) {
            oBuilder.append(separator).append(oIter.next());
        }
        return oBuilder.toString();
    }

    protected static List<String> split(String message, String separator) {
        return new ArrayList<String>(Arrays.asList(message.split(separator)));
    }
}
