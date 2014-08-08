/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.cebitec.mgx.client.access.rest;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 *
 * @author sjaenick
 */
class PropCounter implements PropertyChangeListener {
    private int cnt = 0;
    private PropertyChangeEvent last = null;

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        //System.err.println("  " + evt.getPropertyName());
        last = evt;
        cnt++;
    }

    public int getCount() {
        return cnt;
    }

    public PropertyChangeEvent getLastEvent() {
        return last;
    }
    
}
