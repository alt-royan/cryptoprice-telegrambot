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

    public static Exchange getEnum(String value) {
        for (Exchange v : values())
            if (v.getName().equalsIgnoreCase(value)) return v;
        throw new IllegalArgumentException();
    }
}
