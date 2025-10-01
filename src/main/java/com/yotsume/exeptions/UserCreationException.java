package com.yotsume.exeptions;

public class UserCreationException extends RuntimeException {
    public UserCreationException(String message) {
        super(message);
    }
    public UserCreationException(String message, Throwable cause) {
        super(message, cause);
    }
}
