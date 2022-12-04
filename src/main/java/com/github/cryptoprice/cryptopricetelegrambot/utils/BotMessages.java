package com.github.cryptoprice.cryptopricetelegrambot.utils;

import com.github.cryptoprice.cryptopricetelegrambot.model.enums.Language;

import java.util.ResourceBundle;

public class BotMessages {

    private static final String bundleName = "messages";

    public static String getBotMessage(Language language, String key) {
        ResourceBundle bundle = ResourceBundle.getBundle(bundleName, language.getLocale());
        return bundle.getString(key);
    }
}
