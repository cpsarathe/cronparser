package org.loop.test;

public class CronParsingException extends RuntimeException {

    public CronParsingException() {
        super();
    }

    public CronParsingException(String message) {
        super(message);
    }

    public CronParsingException(String message, Throwable t) {
        super(message, t);
    }


}
