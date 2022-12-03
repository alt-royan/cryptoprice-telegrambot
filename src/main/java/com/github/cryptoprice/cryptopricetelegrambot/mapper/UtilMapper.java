package com.github.cryptoprice.cryptopricetelegrambot.mapper;

import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.instrument.Instrument;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

import java.util.Map;


@Mapper(componentModel = "spring")
public interface UtilMapper {

    Map<String, Currency> analogues = Map.of("UST", Currency.USDT);

    @Named("fromInstrumentToCoinCode")
    default Currency fromInstrumentToCoinCode(Instrument instrument) {
        var currencyPair = (CurrencyPair) instrument;
        return currencyPair.base;

    }

    @Named("fromInstrumentToCurrency")
    default Currency fromInstrumentToCurrency(Instrument instrument) {
        var currencyPair = (CurrencyPair) instrument;
        if (analogues.containsKey(currencyPair.counter.getCurrencyCode())) {
            return analogues.get(currencyPair.counter.getCurrencyCode());
        }
        return currencyPair.counter;
    }
}
