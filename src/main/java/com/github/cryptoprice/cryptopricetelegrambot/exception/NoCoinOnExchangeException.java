package com.github.cryptoprice.cryptopricetelegrambot.exception;

import com.github.cryptoprice.cryptopricetelegrambot.model.enums.Exchange;
import lombok.Getter;

@Getter
public class NoCoinOnExchangeException extends EditableMessageException {

    private final String coinCode;
    private final Exchange exchange;

    public NoCoinOnExchangeException(String coinCode, Exchange exchange) {
        super();
        this.coinCode = coinCode;
        this.exchange = exchange;
    }

    public NoCoinOnExchangeException(String coinCode, Exchange exchange, Integer editableMessageId) {
        super(editableMessageId);
        this.coinCode = coinCode;
        this.exchange = exchange;
    }
}
