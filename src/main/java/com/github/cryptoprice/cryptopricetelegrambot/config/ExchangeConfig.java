package com.github.cryptoprice.cryptopricetelegrambot.config;

import com.binance.connector.client.impl.SpotClientImpl;
import com.binance.connector.client.impl.spot.Market;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ExchangeConfig {

    @Bean
    Market binanceMarket() {
        SpotClientImpl client = new SpotClientImpl();
        return client.createMarket();
    }
}
