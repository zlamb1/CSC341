package com.github.zlamb1.io.gif;

import java.io.IOException;

public class GIFDecodeException extends IOException {
    public enum DecodeCause {
        INVALID_VERSION,
        INVALID_FORMAT,
        MISSING_COLOR_TABLE,
        UNKNOWN,
    }

    protected DecodeCause decodeCause;
    protected String ctx;

    public GIFDecodeException(DecodeCause decodeCause) {
        this(decodeCause, null);
    }

    public GIFDecodeException(DecodeCause decodeCause, String ctx) {
        super(switch (decodeCause) {
            case INVALID_VERSION -> getMessageWithContext("Invalid GIF Version", ctx);
            case INVALID_FORMAT -> getMessageWithContext("Invalid GIF Format", ctx);
            case MISSING_COLOR_TABLE -> getMessageWithContext("Missing Color Table", ctx);
            default -> "Unknown GIF Decode Exception";
        });
    }

    public DecodeCause getDecodeCause() {
        return decodeCause;
    }

    public String getContext() {
        return ctx;
    }

    protected static String getMessageWithContext(String message, String ctx) {
        if (ctx == null) {
            return message;
        } else {
            return message + ": " + ctx;
        }
    }
}
