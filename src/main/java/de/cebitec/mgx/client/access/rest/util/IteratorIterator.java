/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.client.access.rest.util;

import java.util.Iterator;

/**
 *
 * @author sj
 */
public class IteratorIterator<T> implements Iterator<T> {

    private final Iterator<T>[] iters;
    private T elem = null;
    private int cur = 0;

    @SafeVarargs
    public IteratorIterator(Iterator<T>... iters) {
        this.iters = iters;
    }

    @Override
    public boolean hasNext() {
        if (elem != null) {
            return true;
        }

        while (iters != null && cur < iters.length) {
            if (iters[cur] != null && iters[cur].hasNext()) {
                elem = iters[cur].next();
                return true;
            } else {
                cur++;
            }
        }
        
        return false;
    }

    @Override
    public T next() {
        T ret = elem;
        elem = null;
        return ret;
    }

}
