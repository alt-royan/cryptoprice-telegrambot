package com.github.cryptoprice.cryptopricetelegrambot.service.common;

import com.github.cryptoprice.cryptopricetelegrambot.bot.command.CommandName;

public interface CommandCacheService {

    CommandName getCurrentCommand(Long chatId);

    void setCurrentCommand(Long chatId, CommandName commandName);

    default void setCurrentCommandNone(Long chatId) {
        setCurrentCommand(chatId, CommandName.NONE);
    }

    void clearCache(Long chatId);
}
