package com.github.cryptoprice.cryptopricetelegrambot.service.exchange;

import com.github.cryptoprice.cryptopricetelegrambot.mapper.CoinPriceMapper;
import com.github.cryptoprice.cryptopricetelegrambot.model.enums.Exchange;
import com.github.cryptoprice.cryptopricetelegrambot.service.common.ExchangeService;
import org.knowm.xchange.service.marketdata.MarketDataService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class KrakenService extends ExchangeService {


    public KrakenService(@Qualifier("krakenMarket") MarketDataService market, CoinPriceMapper coinPriceMapper) {
        super(market, coinPriceMapper);
    }

    @Override
    public Exchange getExchange() {
        return Exchange.KRAKEN;
    }
}
