package com.github.cryptoprice.cryptopricetelegrambot.service.common.impl;

import com.github.cryptoprice.cryptopricetelegrambot.dto.common.CoinPrice24hDto;
import com.github.cryptoprice.cryptopricetelegrambot.exception.ClientException;
import com.github.cryptoprice.cryptopricetelegrambot.exception.ExchangeServerException;
import com.github.cryptoprice.cryptopricetelegrambot.exception.NotFoundException;
import com.github.cryptoprice.cryptopricetelegrambot.model.Chat;
import com.github.cryptoprice.cryptopricetelegrambot.model.enums.Currency;
import com.github.cryptoprice.cryptopricetelegrambot.model.enums.Exchange;
import com.github.cryptoprice.cryptopricetelegrambot.service.chat.ChatService;
import com.github.cryptoprice.cryptopricetelegrambot.service.common.BotService;
import com.github.cryptoprice.cryptopricetelegrambot.service.common.ExchangeService;
import com.github.cryptoprice.cryptopricetelegrambot.service.common.ServiceExecutor;
import com.github.cryptoprice.cryptopricetelegrambot.service.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ServiceExecutorImpl implements BotService, ServiceExecutor {

    private final HashMap<Exchange, ExchangeService> exchangeMap;

    private final ChatService chatService;

    private final NotificationService notificationService;

    private ExchangeService getExchangeService(Long chatId) {
        Chat chat;
        try {
            chat = chatService.getByChatId(chatId);
        } catch (NotFoundException e) {
            chat = chatService.registerChat(chatId);
        }
        return exchangeMap.get(chat.getExchange());
    }

    @Override
    public void registerService(ExchangeService service) throws ClientException, ExchangeServerException {
        exchangeMap.put(service.getExchange(), service);
        log.info(service.getClass().getSimpleName() + " was registered for exchange " + service.getExchange());
    }


    @Override
    public String checkCoinPrice(Long chatId, String coinCode, Currency currency) throws ClientException, ExchangeServerException {
        var coinPrice24h = getExchangeService(chatId).getCoinPriceFor24h(coinCode, currency);
        return coinPrice24h.toString();
    }

    @Override
    public String compareDiffExchangePrice(Long chatId, String coinCode, Currency currency) throws ClientException, ExchangeServerException {
        List<CoinPrice24hDto> coinPrice24hList = new ArrayList<>();
        for (ExchangeService service : exchangeMap.values()) {
            coinPrice24hList.add(service.getCoinPriceFor24h(coinCode, currency));
        }
        return coinPrice24hList.toString();
    }

    @Override
    public String checkFavouriteCoinsPrice(Long chatId, Currency currency) throws ClientException, ExchangeServerException {
        var chat = chatService.getByChatId(chatId);
        var favouriteCoins = chat.getFavoriteCoins();
        var coinPrices = getExchangeService(chatId).getCoinPriceFor24h(favouriteCoins, currency);
        return coinPrices.toString();
    }

    @Override
    public String setExchange(Long chatId, Exchange exchange) throws ClientException, ExchangeServerException {
        chatService.changeExchange(chatId, exchange);
        return "Биржа изменена";
    }

    @Override
    public String getExchange(Long chatId) throws ClientException, ExchangeServerException {
        var chat = chatService.getByChatId(chatId);
        var exchange = chat.getExchange();
        return "Текущая биржа: " + exchange.getName();
    }

    @Override
    public String addFavouriteCoins(Long chatId, List<String> coinCodes) throws ClientException, ExchangeServerException {
        return null;
    }

    @Override
    public String removeFavouriteCoins(Long chatId, List<String> coinCodes) throws ClientException, ExchangeServerException {
        for (String coinCode : coinCodes) {
            chatService.removeFavouriteCoin(chatId, coinCode);
        }
        return "Удалено";
    }

    @Override
    public String getFavouriteCoins(Long chatId) throws ClientException, ExchangeServerException {
        var chat = chatService.getByChatId(chatId);
        var favouriteCoins = chat.getFavoriteCoins();
        return favouriteCoins.toString();
    }

    @Override
    public String createNotification(Long chatId, String request) throws ClientException, ExchangeServerException {
        return null;
    }

    @Override
    public String removeNotification(Long chatId, Long notificationId) throws ClientException, ExchangeServerException {
        return null;
    }

    @Override
    public String getActiveNotifications(Long chatId) throws ClientException, ExchangeServerException {
        var notifications = notificationService.getAllNotifications(chatId);
        return notifications.toString();
    }
}
