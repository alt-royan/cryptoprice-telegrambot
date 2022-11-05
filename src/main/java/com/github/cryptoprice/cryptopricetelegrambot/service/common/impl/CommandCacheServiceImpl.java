package com.github.cryptoprice.cryptopricetelegrambot.service.common.impl;

import com.github.cryptoprice.cryptopricetelegrambot.bot.command.CommandName;
import com.github.cryptoprice.cryptopricetelegrambot.service.common.CommandCacheService;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class CommandCacheServiceImpl implements CommandCacheService {

    private final HashMap<Long, CommandName> currentCommands = new HashMap<>();

    @Override
    public CommandName getCurrentCommand(Long chatId) {
        return currentCommands.getOrDefault(chatId, CommandName.NONE);
    }

    @Override
    public void setCurrentCommand(Long chatId, CommandName commandName) {
        currentCommands.put(chatId, commandName);
    }

    @Override
    public void clearCache(Long chatId) {
        currentCommands.remove(chatId);
    }

}
