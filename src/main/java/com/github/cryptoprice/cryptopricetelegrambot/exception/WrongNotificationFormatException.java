package com.github.cryptoprice.cryptopricetelegrambot.exception;

import lombok.Getter;

@Getter
public class WrongNotificationFormatException extends CommonException {

    private final String message = "Неправильный формат уведомления.";
}
