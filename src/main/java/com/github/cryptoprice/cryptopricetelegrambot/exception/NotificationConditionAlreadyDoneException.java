package com.github.cryptoprice.cryptopricetelegrambot.exception;

import com.github.cryptoprice.cryptopricetelegrambot.model.Notification;
import lombok.Getter;

@Getter
public class NotificationConditionAlreadyDoneException extends Exception {

    private final Notification notification;
    private final Integer editableMessageId;

    public NotificationConditionAlreadyDoneException(Notification notification) {
        super();
        this.notification = notification;
        this.editableMessageId = null;
    }

    public NotificationConditionAlreadyDoneException(Notification notification, Integer editableMessageId) {
        super();
        this.notification = notification;
        this.editableMessageId = editableMessageId;
    }
}
