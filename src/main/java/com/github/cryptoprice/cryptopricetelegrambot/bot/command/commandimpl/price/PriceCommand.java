package com.github.cryptoprice.cryptopricetelegrambot.bot.command.commandimpl.price;

import com.github.cryptoprice.cryptopricetelegrambot.bot.command.Command;
import com.github.cryptoprice.cryptopricetelegrambot.bot.command.CommandName;
import com.github.cryptoprice.cryptopricetelegrambot.exception.CommonException;
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
public class PriceCommand implements Command {

    public final static String COIN_PRICE = "price.coinPrice";
    public final static String PRICE_FAVOURITES = "price.favouritesPrice";
    public final static String COMPARE = "price.compare";
    public final static String MAIN = "price.main";


    public final BotService botService;

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
            List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
            var coinPriceButton = InlineKeyboardButton.builder()
                    .text(BotMessages.getBotMessage(language, COIN_PRICE)).callbackData(CommandName.COIN_PRICE.getCommandIdentifier())
                    .build();
            var priceFavouritesButton = InlineKeyboardButton.builder()
                    .text(BotMessages.getBotMessage(language, PRICE_FAVOURITES)).callbackData(CommandName.PRICE_FAVOURITES.getCommandIdentifier())
                    .build();
            var compareButton = InlineKeyboardButton.builder()
                    .text(BotMessages.getBotMessage(language, COMPARE)).callbackData(CommandName.COMPARE.getCommandIdentifier())
                    .build();

            keyboard.add(List.of(coinPriceButton));
            keyboard.add(List.of(priceFavouritesButton));
            keyboard.add(List.of(compareButton));

            MessageSender.editOrSend(chatId, messageId, BotMessages.getBotMessage(language, MAIN), keyboard);
        }
    }


    @Override
    public CommandName getCommandName() {
        return CommandName.PRICE;
    }
}
