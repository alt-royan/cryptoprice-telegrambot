package com.github.cryptoprice.cryptopricetelegrambot.bot.command.commandimpl;

import com.github.cryptoprice.cryptopricetelegrambot.bot.command.Command;
import com.github.cryptoprice.cryptopricetelegrambot.bot.command.CommandName;
import com.github.cryptoprice.cryptopricetelegrambot.exception.WrongCommandFormatException;
import com.github.cryptoprice.cryptopricetelegrambot.model.enums.Exchange;
import com.github.cryptoprice.cryptopricetelegrambot.service.common.BotService;
import com.github.cryptoprice.cryptopricetelegrambot.utils.MessageSender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

import static com.github.cryptoprice.cryptopricetelegrambot.bot.command.commandimpl.ChangeExchangeCommand.TextMessages.*;

@Component
@RequiredArgsConstructor
public class ChangeExchangeCommand implements Command {

    private final BotService botService;

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
        } catch (RuntimeException e) {
            MessageSender.sendMessage(update.getMessage().getChatId(), TRY_AGAIN);
        } catch (WrongCommandFormatException ex) {
            if (ex.getEditableMessageId() != null) {
                MessageSender.editMessage(chatId, ex.getEditableMessageId(), WRONG_EXCHANGE);
            } else {
                MessageSender.sendMessage(chatId, WRONG_EXCHANGE);
            }
        }
    }

    @Override
    public void executeWithExceptions(Update update) throws WrongCommandFormatException {
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
            var exchange = botService.getExchange(chatId);
            List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
            for (Exchange ex : Exchange.values()) {
                keyboard.add(List.of(InlineKeyboardButton.builder()
                        .text(ex.getName())
                        .callbackData(getCommandName().getCommandIdentifier() + " " + ex)
                        .build()));
            }
            editOrSend(chatId, messageId, isCallback, String.format(CHOOSE_EXCHANGE, exchange.getName()), keyboard);
        } else if (text.startsWith(getCommandName().getCommandIdentifier())) {
            var exchangeName = text.substring(getCommandName().getCommandIdentifier().length()).trim();
            Exchange exchange;
            try {
                exchange = Exchange.getEnum(exchangeName);
            } catch (IllegalArgumentException e) {
                throw new WrongCommandFormatException();
            }
            botService.setExchange(chatId, exchange);
            editOrSend(chatId, messageId, isCallback, String.format(DONE, exchange.getName()));
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
        return CommandName.CHANGE_EXCHANGE;
    }

    static class TextMessages {
        public final static String DONE = "Биржа изменена на: %s";
        public final static String CHOOSE_EXCHANGE = "Текущая биржа: %s";

        public final static String WRONG_EXCHANGE = "Такая биржа не поддерживается";

        public final static String TRY_AGAIN = "Ошибка. Попробуйте ещё раз";
    }
}