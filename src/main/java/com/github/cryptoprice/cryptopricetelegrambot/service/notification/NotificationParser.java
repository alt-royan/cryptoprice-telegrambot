package com.github.cryptoprice.cryptopricetelegrambot.service.notification;

import com.github.cryptoprice.cryptopricetelegrambot.model.Notification;
import lombok.experimental.UtilityClass;

@UtilityClass
public class NotificationParser {

    public static Notification parseNotificationCreateRequest(String request) {
        return new Notification();
    }
}
