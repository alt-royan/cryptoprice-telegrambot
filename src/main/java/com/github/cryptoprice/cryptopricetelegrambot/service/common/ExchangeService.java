package com.github.cryptoprice.cryptopricetelegrambot.service.common;

import com.github.cryptoprice.cryptopricetelegrambot.dto.binance.TickerPrice24hDto;
import com.github.cryptoprice.cryptopricetelegrambot.dto.binance.TickerPriceDto;
import com.github.cryptoprice.cryptopricetelegrambot.exception.ClientException;
import com.github.cryptoprice.cryptopricetelegrambot.exception.ExchangeServerException;
import com.github.cryptoprice.cryptopricetelegrambot.model.enums.Currency;

import java.util.List;

public interface ExchangeService {
    default TickerPriceDto getCoinPrice(String coinCode, Currency currency) {
        return this.getCoinPrice(List.of(coinCode), currency).get(0);
    }

    default TickerPrice24hDto getCoinPriceFor24h(String coinCode, Currency currency) {
        return this.getCoinPriceFor24h(List.of(coinCode), currency).get(0);
    }

    List<TickerPriceDto> getCoinPrice(List<String> coinCodes, Currency currency) throws ClientException, ExchangeServerException;

    List<TickerPrice24hDto> getCoinPriceFor24h(List<String> coinCodes, Currency currency) throws ClientException, ExchangeServerException;
}
