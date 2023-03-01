package de.cebitec.mgx.client.exception;

import java.io.Serial;

/**
 *
 * @author sjaenick
 */
public class MGXServerException extends MGXDTOException {

    @Serial
    private static final long serialVersionUID = 1L;

    public MGXServerException(String message) {
        super(message);
    }

}
