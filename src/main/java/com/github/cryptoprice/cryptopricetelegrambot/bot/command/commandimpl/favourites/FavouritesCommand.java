package com.github.cryptoprice.cryptopricetelegrambot.bot.command.commandimpl.favourites;

import com.github.cryptoprice.cryptopricetelegrambot.bot.command.Command;
import com.github.cryptoprice.cryptopricetelegrambot.bot.command.CommandName;
import com.github.cryptoprice.cryptopricetelegrambot.exception.AnyRuntimeException;
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
public class FavouritesCommand implements Command {

    public final static String NO_FAVOURITES = "favourites.noFavourites";
    public final static String FAVOURITES = "favourites.main";
    public final static String ADD_FAVOURITE = "favourites.addFavourites.title";
    public final static String ADD_FAVOURITE_CALLBACK = CommandName.FAVOURITES_ADD.getCommandIdentifier();
    public final static String DELETE_FAVOURITE = "favourites.deleteFavourites.title";
    public final static String DELETE_FAVOURITE_CALLBACK = CommandName.FAVOURITES_REMOVE.getCommandIdentifier();

    private final BotService botService;

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
        try {
            if (text.contentEquals(getCommandName().getCommandIdentifier())) {
                var favourites = botService.getFavouriteCoins(chatId);

                if (favourites.isEmpty()) {
                    List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
                    keyboard.add(List.of(InlineKeyboardButton.builder()
                            .text(BotMessages.getBotMessage(language, ADD_FAVOURITE))
                            .callbackData(ADD_FAVOURITE_CALLBACK)
                            .build()));
                    MessageSender.editOrSend(chatId, messageId, BotMessages.getBotMessage(language, NO_FAVOURITES), keyboard);
                } else {
                    var response = new StringBuilder();
                    for (String f : favourites) {
                        response.append(f.toUpperCase()).append(" ");
                    }
                    var responseStr = String.format(BotMessages.getBotMessage(language, FAVOURITES), response);
                    List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
                    keyboard.add(List.of(InlineKeyboardButton.builder()
                            .text(BotMessages.getBotMessage(language, ADD_FAVOURITE))
                            .callbackData(ADD_FAVOURITE_CALLBACK)
                            .build(), InlineKeyboardButton.builder()
                            .text(BotMessages.getBotMessage(language, DELETE_FAVOURITE))
                            .callbackData(DELETE_FAVOURITE_CALLBACK)
                            .build()));
                    MessageSender.editOrSend(chatId, messageId, responseStr, keyboard);
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
}