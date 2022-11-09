package com.github.cryptoprice.cryptopricetelegrambot.bot.command.commandImpl.notification;

import com.github.cryptoprice.cryptopricetelegrambot.bot.command.Command;
import com.github.cryptoprice.cryptopricetelegrambot.bot.command.CommandName;
import com.github.cryptoprice.cryptopricetelegrambot.exception.*;
import com.github.cryptoprice.cryptopricetelegrambot.service.common.BotService;
import com.github.cryptoprice.cryptopricetelegrambot.service.common.CommandCacheService;
import com.github.cryptoprice.cryptopricetelegrambot.utils.MessageSender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

import static com.github.cryptoprice.cryptopricetelegrambot.bot.command.commandImpl.notification.NotificationCreateCommand.TextMessages.*;


@Component
@RequiredArgsConstructor
public class NotificationCreateCommand implements Command {

    private final BotService botService;
    private final CommandCacheService commandCacheService;

    @Override
    public void execute(Update update) {
        long chatId;
        if (update.hasCallbackQuery()) {
            chatId = update.getCallbackQuery().getMessage().getChatId();
        } else if (update.hasMessage() && update.getMessage().hasText()) {
            chatId = update.getMessage().getChatId();
        } else {
            return;
        }

        try {
            this.executeWithExceptions(update);
        } catch (NoCoinOnExchangeException e) {
            if (e.getEditableMessageId() != null) {
                MessageSender.editMessage(chatId, e.getEditableMessageId(), String.format(NO_COIN_ON_EXCHANGE, e.getCoinCode(), e.getExchange().getName()));
            } else {
                MessageSender.sendMessage(chatId, String.format(NO_COIN_ON_EXCHANGE, e.getCoinCode(), e.getExchange().getName()));
            }
        } catch (NotificationConditionAlreadyDoneException e) {
            var notification = e.getNotification();
            if (e.getEditableMessageId() != null) {
                MessageSender.editMessage(chatId, e.getEditableMessageId(), String.format(NOTIF_CONDITION_ALREADY_DONE,
                        notification.getCoinCode(), notification.getType().getSign(), notification.getTriggeredPrice(), notification.getCurrency()));
            } else {
                MessageSender.sendMessage(chatId, String.format(NOTIF_CONDITION_ALREADY_DONE,
                        notification.getCoinCode(), notification.getType().getSign(), notification.getTriggeredPrice(), notification.getCurrency()));
            }
        } catch (WrongCommandFormatException e) {
            if (e.getEditableMessageId() != null) {
                MessageSender.editMessage(chatId, e.getEditableMessageId(), WRONG_NOTIFICATION_FORMAT);
            } else {
                MessageSender.sendMessage(chatId, WRONG_NOTIFICATION_FORMAT);
            }
        } catch (NotSupportedCurrencyException e) {
            if (e.getEditableMessageId() != null) {
                MessageSender.editMessage(chatId, e.getEditableMessageId(), String.format(NOT_SUPPORTED_CURRENCY, e.getCurrency()));
            } else {
                MessageSender.sendMessage(chatId, String.format(NOT_SUPPORTED_CURRENCY, e.getCurrency()));
            }
        } catch (ExchangeServerException e) {
            MessageSender.sendMessage(chatId, EXCHANGE_SERVER_ERROR);
        } catch (RuntimeException ex) {
            MessageSender.sendMessage(update.getMessage().getChatId(), TRY_AGAIN);
        }
    }

    @Override
    public void executeWithExceptions(Update update) throws NoCoinOnExchangeException, NotificationConditionAlreadyDoneException, WrongCommandFormatException, NotSupportedCurrencyException, ExchangeServerException {
        String text;
        long chatId;
        int messageId;
        boolean isCallback;

        if (update.hasCallbackQuery()) {
            text = update.getCallbackQuery().getData().trim();
            chatId = update.getCallbackQuery().getMessage().getChatId();
            messageId = update.getCallbackQuery().getMessage().getMessageId();
            isCallback = true;
        } else if (update.hasMessage() && update.getMessage().hasText()) {
            text = update.getMessage().getText().trim();
            chatId = update.getMessage().getChatId();
            messageId = update.getMessage().getMessageId();
            isCallback = false;
        } else {
            return;
        }

        if (text.contentEquals(getCommandName().getCommandIdentifier())) {
            editOrSend(chatId, messageId, isCallback, CREATE_NOTIFICATION_MESSAGE);
            commandCacheService.setCurrentCommand(chatId, CommandName.NOTIFICATION_CREATE);
        } else if (text.startsWith(getCommandName().getCommandIdentifier())) {
            var createRequest = text.substring(getCommandName().getCommandIdentifier().length()).trim();
            botService.createNotification(chatId, createRequest);
            editOrSend(chatId, messageId, isCallback, NOTIFICATION_CREATED);
            commandCacheService.clearCache(chatId);
        } else if (!isCallback) {
            botService.createNotification(chatId, text);
            MessageSender.sendMessage(chatId, NOTIFICATION_CREATED);
            commandCacheService.clearCache(chatId);
        }
    }

    private void editOrSend(long chatId, int messageId, boolean isCallback, String text) {
        if (isCallback) {
            MessageSender.editMessage(chatId, messageId, text);
        } else {
            MessageSender.sendMessage(chatId, text);
        }
    }

    private void editOrSend(long chatId, int messageId, boolean isCallback, String text, List<List<InlineKeyboardButton>> keyboard) {
        if (isCallback) {
            MessageSender.editMessage(chatId, messageId, text, keyboard);
        } else {
            MessageSender.sendMessage(chatId, text, keyboard);
        }
    }

    @Override
    public CommandName getCommandName() {
        return CommandName.NOTIFICATION_CREATE;
    }

    static class TextMessages {
        public final static String CREATE_NOTIFICATION_MESSAGE = "Чтобы создать новое уведомление, отправьте сообщение в формате\n" +
                "{криптовалюта} <(>) {цена} {валюта}\n\n" +
                "Например:\n" +
                "BTC > 23000.12 USDT";

        public final static String TRY_AGAIN = "Ошибка. Попробуйте ещё раз";
        public final static String NOTIFICATION_CREATED = "Уведомление создано";
        public static final String WRONG_NOTIFICATION_FORMAT = "Неверный формат уведомления";
        public static final String NOT_SUPPORTED_CURRENCY = "Данная валюта %s не поддерживается";
        public static final String NO_COIN_ON_EXCHANGE = "Такой монеты %s нет на бирже %s. Попробуйте сменить биржу или выберите другую криптовалюту";
        public static final String EXCHANGE_SERVER_ERROR = "Ошибка сервера биржи. Попробуйте позже";
        public static final String NOTIF_CONDITION_ALREADY_DONE = "%s уже %s чем %f %s";
    }
}