package com.github.cryptoprice.cryptopricetelegrambot.bot.command;


import com.github.cryptoprice.cryptopricetelegrambot.exception.CommonException;
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
    default void execute(Update update) {
        long chatId;

        if (update.hasCallbackQuery()) {
            chatId = update.getCallbackQuery().getMessage().getChatId();
        } else if (update.hasMessage() && update.getMessage().hasText()) {
            chatId = update.getMessage().getChatId();
        } else {
            return;
        }

        try {
            this.executeWithExceptions(update);
        } catch (CommonException e) {
            e.handleMessage(chatId);
        }
    }

    void executeWithExceptions(Update update) throws CommonException;

    CommandName getCommandName();

    @Autowired
    default void registerCommand(CommandContainer commandContainer) {
        commandContainer.registerCommand(this);
    }
}
