package com.github.cryptoprice.cryptopricetelegrambot.service.chat;

import com.github.cryptoprice.cryptopricetelegrambot.exception.NotFoundException;
import com.github.cryptoprice.cryptopricetelegrambot.model.Chat;
import com.github.cryptoprice.cryptopricetelegrambot.model.enums.ChatStatus;
import com.github.cryptoprice.cryptopricetelegrambot.model.enums.Exchange;
import com.github.cryptoprice.cryptopricetelegrambot.repository.ChatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ChatService {

    private static final Exchange DEFAULT_EXCHANGE = Exchange.BINANCE;
    private static final List<String> DEFAULT_FAVOURITE_COINS = List.of("BTC", "ETH", "BNB", "SOL");

    private final ChatRepository repository;

    @Transactional
    public void registerChat(Long chatId) {
        Chat chat;
        var savedChat = repository.findByChatId(chatId);
        if (savedChat.isPresent()) {
            chat = savedChat.get();
            chat.setStatus(ChatStatus.ACTIVE);
        } else {
            chat = new Chat();
            chat.setChatId(chatId);
            chat.setExchange(DEFAULT_EXCHANGE);
            chat.setStatus(ChatStatus.ACTIVE);
            chat.setFavoriteCoins(DEFAULT_FAVOURITE_COINS);
        }
        repository.save(chat);
    }

    @Transactional
    public void stopChat(Long chatId) {
        var chat = getByChatId(chatId);
        chat.setStatus(ChatStatus.STOPPED);
        repository.save(chat);
    }

    @Transactional
    public void deleteChat(Long chatId) {
        repository.deleteByChatId(chatId);
    }

    @Transactional
    public void changeExchange(Long chatId, Exchange newExchange) {
        var chat = getByChatId(chatId);
        chat.setExchange(newExchange);
        repository.save(chat);
    }

    @Transactional
    public void addFavouriteCoins(Long chatId, List<String> coinCodes) {
        var chat = getByChatId(chatId);
        var favouriteCoins = chat.getFavoriteCoins();
        for (String coinCode : coinCodes) {
            if (!favouriteCoins.contains(coinCode.toUpperCase())) {
                favouriteCoins.add(coinCode.toUpperCase());
            }
        }
        chat.setFavoriteCoins(favouriteCoins);
        repository.save(chat);
    }

    @Transactional
    public void removeFavouriteCoin(Long chatId, String coinCode) {
        var chat = getByChatId(chatId);
        var favouriteCoins = chat.getFavoriteCoins();
        favouriteCoins.remove(coinCode.toUpperCase());
        chat.setFavoriteCoins(favouriteCoins);
        repository.save(chat);
    }


    public Chat getByChatId(Long chatId) throws NotFoundException {
        var chat = repository.findByChatId(chatId);
        if (chat.isEmpty() || ChatStatus.STOPPED.equals(chat.get().getStatus())) {
            throw new NotFoundException("Chat with id " + chatId + " not exists or stopped");
        }
        return chat.get();
    }

}
