package com.github.cryptoprice.cryptopricetelegrambot.model.enums;

public enum Exchange {
    BINANCE("Binance");

    private final String name;

    Exchange(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
