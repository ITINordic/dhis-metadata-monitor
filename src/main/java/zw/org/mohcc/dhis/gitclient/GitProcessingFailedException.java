/* 
 * Copyright (c) 2018, ITINordic
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
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
