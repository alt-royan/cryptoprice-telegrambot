package com.github.cryptoprice.cryptopricetelegrambot.dto.binance;

import lombok.Data;

import java.time.Instant;

@Data
public class TickerPrice24hDto {
    private String symbol;
    private Double priceChange;
    private Double priceChangePercent;
    private Double weightedAvgPrice;
    private Double prevClosePrice;
    private Double lastPrice;
    private Double lastQty;
    private Double bidPrice;
    private Double bidQty;
    private Double askPrice;
    private Double askQty;
    private Double openPrice;
    private Double highPrice;
    private Double lowPrice;
    private Double volume;
    private Double quoteVolume;
    private Instant openTime;
    private Instant closeTime;
    private Long firstId;
    private Long lastId;
    private Integer count;
}
