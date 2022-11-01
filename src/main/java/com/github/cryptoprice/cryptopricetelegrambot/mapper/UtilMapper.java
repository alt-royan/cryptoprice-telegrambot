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
            currencyIndex = symbol.lastIndexOf(currency.name());
            if (currencyIndex != -1) {
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
            currencyIndex = symbol.lastIndexOf(currency.name());
            if (currencyIndex != -1) {
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
