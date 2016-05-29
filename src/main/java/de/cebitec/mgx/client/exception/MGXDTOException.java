package de.cebitec.mgx.client.exception;

/**
 *
 * @author sjaenick
 */
public abstract class MGXDTOException extends Exception {

    public MGXDTOException(String message) {
        super(message);
    }

    public MGXDTOException(Throwable cause) {
        super(cause);
    }

}
