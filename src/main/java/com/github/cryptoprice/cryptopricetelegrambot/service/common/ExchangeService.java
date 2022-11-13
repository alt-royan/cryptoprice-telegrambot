package com.github.cryptoprice.cryptopricetelegrambot.service.common;

import com.github.cryptoprice.cryptopricetelegrambot.dto.common.CoinPrice24hDto;
import com.github.cryptoprice.cryptopricetelegrambot.dto.common.CoinPriceDto;
import com.github.cryptoprice.cryptopricetelegrambot.exception.ClientException;
import com.github.cryptoprice.cryptopricetelegrambot.exception.CurrencyEqualsCodeException;
import com.github.cryptoprice.cryptopricetelegrambot.exception.ExchangeServerException;
import com.github.cryptoprice.cryptopricetelegrambot.model.enums.Currency;
import com.github.cryptoprice.cryptopricetelegrambot.model.enums.Exchange;
import com.github.cryptoprice.cryptopricetelegrambot.service.common.impl.ServiceExecutorImpl;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public interface ExchangeService {
    default CoinPriceDto getCoinPrice(String coinCode, Currency currency) throws ClientException, ExchangeServerException, CurrencyEqualsCodeException {
        return this.getCoinPrice(List.of(coinCode), currency).get(0);
    }

    default CoinPrice24hDto getCoinPriceFor24h(String coinCode, Currency currency) throws ClientException, ExchangeServerException, CurrencyEqualsCodeException {
        return this.getCoinPriceFor24h(List.of(coinCode), currency).get(0);
    }

    List<CoinPriceDto> getCoinPrice(List<String> coinCodes, Currency currency) throws ClientException, ExchangeServerException, CurrencyEqualsCodeException;

    List<CoinPrice24hDto> getCoinPriceFor24h(List<String> coinCodes, Currency currency) throws ClientException, ExchangeServerException, CurrencyEqualsCodeException;

    Exchange getExchange();

    @Autowired
    default void registerService(ServiceExecutorImpl serviceExecutor) {
        serviceExecutor.registerService(this);
    }
}
