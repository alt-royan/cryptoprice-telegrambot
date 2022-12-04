package com.github.cryptoprice.cryptopricetelegrambot.bot.command.commandimpl.favourites;

import com.github.cryptoprice.cryptopricetelegrambot.bot.command.Command;
import com.github.cryptoprice.cryptopricetelegrambot.bot.command.CommandName;
import com.github.cryptoprice.cryptopricetelegrambot.exception.CommonException;
import com.github.cryptoprice.cryptopricetelegrambot.model.enums.Language;
import com.github.cryptoprice.cryptopricetelegrambot.service.common.BotService;
import com.github.cryptoprice.cryptopricetelegrambot.service.common.CommandCacheService;
import com.github.cryptoprice.cryptopricetelegrambot.utils.BotMessages;
import com.github.cryptoprice.cryptopricetelegrambot.utils.MessageSender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Arrays;


@Component
@RequiredArgsConstructor
public class FavouritesAddCommand implements Command {

    public static final String ADD_FAVOURITES_MESSAGE = "favourites.add.main";
    public static final String FAVOURITES_ADDED = "favourites.add.success";

    private final BotService botService;
    private final CommandCacheService commandCacheService;

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
            MessageSender.editOrSend(chatId, messageId, BotMessages.getBotMessage(language, ADD_FAVOURITES_MESSAGE));
            commandCacheService.setCurrentCommand(chatId, CommandName.FAVOURITES_ADD);
        } else if (text.startsWith(getCommandName().getCommandIdentifier())) {
            var coins = text.substring(getCommandName().getCommandIdentifier().length()).trim().split(" ");
            botService.addFavouriteCoins(chatId, Arrays.asList(coins));
            MessageSender.editOrSend(chatId, messageId, BotMessages.getBotMessage(language, FAVOURITES_ADDED));
            commandCacheService.clearCache(chatId);
        } else if (!isCallback) {
            var coins = text.split(" ");
            botService.addFavouriteCoins(chatId, Arrays.asList(coins));
            MessageSender.sendMessage(chatId, BotMessages.getBotMessage(language, FAVOURITES_ADDED));
            commandCacheService.clearCache(chatId);
        }
    }

    @Override
    public CommandName getCommandName() {
        return CommandName.FAVOURITES_ADD;
    }
}