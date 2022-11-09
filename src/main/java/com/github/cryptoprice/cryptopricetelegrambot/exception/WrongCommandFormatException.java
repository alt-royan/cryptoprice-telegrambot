package com.github.cryptoprice.cryptopricetelegrambot.exception;

import com.github.cryptoprice.cryptopricetelegrambot.bot.command.CommandName;

/**
 * Exception throws when receives a command in the wrong format
 */
public class WrongCommandFormatException extends CommonException {

    private final String messageTemplate = "Неверный формат команды %s. Узнать все возможные команды вы можете здесь " + CommandName.HELP.getCommandIdentifier();

    private final CommandName command;

    public WrongCommandFormatException(CommandName command) {
        super();
        this.command = command;
    }

    public WrongCommandFormatException(CommandName command, Integer editableMessageId) {
        super(editableMessageId);
        this.command = command;
    }

    @Override
    public String getMessage() {
        return String.format(messageTemplate, command.getCommandIdentifier());
    }
}
