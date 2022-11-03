package com.github.cryptoprice.cryptopricetelegrambot.bot;

import com.github.cryptoprice.cryptopricetelegrambot.bot.command.CommandContainer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@Slf4j
@RequiredArgsConstructor
public class TelegramCryptoPriceBot extends TelegramLongPollingBot {

    @Value("${bot.token}")
    private String token;
    @Value("${bot.username}")
    private String username;
    private final CommandContainer commandContainer;

    public static final String COMMAND_PREFIX = "/";

    @Override
    public String getBotUsername() {
        return username;
    }

    @Override
    public String getBotToken() {
        return token;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String message = update.getMessage().getText().trim();
            String commandIdentifier = message.split(" ")[0].toLowerCase();
            commandContainer.retrieveCommand(commandIdentifier).execute(update);
        }
    }
}
