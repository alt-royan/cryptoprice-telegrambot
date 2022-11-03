package com.github.cryptoprice.cryptopricetelegrambot.bot.command;

/**
 * Enumeration for {@link Command}'s.
 */
public enum CommandName {

    START("/start"),
    STOP("/stop"),
    PRICE("/price"),
    HELP("/help");


    private final String commandIdentifier;

    CommandName(String commandIdentifier) {
        this.commandIdentifier = commandIdentifier;
    }

    public String getCommandIdentifier() {
        return commandIdentifier;
    }

}