/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.client.access.rest.util;

import de.cebitec.mgx.client.MGXDTOMaster;
import de.cebitec.mgx.dto.dto.MappedSequenceDTO;
import java.util.Iterator;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import javax.swing.SwingWorker;

/**
 *
 * @author sj
 */
public class MapFetcher extends SwingWorker<Iterator<MappedSequenceDTO>, Void> {
    private final CountDownLatch latch;
    private final MGXDTOMaster master;
    private final UUID session;

    public MapFetcher(CountDownLatch latch, MGXDTOMaster master, UUID session) {
        this.latch = latch;
        this.master = master;
        this.session = session;
    }

    @Override
    protected Iterator<MappedSequenceDTO> doInBackground() throws Exception {
        latch.await();
        return master.Mapping().byReferenceInterval(session, 0, 500000);
    }
    
}
