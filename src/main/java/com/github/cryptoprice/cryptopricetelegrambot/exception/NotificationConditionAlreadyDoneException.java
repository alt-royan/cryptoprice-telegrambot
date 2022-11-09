package com.github.cryptoprice.cryptopricetelegrambot.exception;

import com.github.cryptoprice.cryptopricetelegrambot.model.Notification;

/**
 * Exception throws when try to create notification and the condition is already done
 */
public class NotificationConditionAlreadyDoneException extends CommonException {

    private final Notification n;
    private final String messageTemplate = "%s уже %s чем %f %s";

    public NotificationConditionAlreadyDoneException(Notification notification) {
        super();
        this.n = notification;
    }

    public NotificationConditionAlreadyDoneException(Notification notification, Integer editableMessageId) {
        super(editableMessageId);
        this.n = notification;
    }

    @Override
    public String getMessage() {
        return String.format(messageTemplate, n.getCoinCode(), n.getType().getSign(), n.getTriggeredPrice(), n.getCurrency());
    }
}
