package org.loop.test;

public class InvalidCronExpressionException extends RuntimeException {

    public InvalidCronExpressionException() {
        super();
    }

    public InvalidCronExpressionException(String message) {
        super(message);
    }

    public InvalidCronExpressionException(String message, Throwable t) {
        super(message, t);
    }


}
