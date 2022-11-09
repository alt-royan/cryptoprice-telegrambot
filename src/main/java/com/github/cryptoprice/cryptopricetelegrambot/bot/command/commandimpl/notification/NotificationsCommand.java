package com.github.cryptoprice.cryptopricetelegrambot.bot.command.commandimpl.notification;

import com.github.cryptoprice.cryptopricetelegrambot.bot.command.Command;
import com.github.cryptoprice.cryptopricetelegrambot.bot.command.CommandName;
import com.github.cryptoprice.cryptopricetelegrambot.exception.AnyRuntimeException;
import com.github.cryptoprice.cryptopricetelegrambot.service.common.BotService;
import com.github.cryptoprice.cryptopricetelegrambot.utils.MessageSender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

import static com.github.cryptoprice.cryptopricetelegrambot.bot.command.commandimpl.notification.NotificationsCommand.TextMessages.*;


@Component
@RequiredArgsConstructor
public class NotificationsCommand implements Command {

    private final BotService botService;

    @Override
    public void executeWithExceptions(Update update) throws AnyRuntimeException {
        String text;
        Long chatId;
        Integer messageId;

        if (update.hasCallbackQuery()) {
            text = update.getCallbackQuery().getData().trim();
            chatId = update.getCallbackQuery().getMessage().getChatId();
            messageId = update.getCallbackQuery().getMessage().getMessageId();
        } else if (update.hasMessage() && update.getMessage().hasText()) {
            text = update.getMessage().getText().trim();
            chatId = update.getMessage().getChatId();
            messageId = null;
        } else {
            return;
        }

        try {
            if (text.contentEquals(getCommandName().getCommandIdentifier())) {
                var notifications = botService.getActiveNotifications(chatId);

                if (notifications.isEmpty()) {
                    List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
                    keyboard.add(List.of(InlineKeyboardButton.builder()
                            .text(CREATE_NOTIFICATION)
                            .callbackData(CREATE_NOTIFICATION_CALLBACK)
                            .build()));
                    MessageSender.editOrSend(chatId, messageId, NO_ACTIVE_NOTIFICATIONS, keyboard);
                } else {
                    var response = new StringBuilder();
                    for (int i = 0; i < notifications.size(); i++) {
                        var n = notifications.get(i);
                        response.append(i + 1).append(". ")
                                .append(n.getCoinCode().toUpperCase()).append(" ")
                                .append(n.getType().getSign()).append(" ")
                                .append(n.getTriggeredPrice()).append(" ")
                                .append(n.getCurrency().toString().toUpperCase()).append("\n");

                    }
                    List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
                    keyboard.add(List.of(InlineKeyboardButton.builder()
                            .text(CREATE_NOTIFICATION)
                            .callbackData(CREATE_NOTIFICATION_CALLBACK)
                            .build(), InlineKeyboardButton.builder()
                            .text(DELETE_NOTIFICATION)
                            .callbackData(DELETE_NOTIFICATION_CALLBACK)
                            .build()));
                    MessageSender.editOrSend(chatId, messageId, response.toString(), keyboard);
                }
            }
        } catch (RuntimeException e) {
            throw new AnyRuntimeException();
        }
    }


    @Override
    public CommandName getCommandName() {
        return CommandName.NOTIFICATIONS;
    }

    static class TextMessages {
        public final static String NO_ACTIVE_NOTIFICATIONS = "У вас нет активных уведомлений";
        public final static String CREATE_NOTIFICATION = "Создать";
        public final static String CREATE_NOTIFICATION_CALLBACK = "/notificationCreate";
        public final static String DELETE_NOTIFICATION = "Удалить";
        public final static String DELETE_NOTIFICATION_CALLBACK = "/notificationDelete";
    }
}