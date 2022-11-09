package com.github.cryptoprice.cryptopricetelegrambot.bot.command.commandimpl;

import com.github.cryptoprice.cryptopricetelegrambot.bot.command.Command;
import com.github.cryptoprice.cryptopricetelegrambot.bot.command.CommandName;
import com.github.cryptoprice.cryptopricetelegrambot.utils.MessageSender;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class StopCommand implements Command {

    public static final String STOP_MESSAGE = "Здесь будет прощальное сообщение";

    @Override
    public void execute(Update update) {
        MessageSender.sendMessage(update.getMessage().getChatId(), STOP_MESSAGE);
    }

    @Override
    public void executeWithExceptions(Update update) throws Exception {

    }

    @Override
    public CommandName getCommandName() {
        return CommandName.STOP;
    }
}