package com.github.cryptoprice.cryptopricetelegrambot.dto.binance;

import lombok.Data;

@Data
public class TickerPriceDto {

    private String symbol;
    private Double price;
}
