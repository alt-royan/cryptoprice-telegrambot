package com.github.cryptoprice.cryptopricetelegrambot.bot.command.commandImpl.price;

import com.github.cryptoprice.cryptopricetelegrambot.bot.command.Command;
import com.github.cryptoprice.cryptopricetelegrambot.bot.command.CommandName;
import com.github.cryptoprice.cryptopricetelegrambot.dto.common.CoinPrice24hDto;
import com.github.cryptoprice.cryptopricetelegrambot.exception.ExchangeServerException;
import com.github.cryptoprice.cryptopricetelegrambot.exception.NoCoinOnExchangeException;
import com.github.cryptoprice.cryptopricetelegrambot.exception.NotSupportedCurrencyException;
import com.github.cryptoprice.cryptopricetelegrambot.exception.WrongCommandFormatException;
import com.github.cryptoprice.cryptopricetelegrambot.model.enums.Currency;
import com.github.cryptoprice.cryptopricetelegrambot.service.common.BotService;
import com.github.cryptoprice.cryptopricetelegrambot.service.common.CommandCacheService;
import com.github.cryptoprice.cryptopricetelegrambot.utils.MessageSender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.github.cryptoprice.cryptopricetelegrambot.bot.command.commandImpl.price.PriceCommand.TextMessages.*;


@Component
@RequiredArgsConstructor
public class PriceCommand implements Command {

    public final BotService botService;
    private final CommandCacheService commandCacheService;

    private static final String requestRegex = "/price [a-zA-Z]*_[a-zA-Z]*";
    private static final int BUTTONS_IN_LINE = 4;

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
        } catch (WrongCommandFormatException ex) {
            if (ex.getEditableMessageId() != null) {
                MessageSender.editMessage(chatId, ex.getEditableMessageId(), WRONG_PRICE_FORMAT);
            } else {
                MessageSender.sendMessage(chatId, WRONG_PRICE_FORMAT);
            }
        } catch (NoCoinOnExchangeException ex) {
            if (ex.getEditableMessageId() != null) {
                MessageSender.editMessage(chatId, ex.getEditableMessageId(), String.format(NO_COIN_ON_EXCHANGE, ex.getCoinCode(), ex.getExchange().getName()));
            } else {
                MessageSender.sendMessage(chatId, String.format(NO_COIN_ON_EXCHANGE, ex.getCoinCode(), ex.getExchange().getName()));
            }
        } catch (NotSupportedCurrencyException ex) {
            if (ex.getEditableMessageId() != null) {
                MessageSender.editMessage(chatId, ex.getEditableMessageId(), String.format(NOT_SUPPORTED_CURRENCY, ex.getCurrency()));
            } else {
                MessageSender.sendMessage(chatId, String.format(NOT_SUPPORTED_CURRENCY, ex.getCurrency()));
            }
        } catch (ExchangeServerException ex) {
            MessageSender.sendMessage(chatId, EXCHANGE_SERVER_ERROR);
        } catch (RuntimeException ex) {
            MessageSender.sendMessage(update.getMessage().getChatId(), TRY_AGAIN);
        }
    }

    @Override
    public void executeWithExceptions(Update update) throws NoCoinOnExchangeException, WrongCommandFormatException, NotSupportedCurrencyException, ExchangeServerException {
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

            editOrSend(chatId, messageId, isCallback, CHOOSE_CRYPTO, keyboard);
            commandCacheService.setCurrentCommand(chatId, getCommandName());
        } else if (text.startsWith(getCommandName().getCommandIdentifier())) {
            if (!Pattern.matches(requestRegex, text)) {
                throw new WrongCommandFormatException();
            }
            var waitMessage = editOrSend(chatId, messageId, isCallback, WATCH_PRICE_WAIT);

            var coinPrice24h = getCoinPriceFromRequest(text, chatId, messageId);

            MessageSender.editMessage(chatId, waitMessage.getMessageId(), coinPrice24h.toString());
            commandCacheService.clearCache(chatId);
        } else if (isCallback && text.contentEquals(ANOTHER_COIN_CALLBACK)) {
            MessageSender.editMessage(chatId, messageId, ANOTHER_COIN_CHOOSE_TEXT);
        } else {
            var keyboard = createKeyboardForCurrencies(text);
            editOrSend(chatId, messageId, isCallback, CHOOSE_CURRENCY, keyboard);
            commandCacheService.clearCache(chatId);
        }
    }

    private CoinPrice24hDto getCoinPriceFromRequest(String text, long chatId, Integer messageId) throws
            NotSupportedCurrencyException, ExchangeServerException, NoCoinOnExchangeException {
        var symbol = text.split(" ")[1].split("_");
        var coinCode = symbol[0];
        Currency currency;
        try {
            currency = Currency.valueOf(symbol[1].toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new NotSupportedCurrencyException(symbol[1].toUpperCase(), messageId);
        }
        try {
            return botService.getCoinPrice24h(chatId, coinCode, currency);
        } catch (NoCoinOnExchangeException e) {
            throw new NoCoinOnExchangeException(e.getCoinCode(), e.getExchange(), messageId);
        }
    }

    private List<List<InlineKeyboardButton>> createKeyboardForCurrencies(String text) {
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        List<InlineKeyboardButton> buttons = new ArrayList<>();
        for (Currency currency : Currency.values()) {
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

    private Message editOrSend(long chatId, int messageId, boolean isCallback, String text) {
        if (isCallback) {
            MessageSender.editMessage(chatId, messageId, text);
            return new Message();
        } else {
            return MessageSender.sendMessage(chatId, text);
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
        return CommandName.PRICE;
    }

    //todo
    static class TextMessages {
        public static final String CHOOSE_CRYPTO = "Выберите криптовалюту";
        public static final String CHOOSE_CURRENCY = "Выберите валюту";
        public static final String ANOTHER_COIN_CALLBACK = "write_another_coin";
        public static final String ANOTHER_COIN_CHOOSE_TEXT = "Введите код криптовалюты";
        public static final String WATCH_PRICE_WAIT = "Смотрю курс ...";
        public static final String WRONG_PRICE_FORMAT = "Неверный формат команды /price";
        public static final String NOT_SUPPORTED_CURRENCY = "Данная валюта %s не поддерживается";
        public static final String NO_COIN_ON_EXCHANGE = "Такой монеты %s нет на бирже %s. Попробуйте сменить биржу или выберите другую криптовалюту";
        public static final String EXCHANGE_SERVER_ERROR = "Ошибка сервера биржи. Попробуйте позже";
        public final static String TRY_AGAIN = "Ошибка. Попробуйте ещё раз";
    }
}
