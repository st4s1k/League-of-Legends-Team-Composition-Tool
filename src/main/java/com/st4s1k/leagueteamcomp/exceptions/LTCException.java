package com.st4s1k.leagueteamcomp.exceptions;

import org.slf4j.helpers.MessageFormatter;

public class LTCException extends RuntimeException {
    private LTCException(String message) {
        super(message);
    }

    public static LTCException of(String message) {
        return new LTCException(message);
    }

    public static LTCException of(String formattedMessage, Object... arguments) {
        return new LTCException(MessageFormatter.arrayFormat(formattedMessage, arguments).getMessage());
    }
}
