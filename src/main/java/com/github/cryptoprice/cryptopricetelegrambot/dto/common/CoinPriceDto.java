package com.github.cryptoprice.cryptopricetelegrambot.dto.common;

import com.github.cryptoprice.cryptopricetelegrambot.model.enums.Currency;
import lombok.Data;

@Data
public class CoinPriceDto {
    private String coinCode;
    private Currency currency;
    private Double price;
}
