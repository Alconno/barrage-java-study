package com.setronica.eventing.exceptions;

public class OrderInProcess extends BaseApplicationException {
    public OrderInProcess() {
    }

    public OrderInProcess(String message) {
        super(message);
    }

    public OrderInProcess(String message, Throwable cause) {
        super(message, cause);
    }

    public OrderInProcess(Throwable cause) {
        super(cause);
    }

    public OrderInProcess(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}