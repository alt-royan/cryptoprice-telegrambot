package com.github.cryptoprice.cryptopricetelegrambot.exception;

import lombok.Getter;

/**
 * Exception throws when receives error from exchange server
 */
@Getter
public class ExchangeServerException extends CommonException {

    private final String message = "Ошибка на сервере биржи. Попробуйте позже";
    private final int statusCode;

    public ExchangeServerException() {
        super();
        this.statusCode = 0;
    }

    public ExchangeServerException(int statusCode) {
        super();
        this.statusCode = statusCode;
    }
}
