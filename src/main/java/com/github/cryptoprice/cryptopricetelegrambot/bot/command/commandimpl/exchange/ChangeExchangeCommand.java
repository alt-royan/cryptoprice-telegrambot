package com.github.cryptoprice.cryptopricetelegrambot.bot.command.commandimpl.exchange;

import com.github.cryptoprice.cryptopricetelegrambot.bot.command.Command;
import com.github.cryptoprice.cryptopricetelegrambot.bot.command.CommandName;
import com.github.cryptoprice.cryptopricetelegrambot.exception.CommonException;
import com.github.cryptoprice.cryptopricetelegrambot.exception.WrongCommandFormatException;
import com.github.cryptoprice.cryptopricetelegrambot.model.enums.Exchange;
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
public class ChangeExchangeCommand implements Command {

    public final static String EXCHANGE_IS_CHANGED = "exchange.isChanged";
    public final static String CURRENT_EXCHANGE = "exchange.current";


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

        if (text.contentEquals(getCommandName().getCommandIdentifier())) {
            var exchange = botService.getExchange(chatId);
            List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
            for (Exchange ex : Exchange.values()) {
                keyboard.add(List.of(InlineKeyboardButton.builder()
                        .text(ex.getName())
                        .callbackData(getCommandName().getCommandIdentifier() + " " + ex)
                        .build()));
            }
            MessageSender.editOrSend(chatId, messageId, String.format(BotMessages.getBotMessage(language, CURRENT_EXCHANGE), exchange.getName()), keyboard);
        } else if (text.startsWith(getCommandName().getCommandIdentifier())) {
            var exchangeName = text.substring(getCommandName().getCommandIdentifier().length()).trim();
            Exchange exchange;
            try {
                exchange = Exchange.getEnum(exchangeName);
            } catch (IllegalArgumentException e) {
                throw new WrongCommandFormatException(getCommandName(), messageId);
            }
            botService.setExchange(chatId, exchange);
            MessageSender.editOrSend(chatId, messageId, String.format(BotMessages.getBotMessage(language, EXCHANGE_IS_CHANGED), exchange.getName()));
        }
    }


    @Override
    public CommandName getCommandName() {
        return CommandName.CHANGE_EXCHANGE;
    }
}