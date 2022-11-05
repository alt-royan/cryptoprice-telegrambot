package com.github.cryptoprice.cryptopricetelegrambot.service.notification;

import com.github.cryptoprice.cryptopricetelegrambot.exception.NotSupportedCurrencyException;
import com.github.cryptoprice.cryptopricetelegrambot.exception.WrongCommandFormatException;
import com.github.cryptoprice.cryptopricetelegrambot.model.Notification;
import com.github.cryptoprice.cryptopricetelegrambot.model.enums.Currency;
import com.github.cryptoprice.cryptopricetelegrambot.model.enums.NotificationType;
import lombok.experimental.UtilityClass;

import java.util.regex.Pattern;

@UtilityClass
public class NotificationParser {

    private static final String regex = "[a-zA-Z]* [><] \\d*[.,]?\\d* [a-zA-Z]*";

    public static Notification parseNotificationCreateRequest(String request) throws NotSupportedCurrencyException, WrongCommandFormatException {
        if (!Pattern.matches(regex, request)) {
            throw new WrongCommandFormatException();
        }
        var splitRequest = request.trim().split(" ");
        try {
            var coinCode = splitRequest[0].toUpperCase();
            var notificationType = NotificationType.getEnum(splitRequest[1]);
            var price = Double.parseDouble(splitRequest[2]);
            Currency currency;
            try {
                currency = Currency.valueOf(splitRequest[3].toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new NotSupportedCurrencyException(splitRequest[3].toUpperCase());
            }

            var notification = new Notification();
            notification.setType(notificationType);
            notification.setCurrency(currency);
            notification.setTriggeredPrice(price);
            notification.setCoinCode(coinCode);
            return notification;
        } catch (IllegalArgumentException e) {
            throw new WrongCommandFormatException();
        }
    }
}
