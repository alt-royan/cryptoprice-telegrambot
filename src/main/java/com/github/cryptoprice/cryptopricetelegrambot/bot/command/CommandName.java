package com.github.cryptoprice.cryptopricetelegrambot.bot.command;

/**
 * Enumeration for {@link Command}'s.
 */
public enum CommandName {

    START("/start"),
    STOP("/stop"),
    PRICE("/price"),
    HELP("/help"),
    CANCEL("/cancel"),
    NOTIFICATIONS("/notifications"),
    NOTIFICATION_CREATE("/notificationCreate"),
    NOTIFICATION_DELETE("/notificationDelete"),
    FAVOURITES("/favourites"),
    FAVOURITES_ADD("/favouriteAdd"),
    FAVOURITES_REMOVE("/favouriteRemove"),
    CHANGE_EXCHANGE("/changeExchange"),
    NONE("");


    private final String commandIdentifier;

    CommandName(String commandIdentifier) {
        this.commandIdentifier = commandIdentifier;
    }

    public String getCommandIdentifier() {
        return commandIdentifier;
    }

}