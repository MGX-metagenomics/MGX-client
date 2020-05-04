
package de.cebitec.mgx.client.exception;

/**
 *
 * @author sjaenick
 */
public class MGXClientLoggedOutException extends MGXClientException {

    public MGXClientLoggedOutException() {
        super("MGX client is disconnected.");
    }
}
