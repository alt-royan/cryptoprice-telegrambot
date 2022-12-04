package com.github.cryptoprice.cryptopricetelegrambot.bot.command.commandimpl.notification;

import com.github.cryptoprice.cryptopricetelegrambot.bot.command.Command;
import com.github.cryptoprice.cryptopricetelegrambot.bot.command.CommandName;
import com.github.cryptoprice.cryptopricetelegrambot.exception.AnyRuntimeException;
import com.github.cryptoprice.cryptopricetelegrambot.exception.CommonException;
import com.github.cryptoprice.cryptopricetelegrambot.exception.WrongCommandFormatException;
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
import java.util.regex.Pattern;


@Component
@RequiredArgsConstructor
public class NotificationDeleteCommand implements Command {

    public final static String DELETE_MAIN = "notification.delete.main";
    public final static String SUCCESS = "notification.delete.success";
    public final static String DELETE_NOTIFICATION_CALLBACK = CommandName.NOTIFICATION_DELETE.getCommandIdentifier() + " %d";
    private final BotService botService;

    private final String requestRegex = this.getCommandName().getCommandIdentifier() + " \\d*";


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
                List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

                notifications.forEach(n -> keyboard.add(List.of(InlineKeyboardButton.builder()
                        .text(String.format("%s %s %f %s", n.getCoinCode(), n.getType().getSign(), n.getTriggeredPrice(), n.getCurrency()))
                        .callbackData(String.format(DELETE_NOTIFICATION_CALLBACK, n.getId()))
                        .build())));

                MessageSender.editOrSend(chatId, messageId, BotMessages.getBotMessage(language, DELETE_MAIN), keyboard);
            } else if (text.startsWith(getCommandName().getCommandIdentifier())) {
                if (!Pattern.matches(requestRegex, text)) {
                    throw new WrongCommandFormatException(getCommandName(), messageId);
                }
                var notificationId = Long.parseLong(text.split(" ")[1]);
                botService.removeNotification(chatId, notificationId);
                MessageSender.editOrSend(chatId, messageId, BotMessages.getBotMessage(language, SUCCESS));
            }
        } catch (RuntimeException e) {
            throw new AnyRuntimeException();
        }
    }


    @Override
    public CommandName getCommandName() {
        return CommandName.NOTIFICATION_DELETE;
    }
}