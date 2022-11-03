package com.github.cryptoprice.cryptopricetelegrambot.bot.command.commandImpl;

import com.github.cryptoprice.cryptopricetelegrambot.bot.command.Command;
import com.github.cryptoprice.cryptopricetelegrambot.bot.command.CommandName;
import com.github.cryptoprice.cryptopricetelegrambot.model.enums.Currency;
import com.github.cryptoprice.cryptopricetelegrambot.service.common.BotService;
import com.github.cryptoprice.cryptopricetelegrambot.utils.MessageSender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class PriceCommand implements Command {

    public final BotService botService;

    @Override
    public void execute(Update update) {
        var chatId = update.getMessage().getChatId();
        var symbol = update.getMessage().getText().trim().split(" ")[1];
        var coinCode = symbol.split("_")[0].toUpperCase();
        var currency = Currency.valueOf(symbol.split("_")[1].toUpperCase());
        MessageSender.sendMessage(chatId, botService.checkCoinPrice(chatId, coinCode, currency));
    }

    @Override
    public CommandName getCommandName() {
        return CommandName.PRICE;
    }
}
