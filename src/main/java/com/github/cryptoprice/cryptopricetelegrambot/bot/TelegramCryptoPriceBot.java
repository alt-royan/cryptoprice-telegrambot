package com.github.cryptoprice.cryptopricetelegrambot.bot;

import com.github.cryptoprice.cryptopricetelegrambot.bot.command.CommandContainer;
import com.github.cryptoprice.cryptopricetelegrambot.service.common.CommandCacheService;
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
    private final CommandCacheService commandCache;

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
        if (update.hasCallbackQuery()) {
            String data = update.getCallbackQuery().getData();
            Long chatId = update.getCallbackQuery().getMessage().getChatId();
            if (data.startsWith(COMMAND_PREFIX)) {
                commandContainer.retrieveCommand(data.split(" ")[0]).executeExceptionHandling(update);
            } else {
                var currentCommand = commandCache.getCurrentCommand(chatId).getCommandIdentifier();
                commandContainer.retrieveCommand(currentCommand).executeExceptionHandling(update);
            }
        } else if (update.hasMessage() && update.getMessage().hasText()) {
            String message = update.getMessage().getText();
            Long chatId = update.getMessage().getChatId();
            if (message.startsWith(COMMAND_PREFIX)) {
                commandContainer.retrieveCommand(message.split(" ")[0]).executeExceptionHandling(update);
            } else {
                var currentCommand = commandCache.getCurrentCommand(chatId).getCommandIdentifier();
                commandContainer.retrieveCommand(currentCommand).executeExceptionHandling(update);
            }
        }
    }
}
