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
        String resolve = r.resolve(c, "create");
        long id = put(resolve, dto, MGXLong.class).getValue();
        return id;
    }

    protected final void update(T dto, Class<T> c) throws MGXServerException, MGXClientException {
        String resolve = r.resolve(c, "update");
        post(resolve, dto);
    }

    protected final T fetch(long id, Class<T> c) throws MGXServerException, MGXClientException {
        String resolve = r.resolve(c, "fetch");
        return get(resolve + id, c);
    }

    protected U fetchlist(Class<U> c) throws MGXServerException, MGXClientException {
        String resolve = r.resolve(c, "fetchall");
        return this.<U>get(resolve, c);
    }

    protected final UUID delete(long id, Class<T> c) throws MGXServerException, MGXClientException {
        String resolve = r.resolve(c, "delete");
        return UUID.fromString(delete(resolve + id));
    }

//    /*
//     * from http://snippets.dzone.com/posts/show/91
//     */
//    protected static String join(Iterable< ? extends Object> pColl, String separator) {
//        Iterator< ? extends Object> oIter;
//        if (pColl == null || (!(oIter = pColl.iterator()).hasNext())) {
//            return "";
//        }
//        StringBuilder oBuilder = new StringBuilder(String.valueOf(oIter.next()));
//        while (oIter.hasNext()) {
//            oBuilder.append(separator).append(oIter.next());
//        }
//        return oBuilder.toString();
//    }
//
//    protected static List<String> split(String message, String separator) {
//        return new ArrayList<String>(Arrays.asList(message.split(separator)));
//    }
}
