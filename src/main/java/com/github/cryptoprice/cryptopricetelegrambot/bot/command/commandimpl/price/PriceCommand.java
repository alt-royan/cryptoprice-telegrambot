package com.github.cryptoprice.cryptopricetelegrambot.bot.command.commandimpl.price;

import com.github.cryptoprice.cryptopricetelegrambot.bot.command.Command;
import com.github.cryptoprice.cryptopricetelegrambot.bot.command.CommandName;
import com.github.cryptoprice.cryptopricetelegrambot.exception.*;
import com.github.cryptoprice.cryptopricetelegrambot.service.common.BotService;
import com.github.cryptoprice.cryptopricetelegrambot.utils.MessageSender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

import static com.github.cryptoprice.cryptopricetelegrambot.bot.command.commandimpl.price.PriceCommand.TextMessages.*;


@Component
@RequiredArgsConstructor
public class PriceCommand implements Command {

    public final BotService botService;

    @Override
    public void executeWithExceptions(Update update) throws WrongCommandFormatException, NoCoinPairOnExchangeException, NotSupportedCurrencyException, ExchangeServerException, CurrencyEqualsCodeException {
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

        if (text.contentEquals(getCommandName().getCommandIdentifier())) {
            List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
            var coinPriceButton = InlineKeyboardButton.builder()
                    .text(COIN_PRICE).callbackData(CommandName.COIN_PRICE.getCommandIdentifier())
                    .build();
            var priceFavouritesButton = InlineKeyboardButton.builder()
                    .text(PRICE_FAVOURITES).callbackData(CommandName.PRICE_FAVOURITES.getCommandIdentifier())
                    .build();
            var compareButton = InlineKeyboardButton.builder()
                    .text(COMPARE).callbackData(CommandName.COMPARE.getCommandIdentifier())
                    .build();

            keyboard.add(List.of(coinPriceButton));
            keyboard.add(List.of(priceFavouritesButton));
            keyboard.add(List.of(compareButton));

            MessageSender.editOrSend(chatId, messageId, "Курс", keyboard);
        }
    }


    @Override
    public CommandName getCommandName() {
        return CommandName.PRICE;
    }

    static class TextMessages {
        public final static String COIN_PRICE = "Курс криптовалюты";
        public final static String PRICE_FAVOURITES = "Курс избранных";
        public final static String COMPARE = "Сравнить на разных биржах";
    }
}
