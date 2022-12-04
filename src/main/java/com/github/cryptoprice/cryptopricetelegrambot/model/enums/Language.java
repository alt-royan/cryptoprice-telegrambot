package com.github.cryptoprice.cryptopricetelegrambot.model.enums;

import lombok.Getter;

import java.util.Locale;

@Getter
public enum Language {
    RU("Русский", new Locale("ru", "RU")),
    EN("English", new Locale("en", "EN"));

    private final String name;

    private final Locale locale;

    Language(String name, Locale locale) {
        this.name = name;
        this.locale = locale;
    }

    public static Language getEnumByName(String languageName) {
        for (Language l : values())
            if (l.getName().equalsIgnoreCase(languageName)) return l;
        throw new IllegalArgumentException();
    }
}
