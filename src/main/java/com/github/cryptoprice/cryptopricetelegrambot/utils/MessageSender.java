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

    public static Message sendMessage(Long chatId, String message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(message);

        try {
            return bot.execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
            return new Message();
        }
    }

    public static Message sendMessage(Long chatId, String message, List<List<InlineKeyboardButton>> keyboard) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setReplyMarkup(InlineKeyboardMarkup.builder().keyboard(keyboard).build());
        sendMessage.setText(message);

        try {
            return bot.execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
            return new Message();
        }
    }

    public static Message sendMessage(SendMessage sendMessage) {
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

    public static void editMessage(Long chatId, Integer messageId, String text) {
        var editMessage = EditMessageText.builder()
                .messageId(messageId)
                .text(text)
                .chatId(chatId)
                .build();
        try {
            bot.execute(editMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public static void editMessage(Long chatId, Integer messageId, String text, List<List<InlineKeyboardButton>> keyboard) {
        var editMessage = EditMessageText.builder()
                .messageId(messageId)
                .text(text)
                .replyMarkup(InlineKeyboardMarkup.builder().keyboard(keyboard).build())
                .chatId(chatId)
                .build();
        try {
            bot.execute(editMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public static Message editOrSend(long chatId, Integer messageId, String text) {
        if (messageId != null) {
            MessageSender.editMessage(chatId, messageId, text);
            return new Message();
        } else {
            return MessageSender.sendMessage(chatId, text);
        }
    }

    public static Message editOrSend(long chatId, Integer messageId, String text, List<List<InlineKeyboardButton>> keyboard) {
        if (messageId != null) {
            MessageSender.editMessage(chatId, messageId, text, keyboard);
            return new Message();
        } else {
            return MessageSender.sendMessage(chatId, text, keyboard);
        }
    }

}
