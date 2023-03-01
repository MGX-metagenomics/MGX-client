package de.cebitec.mgx.client.exception;

import java.io.Serial;

/**
 *
 * @author sjaenick
 */
public class MGXClientLoggedOutException extends MGXClientException {

    @Serial
    private static final long serialVersionUID = 1L;

    public MGXClientLoggedOutException() {
        super("MGX client is disconnected.");
    }
}
