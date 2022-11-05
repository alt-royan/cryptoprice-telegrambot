package com.github.cryptoprice.cryptopricetelegrambot.exception;

import com.github.cryptoprice.cryptopricetelegrambot.model.enums.Exchange;
import lombok.Getter;

@Getter
public class NoCoinOnExchangeException extends Exception {

    private final String coinCode;
    private final Exchange exchange;
    private final Integer editableMessageId;

    public NoCoinOnExchangeException(String coinCode, Exchange exchange) {
        super();
        this.coinCode = coinCode;
        this.exchange = exchange;
        this.editableMessageId = null;
    }

    public NoCoinOnExchangeException(String coinCode, Exchange exchange, Integer editableMessageId) {
        super();
        this.coinCode = coinCode;
        this.exchange = exchange;
        this.editableMessageId = editableMessageId;
    }
}
