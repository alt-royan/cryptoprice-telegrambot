package com.github.cryptoprice.cryptopricetelegrambot.bot.command.commandimpl.favourites;

import com.github.cryptoprice.cryptopricetelegrambot.bot.command.Command;
import com.github.cryptoprice.cryptopricetelegrambot.bot.command.CommandName;
import com.github.cryptoprice.cryptopricetelegrambot.exception.AnyRuntimeException;
import com.github.cryptoprice.cryptopricetelegrambot.service.common.BotService;
import com.github.cryptoprice.cryptopricetelegrambot.utils.MessageSender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

import static com.github.cryptoprice.cryptopricetelegrambot.bot.command.commandimpl.favourites.FavouritesCommand.TextMessages.*;


@Component
@RequiredArgsConstructor
public class FavouritesCommand implements Command {

    private final BotService botService;

    @Override
    public void executeWithExceptions(Update update) throws AnyRuntimeException {
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

        try {
            if (text.contentEquals(getCommandName().getCommandIdentifier())) {
                var favourites = botService.getFavouriteCoins(chatId);

                if (favourites.isEmpty()) {
                    List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
                    keyboard.add(List.of(InlineKeyboardButton.builder()
                            .text(ADD_FAVOURITE)
                            .callbackData(ADD_FAVOURITE_CALLBACK)
                            .build()));
                    MessageSender.editOrSend(chatId, messageId, NO_FAVOURITES, keyboard);
                } else {
                    var response = new StringBuilder("Избранные криптовалюты:\n\n");
                    for (String f : favourites) {
                        response.append(f.toUpperCase()).append(" ");
                    }
                    List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
                    keyboard.add(List.of(InlineKeyboardButton.builder()
                            .text(ADD_FAVOURITE)
                            .callbackData(ADD_FAVOURITE_CALLBACK)
                            .build(), InlineKeyboardButton.builder()
                            .text(DELETE_FAVOURITE)
                            .callbackData(DELETE_FAVOURITE_CALLBACK)
                            .build()));
                    MessageSender.editOrSend(chatId, messageId, response.toString(), keyboard);
                }
            }
        } catch (RuntimeException e) {
            throw new AnyRuntimeException();
        }
    }


    @Override
    public CommandName getCommandName() {
        return CommandName.FAVOURITES;
    }

    static class TextMessages {
        public final static String NO_FAVOURITES = "У вас нет избранных криптовалют";
        public final static String ADD_FAVOURITE = "Добавить";
        public final static String ADD_FAVOURITE_CALLBACK = "/favouritesAdd";
        public final static String DELETE_FAVOURITE = "Удалить";
        public final static String DELETE_FAVOURITE_CALLBACK = "/favouritesRemove";
    }
}