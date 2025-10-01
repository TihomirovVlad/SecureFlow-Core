package com.yotsume.exeptions;

public class TransferException extends RuntimeException {
    public TransferException(String message) {
        super(message);
    }

    public TransferException(String message, Throwable cause) {
        super(message, cause);
    }
}