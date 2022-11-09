package com.github.cryptoprice.cryptopricetelegrambot.bot.command.commandImpl;

import com.github.cryptoprice.cryptopricetelegrambot.bot.command.Command;
import com.github.cryptoprice.cryptopricetelegrambot.bot.command.CommandName;
import com.github.cryptoprice.cryptopricetelegrambot.service.common.BotService;
import com.github.cryptoprice.cryptopricetelegrambot.utils.MessageSender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import static com.github.cryptoprice.cryptopricetelegrambot.bot.command.commandImpl.StartCommand.TextMessages.START_MESSAGE;
import static com.github.cryptoprice.cryptopricetelegrambot.bot.command.commandImpl.StartCommand.TextMessages.TRY_AGAIN;

@Component
@RequiredArgsConstructor
public class StartCommand implements Command {

    private final BotService botService;

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
        long chatId;

        if (update.hasCallbackQuery()) {
            chatId = update.getCallbackQuery().getMessage().getChatId();
        } else if (update.hasMessage() && update.getMessage().hasText()) {
            chatId = update.getMessage().getChatId();
        } else {
            return;
        }

        botService.registerChat(chatId);
        MessageSender.sendMessage(chatId, START_MESSAGE);
    }

    @Override
    public CommandName getCommandName() {
        return CommandName.START;
    }

    static class TextMessages {
        public final static String START_MESSAGE = "Привет!";

        public final static String TRY_AGAIN = "Ошибка. Попробуйте ещё раз";
    }
}