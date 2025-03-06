package com.github.zlamb1.assignment6;

public class SpriteLoadException extends RuntimeException {
    public SpriteLoadException(String message) {
        super(message);
    }

    public SpriteLoadException(String message, Throwable cause) {
        super(message, cause);
    }
}
