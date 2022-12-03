package com.github.cryptoprice.cryptopricetelegrambot.config;

import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.binance.BinanceExchange;
import org.knowm.xchange.bitfinex.BitfinexExchange;
import org.knowm.xchange.bittrex.BittrexExchange;
import org.knowm.xchange.coinbasepro.CoinbaseProExchange;
import org.knowm.xchange.kraken.KrakenExchange;
import org.knowm.xchange.poloniex.PoloniexExchange;
import org.knowm.xchange.service.marketdata.MarketDataService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ExchangeConfig {

    @Bean
    public MarketDataService binanceMarket() {
        var binanceExchange = ExchangeFactory.INSTANCE.createExchange(BinanceExchange.class);
        return binanceExchange.getMarketDataService();
    }

    @Bean
    public MarketDataService poloniexMarket() {
        var poloniex = ExchangeFactory.INSTANCE.createExchange(PoloniexExchange.class);
        return poloniex.getMarketDataService();
    }

   /* @Bean
    public MarketDataService bitstampMarket() {
        var bitstamp = ExchangeFactory.INSTANCE.createExchange(BitstampExchange.class);
        return bitstamp.getMarketDataService();
    }*/

    @Bean
    public MarketDataService bitfinexMarket() {
        var bitfinex = ExchangeFactory.INSTANCE.createExchange(BitfinexExchange.class);
        return bitfinex.getMarketDataService();
    }

    @Bean
    public MarketDataService coinbaseMarket() {
        var coinbase = ExchangeFactory.INSTANCE.createExchange(CoinbaseProExchange.class);
        return coinbase.getMarketDataService();
    }

    @Bean
    public MarketDataService krakenMarket() {
        var kraken = ExchangeFactory.INSTANCE.createExchange(KrakenExchange.class);
        return kraken.getMarketDataService();
    }

    @Bean
    public MarketDataService bittrexMarket() {
        var bittrex = ExchangeFactory.INSTANCE.createExchange(BittrexExchange.class);
        return bittrex.getMarketDataService();
    }
}
