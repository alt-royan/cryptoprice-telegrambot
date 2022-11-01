package com.github.cryptoprice.cryptopricetelegrambot.exception;

import lombok.Getter;

public class ClientException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    @Getter
    private final int errorCode;

    public ClientException(String message) {
        super(message);
        this.errorCode = 0;
    }

    public ClientException(String message, int errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

}
