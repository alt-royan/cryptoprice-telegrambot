package com.github.cryptoprice.cryptopricetelegrambot.model.enums;

public enum Exchange {
    BINANCE("Binance"),
    POLONIEX("Poloniex"),
    BITFINEX("Bitfinex"),
    COINBASE("Coinbase"),
    KRAKEN("Kraken"),
    BITTREX("Bittrex");

    private final String name;

    Exchange(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static Exchange getEnum(String value) {
        for (Exchange v : values())
            if (v.getName().equalsIgnoreCase(value)) return v;
        throw new IllegalArgumentException();
    }
}
