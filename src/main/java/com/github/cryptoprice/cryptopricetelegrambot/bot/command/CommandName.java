package com.github.cryptoprice.cryptopricetelegrambot.bot.command;

/**
 * Enumeration for {@link Command}'s.
 */
public enum CommandName {

    START("/start"),
    LANGUAGE("/language"),
    STOP("/stop"),
    PRICE("/price"),
    COIN_PRICE("/coinPrice"),
    HELP("/help"),
    CANCEL("/cancel"),
    NOTIFICATIONS("/notifications"),
    NOTIFICATION_CREATE("/notificationCreate"),
    NOTIFICATION_DELETE("/notificationDelete"),
    FAVOURITES("/favourites"),
    FAVOURITES_ADD("/favouritesAdd"),
    FAVOURITES_REMOVE("/favouritesRemove"),
    PRICE_FAVOURITES("/priceFavourites"),
    COMPARE("/compare"),
    CHANGE_EXCHANGE("/changeExchange"),
    GET_MENU("/getMenu"),
    NONE("");


    private final String commandIdentifier;

    CommandName(String commandIdentifier) {
        this.commandIdentifier = commandIdentifier;
    }

    public String getCommandIdentifier() {
        return commandIdentifier;
    }

}