package com.github.cryptoprice.cryptopricetelegrambot.mapper;

import com.github.cryptoprice.cryptopricetelegrambot.model.enums.Currency;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface UtilMapper {

    @Named("fromBinanceSymbolToCoinCode")
    default String fromBinanceSymbolToCoinCode(String symbol) {
        int currencyIndex;
        for (Currency currency : Currency.values()) {
            if (symbol.endsWith(currency.toString())) {
                currencyIndex = symbol.lastIndexOf(currency.name());
                try {
                    Currency.valueOf(symbol.substring(currencyIndex).toUpperCase());
                    return symbol.substring(0, currencyIndex).toUpperCase();
                } catch (RuntimeException e) {
                    return null;
                }
            }
        }
        return null;
    }

    @Named("fromBinanceSymbolToCurrency")
    default Currency fromBinanceSymbolToCurrency(String symbol) {
        int currencyIndex;
        for (Currency currency : Currency.values()) {
            if (symbol.endsWith(currency.toString())) {
                currencyIndex = symbol.lastIndexOf(currency.name());
                try {
                    return Currency.valueOf(symbol.substring(currencyIndex).toUpperCase());
                } catch (RuntimeException e) {
                    return null;
                }
            }
        }
        return null;
    }

}
