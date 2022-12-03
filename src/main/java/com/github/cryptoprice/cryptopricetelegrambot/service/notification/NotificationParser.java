package com.github.cryptoprice.cryptopricetelegrambot.service.notification;

import com.github.cryptoprice.cryptopricetelegrambot.exception.WrongNotificationFormatException;
import com.github.cryptoprice.cryptopricetelegrambot.model.Notification;
import com.github.cryptoprice.cryptopricetelegrambot.model.enums.NotificationType;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.util.regex.Pattern;

@UtilityClass
public class NotificationParser {

    private static final String regex = "[a-zA-Z]* [><] \\d*[.,]?\\d* [a-zA-Z]*";

    public static Notification parseNotificationCreateRequest(String request) throws WrongNotificationFormatException {
        if (!Pattern.matches(regex, request)) {
            throw new WrongNotificationFormatException();
        }
        var splitRequest = request.trim().split(" ");
        var coinCode = splitRequest[0].toUpperCase();
        var notificationType = NotificationType.getEnum(splitRequest[1]);
        var price = Double.parseDouble(splitRequest[2]);
        var currency = splitRequest[3].toUpperCase();

        var notification = new Notification();
        notification.setType(notificationType);
        notification.setCurrency(currency);
        notification.setTriggeredPrice(BigDecimal.valueOf(price));
        notification.setCoinCode(coinCode);
        return notification;
    }
}
