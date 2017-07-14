/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.client.exception;

/**
 *
 * @author sj
 */
public class MGXDTOTimeoutException extends MGXDTOException {

    public MGXDTOTimeoutException(String message) {
        super(message);
    }

    public MGXDTOTimeoutException(Throwable cause) {
        super(cause);
    }
    
}
