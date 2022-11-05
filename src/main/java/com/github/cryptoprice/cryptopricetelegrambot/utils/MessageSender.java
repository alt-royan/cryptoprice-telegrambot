package com.github.cryptoprice.cryptopricetelegrambot.utils;

import com.github.cryptoprice.cryptopricetelegrambot.bot.TelegramCryptoPriceBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

public class MessageSender {

    private static TelegramCryptoPriceBot bot;

    public static void setBot(TelegramCryptoPriceBot bot) {
        MessageSender.bot = bot;
    }

    private MessageSender() {
    }

    public static Message sendMessage(Long chatId, String message, boolean html) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.enableHtml(html);
        sendMessage.setText(message);

        try {
            return bot.execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
            return new Message();
        }
    }


    public static void editMessage(EditMessageText editMessage) {
        try {
            bot.execute(editMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public static void editMessage(Long chatId, Integer messageId, String text, boolean html) {
        var editMessage = EditMessageText.builder()
                .messageId(messageId)
                .text(text)
                .chatId(chatId)
                .build();
        editMessage.enableHtml(html);
        try {
            bot.execute(editMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public static Message sendMessage(SendMessage sendMessage, List<List<InlineKeyboardButton>> buttons) {
        InlineKeyboardMarkup markupInline = InlineKeyboardMarkup.builder()
                .keyboard(buttons)
                .build();
        sendMessage.setReplyMarkup(markupInline);
        try {
            return bot.execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
            return new Message();
        }
    }
}
