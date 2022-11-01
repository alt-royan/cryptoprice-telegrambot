package com.github.cryptoprice.cryptopricetelegrambot.mapper;


import com.github.cryptoprice.cryptopricetelegrambot.model.enums.Currency;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class UtilMapperTest {

    @Test
    void fromBinanceSymbolToCoinCode() {
        String btc_usdt = "BTCUSDT";
        String expectedCoinCode = "BTC";
        UtilMapper utilMapper = new UtilMapper() {
        };
        var coinCode = utilMapper.fromBinanceSymbolToCoinCode(btc_usdt);

        Assertions.assertEquals(expectedCoinCode, coinCode);

        String unknown = "BTCUDT";
        var coinCode2 = utilMapper.fromBinanceSymbolToCoinCode(unknown);
        Assertions.assertNull(coinCode2);
    }

    @Test
    void fromBinanceSymbolToCurrency() {
        String btc_usdt = "BTCUSDT";
        Currency expectedCurrency = Currency.USDT;
        UtilMapper utilMapper = new UtilMapper() {
        };
        var currency = utilMapper.fromBinanceSymbolToCurrency(btc_usdt);

        Assertions.assertEquals(expectedCurrency, currency);

        String unknown = "BTCUDT";
        var currency2 = utilMapper.fromBinanceSymbolToCurrency(unknown);
        Assertions.assertNull(currency2);
    }
}
