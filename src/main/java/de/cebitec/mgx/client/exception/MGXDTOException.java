package de.cebitec.mgx.client.exception;

import java.io.Serial;

/**
 *
 * @author sjaenick
 */
public abstract class MGXDTOException extends Exception {

    @Serial
    private static final long serialVersionUID = 1L;

    public MGXDTOException(String message) {
        super(message);
    }

    public MGXDTOException(Throwable cause) {
        super(cause);
    }

}
