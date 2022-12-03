package com.github.cryptoprice.cryptopricetelegrambot.service.common;

import com.github.cryptoprice.cryptopricetelegrambot.dto.CoinPriceDto;
import com.github.cryptoprice.cryptopricetelegrambot.exception.CurrencyEqualsCodeException;
import com.github.cryptoprice.cryptopricetelegrambot.exception.ExchangeServerException;
import com.github.cryptoprice.cryptopricetelegrambot.exception.NoCoinPairOnExchangeException;
import com.github.cryptoprice.cryptopricetelegrambot.mapper.CoinPriceMapper;
import com.github.cryptoprice.cryptopricetelegrambot.model.enums.Exchange;
import com.github.cryptoprice.cryptopricetelegrambot.service.common.impl.ServiceExecutorImpl;
import lombok.RequiredArgsConstructor;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.exceptions.CurrencyPairNotValidException;
import org.knowm.xchange.exceptions.ExchangeException;
import org.knowm.xchange.service.marketdata.MarketDataService;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

@RequiredArgsConstructor
public abstract class ExchangeService {

    protected final MarketDataService market;

    protected final CoinPriceMapper coinPriceMapper;


    public CoinPriceDto getCoinPrice(String coinCode, String currency) throws ExchangeServerException, CurrencyEqualsCodeException, NoCoinPairOnExchangeException {
        if (coinCode.equalsIgnoreCase(currency)) {
            throw new CurrencyEqualsCodeException(coinCode.toUpperCase());
        }
        try {
            var result = market.getTicker(convertToCurrencyPair(coinCode, currency));
            return coinPriceMapper.toCoinPrice(result);
        } catch (CurrencyPairNotValidException e) {
            e.printStackTrace();
            throw new NoCoinPairOnExchangeException(coinCode, currency, getExchange());
        } catch (ExchangeException | IOException e) {
            e.printStackTrace();
            throw new ExchangeServerException();
        }
    }

    public abstract Exchange getExchange();

    @Autowired
    public void registerService(ServiceExecutorImpl serviceExecutor) {
        serviceExecutor.registerService(this);
    }

    private CurrencyPair convertToCurrencyPair(String coinCode, String currency) {
        return new CurrencyPair(coinCode, currency);
    }
}
