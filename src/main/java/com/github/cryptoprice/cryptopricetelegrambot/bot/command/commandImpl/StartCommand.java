package com.github.cryptoprice.cryptopricetelegrambot.bot.command.commandImpl;

import com.github.cryptoprice.cryptopricetelegrambot.bot.command.Command;
import com.github.cryptoprice.cryptopricetelegrambot.bot.command.CommandName;
import com.github.cryptoprice.cryptopricetelegrambot.utils.MessageSender;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class StartCommand implements Command {

    public final static String START_MESSAGE = "Здесь будет приветствие";

    @Override
    public void execute(Update update) {
        MessageSender.sendMessage(update.getMessage().getChatId(), START_MESSAGE);
    }

    @Override
    public CommandName getCommandName() {
        return CommandName.START;
    }
}