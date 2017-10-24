package nl.programon.ias.microservice.client;

public class CorrelationValidationException extends Exception{
    public CorrelationValidationException() {
        super();
    }

    public CorrelationValidationException(String message) {
        super(message);
    }

    public CorrelationValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public CorrelationValidationException(Throwable cause) {
        super(cause);
    }

    protected CorrelationValidationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
