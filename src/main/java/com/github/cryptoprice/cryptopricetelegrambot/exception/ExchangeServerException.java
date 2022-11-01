package com.github.cryptoprice.cryptopricetelegrambot.exception;

import lombok.Getter;

public class ExchangeServerException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    @Getter
    private final int statusCode;

    public ExchangeServerException(String message) {
        super(message);
        this.statusCode = 0;
    }

    public ExchangeServerException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }
}
