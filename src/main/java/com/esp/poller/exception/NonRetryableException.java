package com.esp.poller.exception;

public class NonRetryableException extends RuntimeException {
    public NonRetryableException( String message ) {
        super( message );
    }

    public NonRetryableException( String message, Throwable cause ) {
        super( message, cause );
    }
}
