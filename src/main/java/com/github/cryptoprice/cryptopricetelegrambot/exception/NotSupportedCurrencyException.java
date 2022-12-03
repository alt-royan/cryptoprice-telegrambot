package com.github.cryptoprice.cryptopricetelegrambot.exception;


import com.github.cryptoprice.cryptopricetelegrambot.model.enums.CurrencyCounter;

/**
 * Exception throws when try to use unsupported currency
 */
public class NotSupportedCurrencyException extends CommonException {

    private final String currency;
    private final String messageTemplate = "Валюта %s не поддерживается.\nПоддерживаемые валюты:\n\n";


    public NotSupportedCurrencyException(String currency) {
        super();
        this.currency = currency;
    }

    public NotSupportedCurrencyException(String currency, Integer editableMessageId) {
        super(editableMessageId);
        this.currency = currency;
    }


    @Override
    public String getMessage() {
        var result = new StringBuilder(String.format(messageTemplate, currency));
        for (CurrencyCounter value : CurrencyCounter.values()) {
            result.append(value).append(" ");
        }
        return result.toString();
    }
}
