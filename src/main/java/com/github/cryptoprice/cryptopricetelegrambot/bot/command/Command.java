package com.github.cryptoprice.cryptopricetelegrambot.bot.command;


import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Command interface for handling telegram-bot commands.
 */
public interface Command {

    /**
     * Main method, which is executing command logic.
     *
     * @param update provided {@link Update} object with all the needed data for command.
     */
    void execute(Update update) throws Exception;

    void executeExceptionHandling(Update update);

    CommandName getCommandName();

    @Autowired
    default void registerCommand(CommandContainer commandContainer) {
        commandContainer.registerCommand(this);
    }
}
