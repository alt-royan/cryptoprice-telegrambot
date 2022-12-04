package com.github.cryptoprice.cryptopricetelegrambot.bot.command.commandimpl.price;

import com.github.cryptoprice.cryptopricetelegrambot.bot.command.Command;
import com.github.cryptoprice.cryptopricetelegrambot.bot.command.CommandName;
import com.github.cryptoprice.cryptopricetelegrambot.exception.CommonException;
import com.github.cryptoprice.cryptopricetelegrambot.exception.WrongCommandFormatException;
import com.github.cryptoprice.cryptopricetelegrambot.model.enums.CurrencyCounter;
import com.github.cryptoprice.cryptopricetelegrambot.model.enums.Language;
import com.github.cryptoprice.cryptopricetelegrambot.service.common.BotService;
import com.github.cryptoprice.cryptopricetelegrambot.service.common.CommandCacheService;
import com.github.cryptoprice.cryptopricetelegrambot.utils.BotMessages;
import com.github.cryptoprice.cryptopricetelegrambot.utils.MessageSender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


@Component
@RequiredArgsConstructor
public class CompareCommand implements Command {

    public static final String CHOOSE_CRYPTO = "price.coinPrice.chooseCrypto";
    public static final String CHOOSE_CURRENCY = "price.coinPrice.chooseCurrency";
    public static final String ANOTHER_COIN_CALLBACK = "another_coin";
    public static final String ANOTHER_COIN_CHOOSE_TEXT = "price.coinPrice.another.text";
    public static final String WATCH_PRICE_WAIT = "price.coinPrice.watchPrice";

    public static final String ANOTHER = "price.coinPrice.another.button";

    public final BotService botService;
    private final CommandCacheService commandCacheService;

    private final String requestRegex = this.getCommandName().getCommandIdentifier() + " [a-zA-Z]*_[a-zA-Z]*";
    private static final int BUTTONS_IN_LINE = 4;

    @Override
    public Language getLanguage(long chatId) {
        return botService.getLanguage(chatId);
    }

    @Override
    public void executeWithExceptions(Update update) throws CommonException {
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

        var language = getLanguage(chatId);
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
                    .text(BotMessages.getBotMessage(language, ANOTHER))
                    .callbackData(ANOTHER_COIN_CALLBACK)
                    .build()));

            MessageSender.editOrSend(chatId, messageId, BotMessages.getBotMessage(language, CHOOSE_CRYPTO), keyboard);
            commandCacheService.setCurrentCommand(chatId, getCommandName());
        } else if (text.startsWith(getCommandName().getCommandIdentifier())) {
            if (!Pattern.matches(requestRegex, text)) {
                throw new WrongCommandFormatException(getCommandName(), messageId);
            }
            var waitMessage = MessageSender.editOrSend(chatId, messageId, BotMessages.getBotMessage(language, WATCH_PRICE_WAIT));

            var symbol = text.split(" ")[1].split("_");
            var coinCode = symbol[0];
            var currency = symbol[1].toUpperCase();
            var coinPriceMap = botService.getPriceAllExchanges(chatId, coinCode, currency);

            if (isCallback) {
                MessageSender.editMessage(chatId, messageId, coinPriceMap.toString());
            } else {
                MessageSender.editMessage(chatId, waitMessage.getMessageId(), coinPriceMap.toString());
            }
            commandCacheService.clearCache(chatId);
        } else if (isCallback && text.contentEquals(ANOTHER_COIN_CALLBACK)) {
            MessageSender.editMessage(chatId, messageId, BotMessages.getBotMessage(language, ANOTHER_COIN_CHOOSE_TEXT));
        } else {
            var keyboard = createKeyboardForCurrencies(text);
            MessageSender.editOrSend(chatId, messageId, BotMessages.getBotMessage(language, CHOOSE_CURRENCY), keyboard);
            commandCacheService.clearCache(chatId);
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
        return CommandName.COMPARE;
    }

}
