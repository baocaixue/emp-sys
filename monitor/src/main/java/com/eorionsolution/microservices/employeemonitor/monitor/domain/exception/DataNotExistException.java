package com.eorionsolution.microservices.employeemonitor.monitor.domain.exception;

public class DataNotExistException extends Exception{

    private static final long serialVersionUID = -4787953868618709865L;

    public DataNotExistException() {
        super();
    }

    public DataNotExistException(String message) {
        super(message);
    }

    public DataNotExistException(String message, Throwable cause) {
        super(message, cause);
    }

    public DataNotExistException(Throwable cause) {
        super(cause);
    }

    protected DataNotExistException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
