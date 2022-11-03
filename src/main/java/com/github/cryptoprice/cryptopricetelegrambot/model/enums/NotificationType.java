package com.github.cryptoprice.cryptopricetelegrambot.model.enums;

public enum NotificationType {
    MORE_THAN(">"), LESS_THAN("<");

    private final String sign;

    NotificationType(String sign) {
        this.sign = sign;
    }

    public static NotificationType getEnum(String value) {
        for (NotificationType v : values())
            if (v.getSign().equalsIgnoreCase(value)) return v;
        throw new IllegalArgumentException();
    }

    public String getSign() {
        return sign;
    }
}
