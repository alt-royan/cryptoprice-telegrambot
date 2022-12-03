package com.github.cryptoprice.cryptopricetelegrambot.exception;

import com.github.cryptoprice.cryptopricetelegrambot.model.enums.Exchange;
import lombok.Getter;
import lombok.Setter;
import org.knowm.xchange.currency.CurrencyPair;

import java.util.ArrayList;
import java.util.List;

/**
 * Exception throws when try to use crypto which not present on exchange
 */
public class NoCoinPairOnExchangeException extends CommonException {

    @Getter
    private final CurrencyPair currencyPair;

    @Getter
    private final Exchange exchange;

    @Getter
    @Setter
    private final List<CurrencyPair> analogues = new ArrayList<>();

    private final String messageTemplate = "Пары %s нет на бирже %s. Попробуйте сменить биржу или выберите другую криптовалюту.";

    public NoCoinPairOnExchangeException(String coinCode, String currency, Exchange exchange) {
        super();
        this.currencyPair = new CurrencyPair(coinCode, currency);
        this.exchange = exchange;
    }

    public NoCoinPairOnExchangeException(String coinCode, String currency, Exchange exchange, Integer editableMessageId) {
        super(editableMessageId);
        this.currencyPair = new CurrencyPair(coinCode, currency);
        this.exchange = exchange;
    }


    public NoCoinPairOnExchangeException(CurrencyPair currencyPair, Exchange exchange) {
        super();
        this.currencyPair = currencyPair;
        this.exchange = exchange;
    }

    public NoCoinPairOnExchangeException(CurrencyPair currencyPair, Exchange exchange, Integer editableMessageId) {
        super(editableMessageId);
        this.currencyPair = currencyPair;
        this.exchange = exchange;
    }

    @Override
    public String getMessage() {
        return String.format(messageTemplate, currencyPair.toString(), exchange.getName());
    }

    public void addAnalogue(CurrencyPair currencyPair) {
        this.analogues.add(currencyPair);
    }
}
