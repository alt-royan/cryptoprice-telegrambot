package com.github.cryptoprice.cryptopricetelegrambot.bot.command.commandimpl;

import com.github.cryptoprice.cryptopricetelegrambot.bot.command.Command;
import com.github.cryptoprice.cryptopricetelegrambot.bot.command.CommandName;
import com.github.cryptoprice.cryptopricetelegrambot.utils.MessageSender;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class HelpCommand implements Command {

    public static final String HELP_MESSAGE = "Здесь будет справка по командам";

    @Override
    public void executeWithExceptions(Update update) {
        MessageSender.sendMessage(update.getMessage().getChatId(), HELP_MESSAGE);
    }


    @Override
    public CommandName getCommandName() {
        return CommandName.HELP;
    }
}