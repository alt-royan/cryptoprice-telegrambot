package com.github.cryptoprice.cryptopricetelegrambot.bot.command.commandImpl;

import com.github.cryptoprice.cryptopricetelegrambot.bot.command.Command;
import com.github.cryptoprice.cryptopricetelegrambot.bot.command.CommandName;
import com.github.cryptoprice.cryptopricetelegrambot.service.common.CommandCacheService;
import com.github.cryptoprice.cryptopricetelegrambot.utils.MessageSender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import static com.github.cryptoprice.cryptopricetelegrambot.bot.command.commandImpl.CancelCommand.TextMessages.*;

@Component
@RequiredArgsConstructor
public class CancelCommand implements Command {

    private final CommandCacheService commandCacheService;

    @Override
    public void execute(Update update) {
        try {
            this.executeWithExceptions(update);
        } catch (RuntimeException e) {
            MessageSender.sendMessage(update.getMessage().getChatId(), TRY_AGAIN);
        }
    }

    @Override
    public void executeWithExceptions(Update update) {
        var chatId = update.getMessage().getChatId();
        var currentCommand = commandCacheService.getCurrentCommand(chatId);
        if (currentCommand != CommandName.NONE) {
            commandCacheService.clearCache(chatId);
            MessageSender.sendMessage(chatId, String.format(CANCEL_COMMAND, currentCommand.getCommandIdentifier().substring(1)));
        } else {
            MessageSender.sendMessage(chatId, NO_ACTIVE_COMMANDS);
        }
    }

    @Override
    public CommandName getCommandName() {
        return CommandName.CANCEL;
    }


    static class TextMessages {
        public final static String NO_ACTIVE_COMMANDS = "Сейчас нет запущенных команд";
        public final static String CANCEL_COMMAND = "Команда %s отменена.\n\nСписок всех команд можно посмотреть здесь /help";
        public final static String TRY_AGAIN = "Ошибка. Попробуйте ещё раз";
    }
}