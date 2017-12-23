/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zw.mohcc.gitclient;

import java.io.IOException;

/**
 *
 * @author cliffordc
 */
public class GitProcessingFailedException extends Exception {

    public GitProcessingFailedException() {
    }

    public GitProcessingFailedException(String message) {
        super(message);
    }

    public GitProcessingFailedException(Throwable cause) {
        super(cause);
    }

    public GitProcessingFailedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }  
}
