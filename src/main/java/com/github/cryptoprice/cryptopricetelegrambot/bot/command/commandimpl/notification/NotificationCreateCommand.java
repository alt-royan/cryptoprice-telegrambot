package com.github.cryptoprice.cryptopricetelegrambot.bot.command.commandimpl.notification;

import com.github.cryptoprice.cryptopricetelegrambot.bot.command.Command;
import com.github.cryptoprice.cryptopricetelegrambot.bot.command.CommandName;
import com.github.cryptoprice.cryptopricetelegrambot.exception.AnyRuntimeException;
import com.github.cryptoprice.cryptopricetelegrambot.exception.CommonException;
import com.github.cryptoprice.cryptopricetelegrambot.model.enums.Language;
import com.github.cryptoprice.cryptopricetelegrambot.service.common.BotService;
import com.github.cryptoprice.cryptopricetelegrambot.service.common.CommandCacheService;
import com.github.cryptoprice.cryptopricetelegrambot.utils.BotMessages;
import com.github.cryptoprice.cryptopricetelegrambot.utils.MessageSender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;


@Component
@RequiredArgsConstructor
public class NotificationCreateCommand implements Command {

    public static final String CREATE_NOTIFICATION_MESSAGE = "notification.create.main";
    public static final String SUCCESS = "notification.create.success";

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
        try {
            if (text.contentEquals(getCommandName().getCommandIdentifier())) {
                MessageSender.editOrSend(chatId, messageId, BotMessages.getBotMessage(language, CREATE_NOTIFICATION_MESSAGE));
                commandCacheService.setCurrentCommand(chatId, CommandName.NOTIFICATION_CREATE);
            } else if (text.startsWith(getCommandName().getCommandIdentifier())) {
                var createRequest = text.substring(getCommandName().getCommandIdentifier().length()).trim();
                botService.createNotification(chatId, createRequest);
                MessageSender.editOrSend(chatId, messageId, BotMessages.getBotMessage(language, SUCCESS));
                commandCacheService.clearCache(chatId);
            } else if (!isCallback) {
                botService.createNotification(chatId, text);
                MessageSender.sendMessage(chatId, BotMessages.getBotMessage(language, SUCCESS));
                commandCacheService.clearCache(chatId);
            }
        } catch (RuntimeException e) {
            throw new AnyRuntimeException();
        }
    }


    @Override
    public CommandName getCommandName() {
        return CommandName.NOTIFICATION_CREATE;
    }
}