package com.github.cryptoprice.cryptopricetelegrambot.bot.command.commandimpl.price;

import com.github.cryptoprice.cryptopricetelegrambot.bot.command.Command;
import com.github.cryptoprice.cryptopricetelegrambot.bot.command.CommandName;
import com.github.cryptoprice.cryptopricetelegrambot.dto.CoinPriceDto;
import com.github.cryptoprice.cryptopricetelegrambot.exception.*;
import com.github.cryptoprice.cryptopricetelegrambot.model.enums.CurrencyCounter;
import com.github.cryptoprice.cryptopricetelegrambot.service.common.BotService;
import com.github.cryptoprice.cryptopricetelegrambot.service.common.CommandCacheService;
import com.github.cryptoprice.cryptopricetelegrambot.utils.MessageSender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.github.cryptoprice.cryptopricetelegrambot.bot.command.commandimpl.price.CoinPriceCommand.TextMessages.*;


@Component
@RequiredArgsConstructor
public class CoinPriceCommand implements Command {

    public final BotService botService;
    private final CommandCacheService commandCacheService;

    private final String requestRegex = this.getCommandName().getCommandIdentifier() + " [a-zA-Z]*_[a-zA-Z]*";
    private static final int BUTTONS_IN_LINE = 4;

    @Override
    public void executeWithExceptions(Update update) throws WrongCommandFormatException, NoCoinPairOnExchangeException, NotSupportedCurrencyException, ExchangeServerException, CurrencyEqualsCodeException {
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

        if (text.contentEquals(getCommandName().getCommandIdentifier())) {
            List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
            var favouriteCoins = botService.getFavouriteCoins(chatId);
            for (int i = 0; i < favouriteCoins.size(); i = i + BUTTONS_IN_LINE) {
                int toIndex = Math.min(i + BUTTONS_IN_LINE, favouriteCoins.size());
                keyboard.add(favouriteCoins.subList(i, toIndex).stream()
                        .map(coinCode -> InlineKeyboardButton.builder()
                                .text(coinCode.toUpperCase())
                                .callbackData(coinCode.toUpperCase())
                                .build()).collect(Collectors.toList()));
            }

            keyboard.add(List.of(InlineKeyboardButton.builder()
                    .text("Другое ...")
                    .callbackData(ANOTHER_COIN_CALLBACK)
                    .build()));

            MessageSender.editOrSend(chatId, messageId, CHOOSE_CRYPTO, keyboard);
            commandCacheService.setCurrentCommand(chatId, getCommandName());
        } else if (text.startsWith(getCommandName().getCommandIdentifier())) {
            if (!Pattern.matches(requestRegex, text)) {
                throw new WrongCommandFormatException(getCommandName(), messageId);
            }
            var waitMessage = MessageSender.editOrSend(chatId, messageId, WATCH_PRICE_WAIT);

            var coinPrice24h = getCoinPriceFromRequest(text, chatId, messageId);

            if (isCallback) {
                MessageSender.editMessage(chatId, messageId, coinPrice24h.toString());
            } else {
                MessageSender.editMessage(chatId, waitMessage.getMessageId(), coinPrice24h.toString());
            }
            commandCacheService.clearCache(chatId);
        } else if (isCallback && text.contentEquals(ANOTHER_COIN_CALLBACK)) {
            MessageSender.editMessage(chatId, messageId, ANOTHER_COIN_CHOOSE_TEXT);
        } else {
            var keyboard = createKeyboardForCurrencies(text);
            MessageSender.editOrSend(chatId, messageId, CHOOSE_CURRENCY, keyboard);
            commandCacheService.clearCache(chatId);
        }
    }

    private CoinPriceDto getCoinPriceFromRequest(String text, long chatId, Integer messageId) throws ExchangeServerException, NoCoinPairOnExchangeException, CurrencyEqualsCodeException {
        var symbol = text.split(" ")[1].split("_");
        var coinCode = symbol[0];
        var currency = symbol[1].toUpperCase();
        try {
            return botService.getCoinPrice(chatId, coinCode, currency);
        } catch (NoCoinPairOnExchangeException e) {
            throw new NoCoinPairOnExchangeException(e.getCurrencyPair(), e.getExchange(), messageId);
        }
    }

    private List<List<InlineKeyboardButton>> createKeyboardForCurrencies(String text) {
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        List<InlineKeyboardButton> buttons = new ArrayList<>();
        for (CurrencyCounter currency : CurrencyCounter.values()) {
            var callback = getCommandName().getCommandIdentifier() +
                    " " +
                    text +
                    "_" +
                    currency.toString();
            var inlineButton = InlineKeyboardButton.builder()
                    .text(currency.toString().toUpperCase())
                    .callbackData(callback)
                    .build();
            if (buttons.size() < BUTTONS_IN_LINE) {
                buttons.add(inlineButton);
            } else {
                keyboard.add(buttons);
                buttons = new ArrayList<>();
            }
        }
        keyboard.add(buttons);
        return keyboard;
    }


    @Override
    public CommandName getCommandName() {
        return CommandName.COIN_PRICE;
    }

    //todo
    static class TextMessages {
        public static final String CHOOSE_CRYPTO = "Выберите криптовалюту";
        public static final String CHOOSE_CURRENCY = "Выберите валюту";
        public static final String ANOTHER_COIN_CALLBACK = "another_coin";
        public static final String ANOTHER_COIN_CHOOSE_TEXT = "Введите код криптовалюты";
        public static final String WATCH_PRICE_WAIT = "Смотрю курс ...";
    }
}
