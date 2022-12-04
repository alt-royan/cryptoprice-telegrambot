package com.github.cryptoprice.cryptopricetelegrambot.bot.command.commandimpl.favourites;

import com.github.cryptoprice.cryptopricetelegrambot.bot.command.Command;
import com.github.cryptoprice.cryptopricetelegrambot.bot.command.CommandName;
import com.github.cryptoprice.cryptopricetelegrambot.exception.CommonException;
import com.github.cryptoprice.cryptopricetelegrambot.exception.WrongCommandFormatException;
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
import java.util.regex.Pattern;


@Component
@RequiredArgsConstructor
public class FavouritesRemoveCommand implements Command {

    public final static String DELETE_MAIN = "favourites.remove.main";
    public final static String SUCCESS = "favourites.remove.success";
    public final static String DELETE_FAVOURITE_CALLBACK = CommandName.FAVOURITES_REMOVE.getCommandIdentifier() + " %s";

    private final BotService botService;

    private final String requestRegex = this.getCommandName().getCommandIdentifier() + " [a-zA-Z]*";

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
        if (text.contentEquals(getCommandName().getCommandIdentifier())) {
            var favourites = botService.getFavouriteCoins(chatId);
            List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
            for (int i = 0; i < favourites.size(); i += 2) {
                List<InlineKeyboardButton> temp = new ArrayList<>();
                temp.add(InlineKeyboardButton.builder()
                        .text(favourites.get(i).toUpperCase())
                        .callbackData(String.format(DELETE_FAVOURITE_CALLBACK, favourites.get(i)))
                        .build());
                if (i + 1 < favourites.size()) {
                    temp.add(InlineKeyboardButton.builder()
                            .text(favourites.get(i + 1).toUpperCase())
                            .callbackData(String.format(DELETE_FAVOURITE_CALLBACK, favourites.get(i + 1)))
                            .build());
                }
                keyboard.add(temp);
            }

            MessageSender.editOrSend(chatId, messageId, BotMessages.getBotMessage(language, DELETE_MAIN), keyboard);
        } else if (text.startsWith(getCommandName().getCommandIdentifier())) {
            if (!Pattern.matches(requestRegex, text)) {
                throw new WrongCommandFormatException(getCommandName(), messageId);
            }
            var coinCode = text.substring(getCommandName().getCommandIdentifier().length()).trim();
            botService.removeFavouriteCoin(chatId, coinCode);
            MessageSender.editOrSend(chatId, messageId, String.format(BotMessages.getBotMessage(language, SUCCESS), coinCode.toUpperCase()));
        }
    }


    @Override
    public CommandName getCommandName() {
        return CommandName.FAVOURITES_REMOVE;
    }
}