package com.github.cryptoprice.cryptopricetelegrambot.exception;

import com.github.cryptoprice.cryptopricetelegrambot.model.enums.Exchange;
import lombok.Getter;

/**
 * Exception throws when try to use crypto which not present on exchange
 */
public class NoCoinOnExchangeException extends CommonException {

    @Getter
    private final String coinCode;

    @Getter
    private final Exchange exchange;

    private final String messageTemplate = "Криптовалюты %s нет на бирже %s. Попробуйте сменить биржу или выберите другую криптовалюту.";

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

    @Override
    public String getMessage() {
        return String.format(messageTemplate, coinCode, exchange.getName());
    }
}
