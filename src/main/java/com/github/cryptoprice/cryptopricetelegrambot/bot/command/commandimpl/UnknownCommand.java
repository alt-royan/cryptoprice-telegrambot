package com.github.cryptoprice.cryptopricetelegrambot.bot.command.commandimpl;

import com.github.cryptoprice.cryptopricetelegrambot.bot.command.Command;
import com.github.cryptoprice.cryptopricetelegrambot.bot.command.CommandContainer;
import com.github.cryptoprice.cryptopricetelegrambot.bot.command.CommandName;
import com.github.cryptoprice.cryptopricetelegrambot.utils.MessageSender;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class UnknownCommand implements Command {

    public static final String UNKNOWN_MESSAGE = "Неизвестная команда";

    @Override
    public void execute(Update update) {
        Long chatId;
        if (update.hasCallbackQuery()) {
            chatId = update.getCallbackQuery().getMessage().getChatId();
        } else if (update.hasMessage()) {
            chatId = update.getMessage().getChatId();
        } else {
            return;
        }
        MessageSender.sendMessage(chatId, UNKNOWN_MESSAGE);
    }

    @Override
    public void executeWithExceptions(Update update) throws Exception {

    }

    @Override
    public CommandName getCommandName() {
        return null;
    }

    @Override
    public void registerCommand(CommandContainer commandContainer) {
    }
}