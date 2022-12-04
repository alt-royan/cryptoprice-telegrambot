package com.github.cryptoprice.cryptopricetelegrambot.bot.command.commandimpl.price;

import com.github.cryptoprice.cryptopricetelegrambot.bot.command.Command;
import com.github.cryptoprice.cryptopricetelegrambot.bot.command.CommandName;
import com.github.cryptoprice.cryptopricetelegrambot.exception.CommonException;
import com.github.cryptoprice.cryptopricetelegrambot.model.enums.CurrencyCounter;
import com.github.cryptoprice.cryptopricetelegrambot.model.enums.Language;
import com.github.cryptoprice.cryptopricetelegrambot.service.common.BotService;
import com.github.cryptoprice.cryptopricetelegrambot.utils.BotMessages;
import com.github.cryptoprice.cryptopricetelegrambot.utils.MessageSender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;


@Component
@RequiredArgsConstructor
public class PriceFavouritesCommand implements Command {

    public final BotService botService;

    public static final String WATCH_PRICE_WAIT = "price.coinPrice.watchPrice";

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
            var waitMessage = MessageSender.editOrSend(chatId, messageId, BotMessages.getBotMessage(language, WATCH_PRICE_WAIT));

            var coinsPrices = botService.getFavouriteCoinsPrice(chatId, CurrencyCounter.USDT.toString());

            if (isCallback) {
                MessageSender.editMessage(chatId, messageId, coinsPrices.toString());
            } else {
                MessageSender.editMessage(chatId, waitMessage.getMessageId(), coinsPrices.toString());
            }
        }
    }


    @Override
    public CommandName getCommandName() {
        return CommandName.PRICE_FAVOURITES;
    }
}
