package com.st4s1k.leagueteamcomp.exceptions;

import java.text.MessageFormat;

public class LTCException extends RuntimeException {
    public LTCException(String message) {
        super(message);
    }

    public LTCException(String formattedMessage, Object... arguments) {
        super(MessageFormat.format(formattedMessage, arguments));
    }
}
