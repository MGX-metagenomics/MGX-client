package de.cebitec.mgx.client.exception;

import java.io.Serial;

/**
 *
 * @author sjaenick
 */
public class MGXClientException extends MGXDTOException {

    @Serial
    private static final long serialVersionUID = 1L;

    public MGXClientException(String message) {
        super(message);
    }
}
