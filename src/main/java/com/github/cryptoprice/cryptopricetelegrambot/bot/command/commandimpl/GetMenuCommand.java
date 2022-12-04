package com.github.cryptoprice.cryptopricetelegrambot.bot.command.commandimpl;

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
public class GetMenuCommand implements Command {

    public final static String PRICE = "menu.price";
    public final static String CHANGE_EXCHANGE = "menu.changeExchange";
    public final static String FAVOURITES = "menu.favourites";
    public final static String NOTIFICATION = "menu.notifications";
    public final static String MAIN = "menu.main";

    private final BotService botService;

    @Override
    public Language getLanguage(long chatId) {
        return botService.getLanguage(chatId);
    }

    @Override
    public void executeWithExceptions(Update update) throws CommonException {
        String text;
        Long chatId;

        if (update.hasCallbackQuery()) {
            text = update.getCallbackQuery().getData().trim();
            chatId = update.getCallbackQuery().getMessage().getChatId();
        } else if (update.hasMessage() && update.getMessage().hasText()) {
            text = update.getMessage().getText().trim();
            chatId = update.getMessage().getChatId();
        } else {
            return;
        }

        var language = getLanguage(chatId);
        if (text.contentEquals(getCommandName().getCommandIdentifier())) {
            List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

            var priceButton = InlineKeyboardButton.builder().text(BotMessages.getBotMessage(language, PRICE)).callbackData(CommandName.PRICE.getCommandIdentifier()).build();
            var changeExchangeButton = InlineKeyboardButton.builder().text(BotMessages.getBotMessage(language, CHANGE_EXCHANGE)).callbackData(CommandName.CHANGE_EXCHANGE.getCommandIdentifier()).build();
            var favouritesButton = InlineKeyboardButton.builder().text(BotMessages.getBotMessage(language, FAVOURITES)).callbackData(CommandName.FAVOURITES.getCommandIdentifier()).build();
            var notificationButton = InlineKeyboardButton.builder().text(BotMessages.getBotMessage(language, NOTIFICATION)).callbackData(CommandName.NOTIFICATIONS.getCommandIdentifier()).build();

            keyboard.add(List.of(priceButton, changeExchangeButton));
            keyboard.add(List.of(favouritesButton, notificationButton));

            MessageSender.sendMessage(chatId, BotMessages.getBotMessage(language, MAIN), keyboard);
        }
    }


    @Override
    public CommandName getCommandName() {
        return CommandName.GET_MENU;
    }
}