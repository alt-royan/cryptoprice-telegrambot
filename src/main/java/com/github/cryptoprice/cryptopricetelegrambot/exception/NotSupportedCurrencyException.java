package com.github.cryptoprice.cryptopricetelegrambot.exception;


import lombok.Getter;

@Getter
public class NotSupportedCurrencyException extends EditableMessageException {

    private final String currency;


    public NotSupportedCurrencyException(String currency) {
        super();
        this.currency = currency;
    }

    public NotSupportedCurrencyException(String currency, Integer editableMessageId) {
        super(editableMessageId);
        this.currency = currency;
    }


}
