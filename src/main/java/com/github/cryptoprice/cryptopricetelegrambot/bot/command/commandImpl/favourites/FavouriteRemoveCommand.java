package com.github.cryptoprice.cryptopricetelegrambot.bot.command.commandImpl.favourites;

import com.github.cryptoprice.cryptopricetelegrambot.bot.command.Command;
import com.github.cryptoprice.cryptopricetelegrambot.bot.command.CommandName;
import com.github.cryptoprice.cryptopricetelegrambot.exception.WrongCommandFormatException;
import com.github.cryptoprice.cryptopricetelegrambot.service.common.BotService;
import com.github.cryptoprice.cryptopricetelegrambot.utils.MessageSender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static com.github.cryptoprice.cryptopricetelegrambot.bot.command.commandImpl.favourites.FavouriteRemoveCommand.TextMessages.*;


@Component
@RequiredArgsConstructor
public class FavouriteRemoveCommand implements Command {

    private final BotService botService;

    private final String requestRegex = "/favouriteRemove [a-zA-Z]*";

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
        } catch (WrongCommandFormatException e) {
            if (e.getEditableMessageId() != null) {
                MessageSender.editMessage(chatId, e.getEditableMessageId(), WRONG_DELETE_FORMAT);
            } else {
                MessageSender.sendMessage(chatId, WRONG_DELETE_FORMAT);
            }
        } catch (RuntimeException ex) {
            MessageSender.sendMessage(update.getMessage().getChatId(), TRY_AGAIN);
        }
    }

    @Override
    public void executeWithExceptions(Update update) throws WrongCommandFormatException {
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
            var favourites = botService.getFavouriteCoins(chatId);
            List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
            favourites.forEach(f -> keyboard.add(List.of(InlineKeyboardButton.builder()
                    .text(f.toUpperCase())
                    .callbackData(String.format(DELETE_FAVOURITE_CALLBACK, f))
                    .build())));

            editOrSend(chatId, messageId, isCallback, DELETE_INIT_MESSAGE, keyboard);
        } else if (text.startsWith(getCommandName().getCommandIdentifier())) {
            if (!Pattern.matches(requestRegex, text)) {
                throw new WrongCommandFormatException(messageId);
            }
            var coinCode = text.substring(getCommandName().getCommandIdentifier().length()).trim();
            botService.removeFavouriteCoin(chatId, coinCode);
            editOrSend(chatId, messageId, isCallback, FAVOURITE_DELETED);
        }
    }

    private void editOrSend(long chatId, int messageId, boolean isCallback, String text) {
        if (isCallback) {
            MessageSender.editMessage(chatId, messageId, text);
        } else {
            MessageSender.sendMessage(chatId, text);
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
        return CommandName.FAVOURITES_REMOVE;
    }

    static class TextMessages {
        public final static String DELETE_INIT_MESSAGE = "Выберите что убрать из избранного: ";

        public final static String TRY_AGAIN = "Ошибка. Попробуйте ещё раз";
        public final static String FAVOURITE_DELETED = "%s убрано из избранных";
        public final static String DELETE_FAVOURITE_CALLBACK = "/favouriteRemove %s";
        public static final String WRONG_DELETE_FORMAT = "Неверный формат команды /favouriteRemove";
    }
}