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
public class LanguageCommand implements Command {

    public final static String MAIN = "language.main";
    public final static String LANGUAGE_KEY = "language";
    public final static String SUCCESS = "language.success";
    public final static String LANGUAGE_CALLBACK = "/language %s";

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
        if (text.contentEquals(getCommandName().getCommandIdentifier())) {
            List<InlineKeyboardButton> buttonList = new ArrayList<>();
            for (Language value : Language.values()) {
                buttonList.add(InlineKeyboardButton.builder()
                        .text(BotMessages.getBotMessage(value, LANGUAGE_KEY))
                        .callbackData(String.format(LANGUAGE_CALLBACK, value.getName()))
                        .build());
            }

            MessageSender.sendMessage(chatId, BotMessages.getBotMessage(language, MAIN), List.of(buttonList));
        } else if (text.startsWith(getCommandName().getCommandIdentifier())) {
            var languageName = text.substring(getCommandName().getCommandIdentifier().length()).trim();
            botService.changeLanguage(Language.getEnumByName(languageName), chatId);
            language = getLanguage(chatId);
            MessageSender.editOrSend(chatId, messageId, String.format(BotMessages.getBotMessage(language, SUCCESS), language.getName()));
        }
    }


    @Override
    public CommandName getCommandName() {
        return CommandName.LANGUAGE;
    }
}