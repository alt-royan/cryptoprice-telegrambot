package com.github.cryptoprice.cryptopricetelegrambot.bot.command.commandimpl;

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

@Component
@RequiredArgsConstructor
public class CancelCommand implements Command {

    public final static String NO_ACTIVE_COMMANDS = "cancel.noActiveCommands";
    public final static String SUCCESS = "cancel.success";

    private final CommandCacheService commandCacheService;
    private final BotService botService;

    @Override
    public Language getLanguage(long chatId) {
        return botService.getLanguage(chatId);
    }

    @Override
    public void executeWithExceptions(Update update) throws CommonException {
        var chatId = update.getMessage().getChatId();
        var language = getLanguage(chatId);
        var currentCommand = commandCacheService.getCurrentCommand(chatId);
        if (currentCommand != CommandName.NONE) {
            commandCacheService.clearCache(chatId);
            MessageSender.sendMessage(chatId, String.format(BotMessages.getBotMessage(language, SUCCESS), currentCommand.getCommandIdentifier().substring(1)));
        } else {
            MessageSender.sendMessage(chatId, BotMessages.getBotMessage(language, NO_ACTIVE_COMMANDS));
        }
    }

    @Override
    public CommandName getCommandName() {
        return CommandName.CANCEL;
    }
}