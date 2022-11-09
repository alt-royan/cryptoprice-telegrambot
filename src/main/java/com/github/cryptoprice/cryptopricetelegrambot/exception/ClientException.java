package com.github.cryptoprice.cryptopricetelegrambot.exception;

import lombok.Getter;

/**
 * Exception throws when receives a client error from exchange
 */
@Getter
public class ClientException extends CommonException {

    private final int errorCode;
    private final String message;

    public ClientException(String message) {
        super();
        this.message = message;
        this.errorCode = 0;
    }

    public ClientException(String message, int errorCode) {
        super();
        this.message = message;
        this.errorCode = errorCode;
    }
}
