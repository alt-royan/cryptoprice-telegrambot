package com.github.cryptoprice.cryptopricetelegrambot.service.common.impl;

import com.github.cryptoprice.cryptopricetelegrambot.dto.common.CoinPrice24hDto;
import com.github.cryptoprice.cryptopricetelegrambot.dto.common.CoinPriceDto;
import com.github.cryptoprice.cryptopricetelegrambot.exception.*;
import com.github.cryptoprice.cryptopricetelegrambot.model.Chat;
import com.github.cryptoprice.cryptopricetelegrambot.model.Notification;
import com.github.cryptoprice.cryptopricetelegrambot.model.enums.Currency;
import com.github.cryptoprice.cryptopricetelegrambot.model.enums.Exchange;
import com.github.cryptoprice.cryptopricetelegrambot.model.enums.NotificationType;
import com.github.cryptoprice.cryptopricetelegrambot.service.chat.ChatService;
import com.github.cryptoprice.cryptopricetelegrambot.service.common.BotService;
import com.github.cryptoprice.cryptopricetelegrambot.service.common.ExchangeService;
import com.github.cryptoprice.cryptopricetelegrambot.service.common.ServiceExecutor;
import com.github.cryptoprice.cryptopricetelegrambot.service.notification.NotificationParser;
import com.github.cryptoprice.cryptopricetelegrambot.service.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class ServiceExecutorImpl implements BotService, ServiceExecutor {

    private final HashMap<Exchange, ExchangeService> exchangeMap;

    private final ChatService chatService;

    private final NotificationService notificationService;

    private ExchangeService getExchangeService(Long chatId) {
        var chat = chatService.getByChatId(chatId);
        return exchangeMap.get(chat.getExchange());
    }

    private Chat checkChatIsRegistered(Long chatId) {
        Chat chat;
        try {
            chat = chatService.getByChatId(chatId);
        } catch (NotFoundException e) {
            chat = chatService.registerChat(chatId);
        }
        return chat;
    }

    @Override
    public void registerService(ExchangeService service) {
        exchangeMap.put(service.getExchange(), service);
        log.info(service.getClass().getSimpleName() + " was registered for exchange " + service.getExchange());
    }


    @Override
    public void registerChat(Long chatId) {
        chatService.registerChat(chatId);
    }

    @Override
    public CoinPrice24hDto getCoinPrice24h(Long chatId, String coinCode, Currency currency) throws ExchangeServerException, NoCoinOnExchangeException {
        checkChatIsRegistered(chatId);
        var exchangeService = getExchangeService(chatId);
        try {
            return exchangeService.getCoinPriceFor24h(coinCode, currency);
        } catch (ClientException e) {
            throw new NoCoinOnExchangeException(coinCode.toUpperCase(), exchangeService.getExchange());
        }
    }

    @Override
    public CoinPriceDto getCoinPrice(Long chatId, String coinCode, Currency currency) throws ExchangeServerException, NoCoinOnExchangeException {
        checkChatIsRegistered(chatId);
        var exchangeService = getExchangeService(chatId);
        try {
            return exchangeService.getCoinPrice(coinCode, currency);
        } catch (ClientException e) {
            throw new NoCoinOnExchangeException(coinCode.toUpperCase(), exchangeService.getExchange());
        }
    }

    /**
     * Return the map type <{@link Exchange}, {@link Object}> where key is exchange and value can be {@link CoinPrice24hDto} or instance of {@link RuntimeException}
     */
    @Override
    public Map<Exchange, Object> getPriceAllExchanges(Long chatId, String coinCode, Currency currency) {
        checkChatIsRegistered(chatId);
        Map<Exchange, Object> coinPriceMap = new HashMap<>();
        for (ExchangeService service : exchangeMap.values()) {
            try {
                var coinPriceDto = service.getCoinPriceFor24h(coinCode, currency);
                coinPriceMap.put(service.getExchange(), coinPriceDto);
            } catch (ClientException e) {
                coinPriceMap.put(service.getExchange(), new NoCoinOnExchangeException(coinCode, service.getExchange()));
            } catch (ExchangeServerException e) {
                coinPriceMap.put(service.getExchange(), e);
            }
        }

        return coinPriceMap;
    }

    /**
     * Return the map type <{@link String}, {@link Object}> where key is coinCode and value can be {@link CoinPrice24hDto} or {@link RuntimeException}
     */
    @Override
    public Map<String, Object> getFavouriteCoinsPrice(Long chatId, Currency currency) {
        var chat = checkChatIsRegistered(chatId);
        Map<String, Object> coinPriceMap = new HashMap<>();
        var favouriteCoins = chat.getFavoriteCoins();
        var exchangeService = getExchangeService(chat.getChatId());

        for (String coinCode : favouriteCoins) {
            try {
                var coinPrice = exchangeService.getCoinPriceFor24h(coinCode, currency);
                coinPriceMap.put(coinCode, coinPrice);
            } catch (ClientException e) {
                coinPriceMap.put(coinCode, new NoCoinOnExchangeException(coinCode, exchangeService.getExchange()));
            } catch (ExchangeServerException e) {
                coinPriceMap.put(coinCode, e);
            }
        }

        return coinPriceMap;
    }


    @Override
    public void setExchange(Long chatId, Exchange exchange) {
        checkChatIsRegistered(chatId);
        chatService.changeExchange(chatId, exchange);
    }

    @Override
    public Exchange getExchange(Long chatId) {
        var chat = checkChatIsRegistered(chatId);
        return chat.getExchange();
    }

    @Override
    public void addFavouriteCoins(Long chatId, List<String> coinCodes) throws ExchangeServerException, NoCoinOnExchangeException {
        checkChatIsRegistered(chatId);
        var exchangeService = getExchangeService(chatId);
        for (String coinCode : coinCodes) {
            try {
                exchangeService.getCoinPrice(coinCode, Currency.USDT);
            } catch (ClientException e) {
                throw new NoCoinOnExchangeException(coinCode.toUpperCase(), exchangeService.getExchange());
            }
        }
        chatService.addFavouriteCoins(chatId, coinCodes);
    }


    @Override
    public void removeFavouriteCoin(Long chatId, String coinCode) {
        checkChatIsRegistered(chatId);
        chatService.removeFavouriteCoin(chatId, coinCode);
    }

    @Override
    public List<String> getFavouriteCoins(Long chatId) {
        var chat = checkChatIsRegistered(chatId);
        return chat.getFavoriteCoins();
    }

    @Override
    public Notification createNotification(Long chatId, String request) throws WrongNotificationFormatException, NotSupportedCurrencyException, ExchangeServerException, NoCoinOnExchangeException, NotificationConditionAlreadyDoneException {
        checkChatIsRegistered(chatId);
        var exchangeService = getExchangeService(chatId);
        var notification = NotificationParser.parseNotificationCreateRequest(request);
        notification.setChatId(chatId);
        CoinPriceDto coinPrice;
        try {
            coinPrice = exchangeService.getCoinPrice(notification.getCoinCode(), notification.getCurrency());
        } catch (ClientException e) {
            throw new NoCoinOnExchangeException(notification.getCoinCode().toUpperCase(), exchangeService.getExchange());
        }
        if (NotificationType.LESS_THAN.equals(notification.getType()) && coinPrice.getPrice() < notification.getTriggeredPrice() ||
                NotificationType.MORE_THAN.equals(notification.getType()) && coinPrice.getPrice() > notification.getTriggeredPrice()) {
            throw new NotificationConditionAlreadyDoneException(notification);
        }
        return notificationService.createNotification(notification);
    }

    @Override
    public void removeNotification(Long chatId, Long notificationId) {
        checkChatIsRegistered(chatId);
        notificationService.deleteNotification(notificationId);
    }

    @Override
    public List<Notification> getActiveNotifications(Long chatId) {
        checkChatIsRegistered(chatId);
        return notificationService.getAllNotifications(chatId);
    }

    @Override
    public void stopChat(Long chatId) {
        checkChatIsRegistered(chatId);
        chatService.stopChat(chatId);
    }

    @Override
    public void deleteChat(Long chatId) {
        chatService.deleteChat(chatId);
        notificationService.deleteAllNotification(chatId);
    }
}
