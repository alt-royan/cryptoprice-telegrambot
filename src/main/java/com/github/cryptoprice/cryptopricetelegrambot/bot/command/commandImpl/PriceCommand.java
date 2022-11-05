package com.github.cryptoprice.cryptopricetelegrambot.bot.command.commandImpl;

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
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.github.cryptoprice.cryptopricetelegrambot.bot.command.commandImpl.PriceCommand.TextMessages.*;


@Component
@RequiredArgsConstructor
public class PriceCommand implements Command {

    public final BotService botService;
    private final CommandCacheService commandCacheService;

    private static final String requestRegex = "/price [a-zA-Z]*_[a-zA-Z]*";
    private static final int BUTTONS_IN_LINE = 4;


    @Override
    public void execute(Update update) throws Exception {
        if (update.hasCallbackQuery()) {
            handleCallbackQuery(update.getCallbackQuery());
        } else if (update.hasMessage() && update.getMessage().hasText()) {
            handleTextMessage(update.getMessage());
        }
    }

    @Override
    public void executeExceptionHandling(Update update) {
        long chatId;
        if (update.hasCallbackQuery()) {
            chatId = update.getCallbackQuery().getMessage().getChatId();
        } else if (update.hasMessage() && update.getMessage().hasText()) {
            chatId = update.getMessage().getChatId();
        } else {
            return;
        }

        try {
            this.execute(update);
        } catch (Exception e) {
            if (e instanceof WrongCommandFormatException) {
                var ex = (WrongCommandFormatException) e;
                if (ex.getEditableMessageId() != null) {
                    MessageSender.editMessage(chatId, ex.getEditableMessageId(), WRONG_PRICE_FORMAT, false);
                } else {
                    MessageSender.sendMessage(chatId, WRONG_PRICE_FORMAT, false);
                }
            } else if (e instanceof NotSupportedCurrencyException) {
                var ex = (NotSupportedCurrencyException) e;
                if (ex.getEditableMessageId() != null) {
                    MessageSender.editMessage(chatId, ex.getEditableMessageId(), String.format(NOT_SUPPORTED_CURRENCY, ex.getCurrency()), false);
                } else {
                    MessageSender.sendMessage(chatId, String.format(NOT_SUPPORTED_CURRENCY, ex.getCurrency()), false);
                }
            } else if (e instanceof NoCoinOnExchangeException) {
                var ex = (NoCoinOnExchangeException) e;
                if (ex.getEditableMessageId() != null) {
                    MessageSender.editMessage(chatId, ex.getEditableMessageId(), String.format(NO_COIN_ON_EXCHANGE, ex.getCoinCode(), ex.getExchange().getName()), false);
                } else {
                    MessageSender.sendMessage(chatId, String.format(NO_COIN_ON_EXCHANGE, ex.getCoinCode(), ex.getExchange().getName()), false);
                }
            } else if (e instanceof ExchangeServerException) {
                MessageSender.sendMessage(chatId, EXCHANGE_SERVER_ERROR, false);
            } else {
                e.printStackTrace();
            }
        }
    }

    private void handleTextMessage(Message message) throws WrongCommandFormatException, NotSupportedCurrencyException, NoCoinOnExchangeException, ExchangeServerException {
        String text = message.getText();
        long chatId = message.getChatId();

        if (text.contentEquals(getCommandName().getCommandIdentifier())) {
            var sendMessage = new SendMessage();
            sendMessage.setChatId(chatId);
            sendMessage.setText(CHOOSE_CRYPTO);

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

            MessageSender.sendMessage(sendMessage, keyboard);
            commandCacheService.setCurrentCommand(chatId, getCommandName());
        } else if (text.startsWith(getCommandName().getCommandIdentifier())) {
            if (!Pattern.matches(requestRegex, text)) {
                throw new WrongCommandFormatException();
            }
            var waitMessage = MessageSender.sendMessage(chatId, WATCH_PRICE_WAIT, false);

            var coinPrice24h = getCoinPriceFromRequest(text, chatId, waitMessage.getMessageId());

            MessageSender.editMessage(chatId, waitMessage.getMessageId(), coinPrice24h.toString(), false);
        } else {
            var keyboard = createKeyboardForCurrencies(text);

            var sendMessage = SendMessage.builder()
                    .chatId(chatId)
                    .text(CHOOSE_CURRENCY)
                    .build();

            MessageSender.sendMessage(sendMessage, keyboard);
            commandCacheService.setCurrentCommandNone(chatId);
        }
    }

    private void handleCallbackQuery(CallbackQuery callbackQuery) throws WrongCommandFormatException, NotSupportedCurrencyException, NoCoinOnExchangeException, ExchangeServerException {
        String text = callbackQuery.getData();
        long chatId = callbackQuery.getMessage().getChatId();
        int messageId = callbackQuery.getMessage().getMessageId();

        if (text.equals(ANOTHER_COIN_CALLBACK)) {
            var editMessage = EditMessageText.builder()
                    .messageId(messageId)
                    .chatId(chatId)
                    .text(ANOTHER_COIN_CHOOSE_TEXT)
                    .build();
            MessageSender.editMessage(editMessage);
        } else if (text.startsWith(getCommandName().getCommandIdentifier())) {
            if (!Pattern.matches(requestRegex, text)) {
                throw new WrongCommandFormatException();
            }
            var editMessage1 = EditMessageText.builder()
                    .messageId(messageId)
                    .chatId(chatId)
                    .text(WATCH_PRICE_WAIT)
                    .build();
            MessageSender.editMessage(editMessage1);

            var coinPrice24h = getCoinPriceFromRequest(text, chatId, messageId);

            var editMessage2 = EditMessageText.builder()
                    .messageId(messageId)
                    .chatId(chatId)
                    .text(coinPrice24h.toString())
                    .build();
            MessageSender.editMessage(editMessage2);
        } else {
            var keyboard = createKeyboardForCurrencies(text);

            var editMessage = EditMessageText.builder()
                    .messageId(messageId)
                    .chatId(chatId)
                    .text(CHOOSE_CURRENCY)
                    .replyMarkup(InlineKeyboardMarkup.builder().keyboard(keyboard).build())
                    .build();
            MessageSender.editMessage(editMessage);
            commandCacheService.setCurrentCommandNone(chatId);
        }
    }

    private CoinPrice24hDto getCoinPriceFromRequest(String text, long chatId, Integer messageId) throws NotSupportedCurrencyException, ExchangeServerException, NoCoinOnExchangeException {
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
    }
}
