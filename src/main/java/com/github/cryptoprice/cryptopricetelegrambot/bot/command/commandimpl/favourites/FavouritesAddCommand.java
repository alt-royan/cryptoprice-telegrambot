package com.github.cryptoprice.cryptopricetelegrambot.bot.command.commandimpl.favourites;

import com.github.cryptoprice.cryptopricetelegrambot.bot.command.Command;
import com.github.cryptoprice.cryptopricetelegrambot.bot.command.CommandName;
import com.github.cryptoprice.cryptopricetelegrambot.exception.ExchangeServerException;
import com.github.cryptoprice.cryptopricetelegrambot.exception.NoCoinOnExchangeException;
import com.github.cryptoprice.cryptopricetelegrambot.service.common.BotService;
import com.github.cryptoprice.cryptopricetelegrambot.service.common.CommandCacheService;
import com.github.cryptoprice.cryptopricetelegrambot.utils.MessageSender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Arrays;

import static com.github.cryptoprice.cryptopricetelegrambot.bot.command.commandimpl.favourites.FavouritesAddCommand.TextMessages.*;


@Component
@RequiredArgsConstructor
public class FavouritesAddCommand implements Command {

    private final BotService botService;
    private final CommandCacheService commandCacheService;

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
        } catch (NoCoinOnExchangeException e) {
            if (e.getEditableMessageId() != null) {
                MessageSender.editMessage(chatId, e.getEditableMessageId(), String.format(NO_COIN_ON_EXCHANGE, e.getCoinCode(), e.getExchange().getName()));
            } else {
                MessageSender.sendMessage(chatId, String.format(NO_COIN_ON_EXCHANGE, e.getCoinCode(), e.getExchange().getName()));
            }
        } catch (ExchangeServerException e) {
            MessageSender.sendMessage(chatId, EXCHANGE_SERVER_ERROR);
        } catch (RuntimeException ex) {
            MessageSender.sendMessage(update.getMessage().getChatId(), TRY_AGAIN);
        }
    }

    @Override
    public void executeWithExceptions(Update update) throws NoCoinOnExchangeException, ExchangeServerException {
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
            editOrSend(chatId, messageId, isCallback, ADD_FAVOURITES_MESSAGE);
            commandCacheService.setCurrentCommand(chatId, CommandName.FAVOURITES_ADD);
        } else if (text.startsWith(getCommandName().getCommandIdentifier())) {
            var coins = text.substring(getCommandName().getCommandIdentifier().length()).trim().split(" ");
            botService.addFavouriteCoins(chatId, Arrays.asList(coins));
            editOrSend(chatId, messageId, isCallback, FAVOURITES_ADDED);
            commandCacheService.clearCache(chatId);
        } else if (!isCallback) {
            var coins = text.split(" ");
            botService.addFavouriteCoins(chatId, Arrays.asList(coins));
            MessageSender.sendMessage(chatId, FAVOURITES_ADDED);
            commandCacheService.clearCache(chatId);
        }
    }

    private void editOrSend(long chatId, int messageId, boolean isCallback, String text) {
        if (isCallback) {
            MessageSender.editMessage(chatId, messageId, text);
        } else {
            MessageSender.sendMessage(chatId, text);
        }
    }

    @Override
    public CommandName getCommandName() {
        return CommandName.FAVOURITES_ADD;
    }

    static class TextMessages {
        public static final String ADD_FAVOURITES_MESSAGE = "Введите коды криптовалют через пробел, которые хотите добавить в избранное";

        public static final String TRY_AGAIN = "Ошибка. Попробуйте ещё раз";
        public static final String FAVOURITES_ADDED = "Криптовалюты добавлены";
        public static final String NO_COIN_ON_EXCHANGE = "Такой монеты %s нет на бирже %s. Попробуйте сменить биржу или выберите другую криптовалюту";
        public static final String EXCHANGE_SERVER_ERROR = "Ошибка сервера биржи. Попробуйте позже";
    }
}