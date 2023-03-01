/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.client.exception;

import java.io.Serial;

/**
 *
 * @author sj
 */
public class MGXDTOTimeoutException extends MGXDTOException {

    @Serial
    private static final long serialVersionUID = 1L;

    public MGXDTOTimeoutException(String message) {
        super(message);
    }

    public MGXDTOTimeoutException(Throwable cause) {
        super(cause);
    }

}
