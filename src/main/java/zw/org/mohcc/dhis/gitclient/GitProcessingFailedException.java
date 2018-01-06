/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zw.org.mohcc.dhis.gitclient;

import java.io.IOException;

/**
 *
 * @author cliffordc
 */
public class GitProcessingFailedException extends Exception {

    public static final String GIT_PROCESSING_FAILED_EXCEPTION_DHIS_MONITOR
            = "GitProcessingFailedException: DHIS Monitor Failed to process git action";

    public GitProcessingFailedException() {
        super(GIT_PROCESSING_FAILED_EXCEPTION_DHIS_MONITOR);
    }

    public GitProcessingFailedException(String message) {
        super(message == null ? GIT_PROCESSING_FAILED_EXCEPTION_DHIS_MONITOR : message);
    }

    public GitProcessingFailedException(Throwable cause) {
        super(GIT_PROCESSING_FAILED_EXCEPTION_DHIS_MONITOR, cause);
    }

    public GitProcessingFailedException(String message, Throwable cause) {
        super(message == null ? GIT_PROCESSING_FAILED_EXCEPTION_DHIS_MONITOR : message, cause);
    }

    public GitProcessingFailedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message == null ? GIT_PROCESSING_FAILED_EXCEPTION_DHIS_MONITOR : message, cause, enableSuppression, writableStackTrace);
    }
}
