package com.github.cryptoprice.cryptopricetelegrambot.service.common.impl;

import com.github.cryptoprice.cryptopricetelegrambot.dto.CoinPriceDto;
import com.github.cryptoprice.cryptopricetelegrambot.exception.*;
import com.github.cryptoprice.cryptopricetelegrambot.model.Chat;
import com.github.cryptoprice.cryptopricetelegrambot.model.Notification;
import com.github.cryptoprice.cryptopricetelegrambot.model.enums.Exchange;
import com.github.cryptoprice.cryptopricetelegrambot.model.enums.NotificationType;
import com.github.cryptoprice.cryptopricetelegrambot.service.chat.ChatService;
import com.github.cryptoprice.cryptopricetelegrambot.service.common.BotService;
import com.github.cryptoprice.cryptopricetelegrambot.service.common.ExchangeService;
import com.github.cryptoprice.cryptopricetelegrambot.service.common.ServiceExecutor;
import com.github.cryptoprice.cryptopricetelegrambot.service.exchange.BinanceService;
import com.github.cryptoprice.cryptopricetelegrambot.service.notification.NotificationParser;
import com.github.cryptoprice.cryptopricetelegrambot.service.notification.NotificationService;
import com.github.cryptoprice.cryptopricetelegrambot.utils.MessageSender;
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

    private final String NOTIFICATION_MESSAGE = "Внимание! %s стал %s чем %f %s";

    private final HashMap<Exchange, ExchangeService> exchangeMap;

    private final ChatService chatService;

    private final NotificationService notificationService;

    private ExchangeService getExchangeService(Long chatId) {
        var chat = chatService.getByChatId(chatId);
        return exchangeMap.get(chat.getExchange());
    }

    private ExchangeService getExchangeService(Exchange exchange) {
        return exchangeMap.get(exchange);
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
    public CoinPriceDto getCoinPrice(Long chatId, String coinCode, String currency) throws ExchangeServerException, NoCoinPairOnExchangeException, CurrencyEqualsCodeException {
        checkChatIsRegistered(chatId);
        var exchangeService = getExchangeService(chatId);
        return exchangeService.getCoinPrice(coinCode, currency);
    }

    /**
     * Return the map type <{@link Exchange}, {@link Object}> where key is exchange and value can be {@link CoinPriceDto} or instance of {@link RuntimeException}
     */
    @Override
    public Map<Exchange, Object> getPriceAllExchanges(Long chatId, String coinCode, String currency) throws CurrencyEqualsCodeException {
        checkChatIsRegistered(chatId);
        Map<Exchange, Object> coinPriceMap = new HashMap<>();
        for (ExchangeService service : exchangeMap.values()) {
            try {
                var coinPriceDto = service.getCoinPrice(coinCode, currency);
                coinPriceMap.put(service.getExchange(), coinPriceDto);
            } catch (NoCoinPairOnExchangeException | ExchangeServerException e) {
                coinPriceMap.put(service.getExchange(), e);
            }
        }

        return coinPriceMap;
    }

    /**
     * Return the map type <{@link String}, {@link Object}> where key is coinCode and value can be {@link CoinPriceDto} or {@link RuntimeException}
     */
    @Override
    public Map<String, Object> getFavouriteCoinsPrice(Long chatId, String currency) throws CurrencyEqualsCodeException {
        var chat = checkChatIsRegistered(chatId);
        Map<String, Object> coinPriceMap = new HashMap<>();
        var favouriteCoins = chat.getFavoriteCoins();
        var exchangeService = getExchangeService(chat.getChatId());

        for (String coinCode : favouriteCoins) {
            try {
                var coinPrice = exchangeService.getCoinPrice(coinCode, currency);
                coinPriceMap.put(coinCode, coinPrice);
            } catch (ExchangeServerException | NoCoinPairOnExchangeException e) {
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
    public void addFavouriteCoins(Long chatId, List<String> coinCodes) throws ExchangeServerException, NoCoinPairOnExchangeException, CurrencyEqualsCodeException {
        checkChatIsRegistered(chatId);
        var exchangeService = getExchangeService(chatId);
        for (String coinCode : coinCodes) {
            exchangeService.getCoinPrice(coinCode, "USDT");
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
    public Notification createNotification(Long chatId, String request) throws WrongNotificationFormatException, ExchangeServerException, NoCoinPairOnExchangeException, NotificationConditionAlreadyDoneException, CurrencyEqualsCodeException {
        checkChatIsRegistered(chatId);
        var exchangeService = getExchangeService(Exchange.BINANCE);
        var notification = NotificationParser.parseNotificationCreateRequest(request);
        notification.setChatId(chatId);
        var coinPrice = exchangeService.getCoinPrice(notification.getCoinCode().toUpperCase(), notification.getCurrency());
        if (NotificationType.LESS_THAN.equals(notification.getType()) && coinPrice.getLast().compareTo(notification.getTriggeredPrice()) < 0 ||
                NotificationType.MORE_THAN.equals(notification.getType()) && coinPrice.getLast().compareTo(notification.getTriggeredPrice()) > 0) {
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

    @Override
    public void checkNotifications() {
        var service = (BinanceService) getExchangeService(Exchange.BINANCE);
        List<CoinPriceDto> prices;
        try {
            prices = service.getAllCoinPrice();
        } catch (NoCoinPairOnExchangeException | ExchangeServerException e) {
            throw new RuntimeException(e);
        }

        var notifications = notificationService.getAll();
        for (Notification n : notifications) {
            var price = prices.stream()
                    .filter(p -> n.getCurrency().equals(p.getCurrency().getCurrencyCode())
                            && n.getCoinCode().equals(p.getCoinCode().getCurrencyCode()))
                    .findFirst().orElseThrow(() -> new RuntimeException("No such currency pair on Binance: " + n.getCoinCode() + "_" + n.getCurrency()));
            switch (n.getType()) {
                case LESS_THAN:
                    if (price.getLast().compareTo(n.getTriggeredPrice()) < 0) {
                        MessageSender.sendMessage(n.getChatId(), String.format(NOTIFICATION_MESSAGE, n.getCoinCode(), n.getType().toString(), n.getTriggeredPrice(), n.getCurrency()));
                        notificationService.deleteNotification(n.getId());
                    }
                    break;
                case MORE_THAN:
                    if (price.getLast().compareTo(n.getTriggeredPrice()) > 0) {
                        MessageSender.sendMessage(n.getChatId(), String.format(NOTIFICATION_MESSAGE, n.getCoinCode(), n.getType().toString(), n.getTriggeredPrice(), n.getCurrency()));
                        notificationService.deleteNotification(n.getId());
                    }
                    break;
            }
        }
    }
}
