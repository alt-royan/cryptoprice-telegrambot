package com.github.cryptoprice.cryptopricetelegrambot.dto.common;

import com.github.cryptoprice.cryptopricetelegrambot.model.enums.Currency;
import lombok.Data;

import java.time.Instant;

@Data
public class CoinPrice24hDto {
    private String coinCode;
    private Currency currency;
    private Double priceChange;
    private Double priceChangePercent;
    private Double lastPrice;
    private Double bidPrice;
    private Double askPrice;
    private Double openPrice;
    private Double highPrice;
    private Double lowPrice;
    private Double volume;
    private Double quoteVolume;
    private Instant openTime;
    private Instant closeTime;
}
