package com.github.cryptoprice.cryptopricetelegrambot.model.enums;

public enum Currency {

    EUR("EUR (Fiat)"),
    RUB("RUB (Fiat)"),
    USDT("Tether USDT"),
    BTC("Bitcoin BTC"),
    ETH("Ethereum ETH"),
    BNB("BNB"),
    BUSD("Binance USD");

    private final String name;

    Currency(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
