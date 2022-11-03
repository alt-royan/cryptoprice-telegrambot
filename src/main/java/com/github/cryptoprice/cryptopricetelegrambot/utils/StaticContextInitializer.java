package com.github.cryptoprice.cryptopricetelegrambot.utils;

import com.github.cryptoprice.cryptopricetelegrambot.bot.TelegramCryptoPriceBot;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@RequiredArgsConstructor
public class StaticContextInitializer {


    private final TelegramCryptoPriceBot telegramBot;

    @PostConstruct
    public void init() {
        MessageSender.setBot(telegramBot);
    }
}