package com.github.cryptoprice.cryptopricetelegrambot.exception;


import lombok.Getter;

@Getter
public class NotSupportedCurrencyException extends Exception {


    private final String currency;
    private final Integer editableMessageId;


    public NotSupportedCurrencyException(String currency) {
        super();
        this.currency = currency;
        this.editableMessageId = null;

    }

    public NotSupportedCurrencyException(String currency, Integer editableMessageId) {
        super();
        this.currency = currency;
        this.editableMessageId = editableMessageId;
    }


}
