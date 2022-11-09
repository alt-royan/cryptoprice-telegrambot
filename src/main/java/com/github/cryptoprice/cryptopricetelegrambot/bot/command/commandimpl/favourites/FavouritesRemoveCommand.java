package com.github.cryptoprice.cryptopricetelegrambot.bot.command.commandimpl.favourites;

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

import static com.github.cryptoprice.cryptopricetelegrambot.bot.command.commandimpl.favourites.FavouritesRemoveCommand.TextMessages.*;


@Component
@RequiredArgsConstructor
public class FavouritesRemoveCommand implements Command {

    private final BotService botService;

    private final String requestRegex = "/favouritesRemove [a-zA-Z]*";

    @Override
    public void executeWithExceptions(Update update) throws WrongCommandFormatException {
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
            var favourites = botService.getFavouriteCoins(chatId);
            List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
            favourites.forEach(f -> keyboard.add(List.of(InlineKeyboardButton.builder()
                    .text(f.toUpperCase())
                    .callbackData(String.format(DELETE_FAVOURITE_CALLBACK, f))
                    .build())));

            MessageSender.editOrSend(chatId, messageId, DELETE_INIT_MESSAGE, keyboard);
        } else if (text.startsWith(getCommandName().getCommandIdentifier())) {
            if (!Pattern.matches(requestRegex, text)) {
                throw new WrongCommandFormatException(getCommandName(), messageId);
            }
            var coinCode = text.substring(getCommandName().getCommandIdentifier().length()).trim();
            botService.removeFavouriteCoin(chatId, coinCode);
            MessageSender.editOrSend(chatId, messageId, String.format(FAVOURITE_DELETED, coinCode.toUpperCase()));
        }
    }


    @Override
    public CommandName getCommandName() {
        return CommandName.FAVOURITES_REMOVE;
    }

    static class TextMessages {
        public final static String DELETE_INIT_MESSAGE = "Выберите что убрать из избранного: ";
        public final static String FAVOURITE_DELETED = "%s убрано из избранных";
        public final static String DELETE_FAVOURITE_CALLBACK = "/favouritesRemove %s";
    }
}