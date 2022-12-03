package com.github.cryptoprice.cryptopricetelegrambot.bot.command.commandimpl.notification;

import com.github.cryptoprice.cryptopricetelegrambot.bot.command.Command;
import com.github.cryptoprice.cryptopricetelegrambot.bot.command.CommandName;
import com.github.cryptoprice.cryptopricetelegrambot.exception.*;
import com.github.cryptoprice.cryptopricetelegrambot.service.common.BotService;
import com.github.cryptoprice.cryptopricetelegrambot.service.common.CommandCacheService;
import com.github.cryptoprice.cryptopricetelegrambot.utils.MessageSender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import static com.github.cryptoprice.cryptopricetelegrambot.bot.command.commandimpl.notification.NotificationCreateCommand.TextMessages.CREATE_NOTIFICATION_MESSAGE;
import static com.github.cryptoprice.cryptopricetelegrambot.bot.command.commandimpl.notification.NotificationCreateCommand.TextMessages.NOTIFICATION_CREATED;


@Component
@RequiredArgsConstructor
public class NotificationCreateCommand implements Command {

    private final BotService botService;
    private final CommandCacheService commandCacheService;

    @Override
    public void executeWithExceptions(Update update) throws NoCoinPairOnExchangeException, NotificationConditionAlreadyDoneException, WrongNotificationFormatException, NotSupportedCurrencyException, ExchangeServerException, AnyRuntimeException, CurrencyEqualsCodeException {
        String text;
        Long chatId;
        Integer messageId;
        boolean isCallback;

        if (update.hasCallbackQuery()) {
            text = update.getCallbackQuery().getData().trim();
            chatId = update.getCallbackQuery().getMessage().getChatId();
            messageId = update.getCallbackQuery().getMessage().getMessageId();
            isCallback = true;
        } else if (update.hasMessage() && update.getMessage().hasText()) {
            text = update.getMessage().getText().trim();
            chatId = update.getMessage().getChatId();
            messageId = null;
            isCallback = false;
        } else {
            return;
        }

        try {
            if (text.contentEquals(getCommandName().getCommandIdentifier())) {
                MessageSender.editOrSend(chatId, messageId, CREATE_NOTIFICATION_MESSAGE);
                commandCacheService.setCurrentCommand(chatId, CommandName.NOTIFICATION_CREATE);
            } else if (text.startsWith(getCommandName().getCommandIdentifier())) {
                var createRequest = text.substring(getCommandName().getCommandIdentifier().length()).trim();
                botService.createNotification(chatId, createRequest);
                MessageSender.editOrSend(chatId, messageId, NOTIFICATION_CREATED);
                commandCacheService.clearCache(chatId);
            } else if (!isCallback) {
                botService.createNotification(chatId, text);
                MessageSender.sendMessage(chatId, NOTIFICATION_CREATED);
                commandCacheService.clearCache(chatId);
            }
        } catch (RuntimeException e) {
            throw new AnyRuntimeException();
        }
    }


    @Override
    public CommandName getCommandName() {
        return CommandName.NOTIFICATION_CREATE;
    }

    static class TextMessages {
        public static final String CREATE_NOTIFICATION_MESSAGE = "Чтобы создать новое уведомление, отправьте сообщение в формате\n" +
                "{криптовалюта} <(>) {цена} {валюта}\n\n" +
                "Например:\n" +
                "BTC > 23000.12 USDT";
        public static final String NOTIFICATION_CREATED = "Уведомление создано";
    }
}