/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.client.access.rest.util;

import de.cebitec.mgx.client.MGXDTOMaster;
import de.cebitec.mgx.client.exception.MGXDTOException;
import de.cebitec.mgx.dto.dto.MappedSequenceDTO;
import java.util.Iterator;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 *
 * @author sj
 */
public class MapFetcher implements RunnableFuture<Iterator<MappedSequenceDTO>> {

    private final CountDownLatch latch;
    private final CountDownLatch processed = new CountDownLatch(1);
    private final MGXDTOMaster master;
    private final UUID session;
    private Iterator<MappedSequenceDTO> iter;
    private boolean cancelled = false;
    private Exception ex = null;

    public MapFetcher(CountDownLatch latch, MGXDTOMaster master, UUID session) {
        this.latch = latch;
        this.master = master;
        this.session = session;
    }

    @Override
    public void run() {
        try {
            latch.await();
            iter = master.Mapping().byReferenceInterval(session, 0, 500000);
        } catch (MGXDTOException | InterruptedException ex) {
            this.ex = ex;
        } finally {
            processed.countDown();
        }
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        cancelled = true;
        return !isDone();
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public boolean isDone() {
        return processed.getCount() == 0;
    }

    @Override
    public Iterator<MappedSequenceDTO> get() throws InterruptedException, ExecutionException {
        processed.await();
        if (ex != null) {
            if (ex instanceof InterruptedException) {
                throw (InterruptedException)ex;
            } else {
                throw new ExecutionException(ex);
            }
        }
        return iter;
    }

    @Override
    public Iterator<MappedSequenceDTO> get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return get();
    }

}
