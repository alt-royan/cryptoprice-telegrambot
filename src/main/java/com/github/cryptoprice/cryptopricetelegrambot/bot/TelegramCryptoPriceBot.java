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
        String text;
        long chatId;

        if (update.hasCallbackQuery()) {
            text = update.getCallbackQuery().getData().trim();
            chatId = update.getCallbackQuery().getMessage().getChatId();
        } else if (update.hasMessage() && update.getMessage().hasText()) {
            text = update.getMessage().getText().trim();
            chatId = update.getMessage().getChatId();
        } else {
            return;
        }

        if (text.startsWith(COMMAND_PREFIX)) {
            commandContainer.retrieveCommand(text.split(" ")[0]).execute(update);
        } else {
            var currentCommand = commandCache.getCurrentCommand(chatId).getCommandIdentifier();
            commandContainer.retrieveCommand(currentCommand).execute(update);
        }
    }
}
