package com.github.cryptoprice.cryptopricetelegrambot.exception;

public class InvalidNotificationFormatException extends RuntimeException {

    private static final String MSG = "Invalid notification format";
    public InvalidNotificationFormatException() {
        super(MSG);
    }
}
