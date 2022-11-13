package com.github.cryptoprice.cryptopricetelegrambot.exception;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CurrencyEqualsCodeException extends CommonException {

    private String codeAndCurrency;

    public CurrencyEqualsCodeException(String codeAndCurrency) {
        super();
        this.codeAndCurrency = codeAndCurrency;
    }

    public CurrencyEqualsCodeException(Integer editableMessageId, String codeAndCurrency) {
        super(editableMessageId);
        this.codeAndCurrency = codeAndCurrency;
    }

    private final String messageTemplate = "Криптовалюта и валюта не могут быть одинаковыми. (%s)";

    @Override
    public String getMessage() {
        return String.format(messageTemplate, codeAndCurrency);
    }
}
