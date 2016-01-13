package de.cebitec.mgx.client.exception;

/**
 *
 * @author sjaenick
 */
public abstract class MGXException extends Exception {

    public MGXException(String message) {
        super(message);
    }

    public MGXException(Throwable cause) {
        super(cause);
    }

}
