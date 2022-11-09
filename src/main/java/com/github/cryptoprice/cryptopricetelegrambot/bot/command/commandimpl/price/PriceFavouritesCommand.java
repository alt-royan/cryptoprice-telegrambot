package com.github.cryptoprice.cryptopricetelegrambot.bot.command.commandimpl.price;

import com.github.cryptoprice.cryptopricetelegrambot.bot.command.Command;
import com.github.cryptoprice.cryptopricetelegrambot.bot.command.CommandName;
import com.github.cryptoprice.cryptopricetelegrambot.model.enums.Currency;
import com.github.cryptoprice.cryptopricetelegrambot.service.common.BotService;
import com.github.cryptoprice.cryptopricetelegrambot.service.common.CommandCacheService;
import com.github.cryptoprice.cryptopricetelegrambot.utils.MessageSender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import static com.github.cryptoprice.cryptopricetelegrambot.bot.command.commandimpl.price.PriceFavouritesCommand.TextMessages.TRY_AGAIN;


@Component
@RequiredArgsConstructor
public class PriceFavouritesCommand implements Command {

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
        } catch (RuntimeException ex) {
            MessageSender.sendMessage(update.getMessage().getChatId(), TRY_AGAIN);
        }
    }

    @Override
    public void executeWithExceptions(Update update) {
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
            var coinsPrices = botService.getFavouriteCoinsPrice(chatId, Currency.USDT);

            editOrSend(chatId, messageId, isCallback, coinsPrices.toString());
        }
    }

    private Message editOrSend(long chatId, int messageId, boolean isCallback, String text) {
        if (isCallback) {
            MessageSender.editMessage(chatId, messageId, text);
            return new Message();
        } else {
            return MessageSender.sendMessage(chatId, text);
        }
    }


    @Override
    public CommandName getCommandName() {
        return CommandName.PRICE_FAVOURITES;
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
