package com.github.cryptoprice.cryptopricetelegrambot.bot.command.commandimpl;

import com.github.cryptoprice.cryptopricetelegrambot.bot.command.Command;
import com.github.cryptoprice.cryptopricetelegrambot.bot.command.CommandName;
import com.github.cryptoprice.cryptopricetelegrambot.exception.WrongCommandFormatException;
import com.github.cryptoprice.cryptopricetelegrambot.utils.MessageSender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

import static com.github.cryptoprice.cryptopricetelegrambot.bot.command.commandimpl.GetMenuCommand.TextMessages.*;

@Component
@RequiredArgsConstructor
public class GetMenuCommand implements Command {


    @Override
    public void executeWithExceptions(Update update) throws WrongCommandFormatException {
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

        if (text.contentEquals(getCommandName().getCommandIdentifier())) {
            List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

            var priceButton = InlineKeyboardButton.builder().text(PRICE).callbackData(CommandName.PRICE.getCommandIdentifier()).build();
            var changeExchangeButton = InlineKeyboardButton.builder().text(CHANGE_EXCHANGE).callbackData(CommandName.CHANGE_EXCHANGE.getCommandIdentifier()).build();
            var favouritesButton = InlineKeyboardButton.builder().text(FAVOURITES).callbackData(CommandName.FAVOURITES.getCommandIdentifier()).build();
            var notificationButton = InlineKeyboardButton.builder().text(NOTIFICATION).callbackData(CommandName.NOTIFICATIONS.getCommandIdentifier()).build();

            keyboard.add(List.of(priceButton, changeExchangeButton));
            keyboard.add(List.of(favouritesButton, notificationButton));

            MessageSender.sendMessage(chatId, "Меню", keyboard);
        }
    }


    @Override
    public CommandName getCommandName() {
        return CommandName.GET_MENU;
    }

    static class TextMessages {
        public final static String PRICE = "Курс";
        public final static String CHANGE_EXCHANGE = "Изменить биржу";
        public final static String FAVOURITES = "Избранные";
        public final static String NOTIFICATION = "Уведомления";
    }
}