package com.github.zlamb1.io;

public class DeserializeException extends Exception {
    public DeserializeException(String message) {
        super(message);
    }

    public DeserializeException(String message, Throwable cause) {
        super(message, cause);
    }
}
