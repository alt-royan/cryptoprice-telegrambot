package com.github.cryptoprice.cryptopricetelegrambot.service.exchange;

import com.github.cryptoprice.cryptopricetelegrambot.dto.CoinPriceDto;
import com.github.cryptoprice.cryptopricetelegrambot.exception.ExchangeServerException;
import com.github.cryptoprice.cryptopricetelegrambot.exception.NoCoinPairOnExchangeException;
import com.github.cryptoprice.cryptopricetelegrambot.mapper.CoinPriceMapper;
import com.github.cryptoprice.cryptopricetelegrambot.model.enums.Exchange;
import com.github.cryptoprice.cryptopricetelegrambot.service.common.ExchangeService;
import org.knowm.xchange.exceptions.CurrencyPairNotValidException;
import org.knowm.xchange.exceptions.ExchangeException;
import org.knowm.xchange.service.marketdata.MarketDataService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class BinanceService extends ExchangeService {


    public BinanceService(@Qualifier("binanceMarket") MarketDataService market, CoinPriceMapper coinPriceMapper) {
        super(market, coinPriceMapper);
    }

    public List<CoinPriceDto> getAllCoinPrice() throws ExchangeServerException, NoCoinPairOnExchangeException {
        try {
            var result = market.getTickers(null);
            return coinPriceMapper.toCoinPriceList(result);
        } catch (CurrencyPairNotValidException e) {
            e.printStackTrace();
            throw new NoCoinPairOnExchangeException(e.getCurrencyPair(), getExchange());
        } catch (ExchangeException | IOException e) {
            e.printStackTrace();
            throw new ExchangeServerException();
        }
    }

    @Override
    public Exchange getExchange() {
        return Exchange.BINANCE;
    }
}
