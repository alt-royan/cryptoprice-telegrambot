package com.github.cryptoprice.cryptopricetelegrambot.bot.command.commandimpl.favourites;

import com.github.cryptoprice.cryptopricetelegrambot.bot.command.Command;
import com.github.cryptoprice.cryptopricetelegrambot.bot.command.CommandName;
import com.github.cryptoprice.cryptopricetelegrambot.exception.CurrencyEqualsCodeException;
import com.github.cryptoprice.cryptopricetelegrambot.exception.ExchangeServerException;
import com.github.cryptoprice.cryptopricetelegrambot.exception.NoCoinPairOnExchangeException;
import com.github.cryptoprice.cryptopricetelegrambot.service.common.BotService;
import com.github.cryptoprice.cryptopricetelegrambot.service.common.CommandCacheService;
import com.github.cryptoprice.cryptopricetelegrambot.utils.MessageSender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Arrays;

import static com.github.cryptoprice.cryptopricetelegrambot.bot.command.commandimpl.favourites.FavouritesAddCommand.TextMessages.ADD_FAVOURITES_MESSAGE;
import static com.github.cryptoprice.cryptopricetelegrambot.bot.command.commandimpl.favourites.FavouritesAddCommand.TextMessages.FAVOURITES_ADDED;


@Component
@RequiredArgsConstructor
public class FavouritesAddCommand implements Command {

    private final BotService botService;
    private final CommandCacheService commandCacheService;

    @Override
    public void executeWithExceptions(Update update) throws NoCoinPairOnExchangeException, ExchangeServerException, CurrencyEqualsCodeException {
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
            MessageSender.editOrSend(chatId, messageId, ADD_FAVOURITES_MESSAGE);
            commandCacheService.setCurrentCommand(chatId, CommandName.FAVOURITES_ADD);
        } else if (text.startsWith(getCommandName().getCommandIdentifier())) {
            var coins = text.substring(getCommandName().getCommandIdentifier().length()).trim().split(" ");
            botService.addFavouriteCoins(chatId, Arrays.asList(coins));
            MessageSender.editOrSend(chatId, messageId, FAVOURITES_ADDED);
            commandCacheService.clearCache(chatId);
        } else if (!isCallback) {
            var coins = text.split(" ");
            botService.addFavouriteCoins(chatId, Arrays.asList(coins));
            MessageSender.sendMessage(chatId, FAVOURITES_ADDED);
            commandCacheService.clearCache(chatId);
        }
    }

    @Override
    public CommandName getCommandName() {
        return CommandName.FAVOURITES_ADD;
    }

    static class TextMessages {
        public static final String ADD_FAVOURITES_MESSAGE = "Введите коды криптовалют через пробел, которые хотите добавить в избранное";
        public static final String FAVOURITES_ADDED = "Криптовалюты добавлены";
    }
}