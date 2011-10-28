package de.cebitec.mgx.client.access.rest;

import de.cebitec.mgx.client.MGXMaster;
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

    public final void setMaster(MGXMaster m) {
        this.master = m;
    }

    protected final MGXMaster getMaster() {
        return master;
    }

    abstract Class getType();

    abstract Class getListType();

    public final Long create(T dto) throws MGXServerException, MGXClientException {
        String resolve = r.resolve(getType(), "create");
        return put(resolve, dto, MGXLong.class).getValue();
    }

    public final void update(T dto) throws MGXServerException, MGXClientException {
        String resolve = r.resolve(getType(), "update");
        post(resolve, dto);
    }

    public final T fetch(Long id) throws MGXServerException, MGXClientException {
        String resolve = r.resolve(getType(), "fetch");
        return (T) get(resolve + id, getType());
    }

    public final U fetchlist() throws MGXServerException, MGXClientException {
        String resolve = r.resolve(getType(), "fetchall");
        return (U) get(resolve, getListType());
    }

    public final void delete(Long id) throws MGXServerException, MGXClientException {
        String resolve = r.resolve(getType(), "delete");
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
