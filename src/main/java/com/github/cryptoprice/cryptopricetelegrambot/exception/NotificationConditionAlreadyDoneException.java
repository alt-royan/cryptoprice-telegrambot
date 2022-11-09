package com.github.cryptoprice.cryptopricetelegrambot.exception;

import com.github.cryptoprice.cryptopricetelegrambot.model.Notification;
import lombok.Getter;

@Getter
public class NotificationConditionAlreadyDoneException extends EditableMessageException {

    private final Notification notification;

    public NotificationConditionAlreadyDoneException(Notification notification) {
        super();
        this.notification = notification;
    }

    public NotificationConditionAlreadyDoneException(Notification notification, Integer editableMessageId) {
        super(editableMessageId);
        this.notification = notification;
    }
}
