package com.github.cryptoprice.cryptopricetelegrambot.dto;

import lombok.Data;
import org.knowm.xchange.currency.Currency;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class CoinPriceDto {

    private Currency coinCode;
    private Currency currency;
    private BigDecimal open;
    private BigDecimal last;
    private BigDecimal bid;
    private BigDecimal ask;
    private BigDecimal high;
    private BigDecimal low;
    private BigDecimal vwap;
    private BigDecimal volume;
    private BigDecimal quoteVolume;
    private Date timestamp;
    private BigDecimal bidSize;
    private BigDecimal askSize;
    private BigDecimal percentageChange;
}
