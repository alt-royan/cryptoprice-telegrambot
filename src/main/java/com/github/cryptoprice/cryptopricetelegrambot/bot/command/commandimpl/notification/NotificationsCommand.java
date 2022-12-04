package com.github.cryptoprice.cryptopricetelegrambot.bot.command.commandimpl.notification;

import com.github.cryptoprice.cryptopricetelegrambot.bot.command.Command;
import com.github.cryptoprice.cryptopricetelegrambot.bot.command.CommandName;
import com.github.cryptoprice.cryptopricetelegrambot.exception.AnyRuntimeException;
import com.github.cryptoprice.cryptopricetelegrambot.exception.CommonException;
import com.github.cryptoprice.cryptopricetelegrambot.model.enums.Language;
import com.github.cryptoprice.cryptopricetelegrambot.service.common.BotService;
import com.github.cryptoprice.cryptopricetelegrambot.utils.BotMessages;
import com.github.cryptoprice.cryptopricetelegrambot.utils.MessageSender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;


@Component
@RequiredArgsConstructor
public class NotificationsCommand implements Command {

    public final static String NO_ACTIVE_NOTIFICATIONS = "notification.noActive";
    public final static String NOTIFICATION_MAIN = "notification.main";
    public final static String CREATE_NOTIFICATION = "notification.createNotification.title";
    public final static String CREATE_NOTIFICATION_CALLBACK = CommandName.NOTIFICATION_CREATE.getCommandIdentifier();
    public final static String DELETE_NOTIFICATION = "notification.deleteNotification.title";
    public final static String DELETE_NOTIFICATION_CALLBACK = CommandName.NOTIFICATION_DELETE.getCommandIdentifier();

    private final BotService botService;

    @Override
    public Language getLanguage(long chatId) {
        return botService.getLanguage(chatId);
    }

    @Override
    public void executeWithExceptions(Update update) throws CommonException {
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

        var language = getLanguage(chatId);
        try {
            if (text.contentEquals(getCommandName().getCommandIdentifier())) {
                var notifications = botService.getActiveNotifications(chatId);

                if (notifications.isEmpty()) {
                    List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
                    keyboard.add(List.of(InlineKeyboardButton.builder()
                            .text(BotMessages.getBotMessage(language, CREATE_NOTIFICATION))
                            .callbackData(CREATE_NOTIFICATION_CALLBACK)
                            .build()));
                    MessageSender.editOrSend(chatId, messageId, BotMessages.getBotMessage(language, NO_ACTIVE_NOTIFICATIONS), keyboard);
                } else {
                    var response = new StringBuilder();
                    for (int i = 0; i < notifications.size(); i++) {
                        var n = notifications.get(i);
                        response.append(i + 1).append(". ")
                                .append(n.getCoinCode()).append(" ")
                                .append(n.getType().getSign()).append(" ")
                                .append(n.getTriggeredPrice()).append(" ")
                                .append(n.getCurrency().toUpperCase()).append("\n");

                    }
                    List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
                    keyboard.add(List.of(InlineKeyboardButton.builder()
                            .text(BotMessages.getBotMessage(language, CREATE_NOTIFICATION))
                            .callbackData(CREATE_NOTIFICATION_CALLBACK)
                            .build(), InlineKeyboardButton.builder()
                            .text(BotMessages.getBotMessage(language, DELETE_NOTIFICATION))
                            .callbackData(DELETE_NOTIFICATION_CALLBACK)
                            .build()));
                    MessageSender.editOrSend(chatId, messageId, String.format(BotMessages.getBotMessage(language, NOTIFICATION_MAIN), response), keyboard);
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

}