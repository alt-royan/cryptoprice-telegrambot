package com.github.cryptoprice.cryptopricetelegrambot.utils;

import com.github.cryptoprice.cryptopricetelegrambot.bot.TelegramCryptoPriceBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class MessageSender {

    private static TelegramCryptoPriceBot bot;

    public static void setBot(TelegramCryptoPriceBot bot) {
        MessageSender.bot = bot;
    }

    private MessageSender() {
    }

    public static void sendMessage(Long chatId, String message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.enableHtml(true);
        sendMessage.setText(message);

        try {
            bot.execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
