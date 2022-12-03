package com.github.cryptoprice.cryptopricetelegrambot.service.common;

import com.github.cryptoprice.cryptopricetelegrambot.dto.CoinPriceDto;
import com.github.cryptoprice.cryptopricetelegrambot.exception.*;
import com.github.cryptoprice.cryptopricetelegrambot.model.Notification;
import com.github.cryptoprice.cryptopricetelegrambot.model.enums.Exchange;

import java.util.List;
import java.util.Map;

public interface BotService {

    void registerChat(Long chatId);

    CoinPriceDto getCoinPrice(Long chatId, String coinCode, String currency) throws ExchangeServerException, NoCoinPairOnExchangeException, CurrencyEqualsCodeException;

    Map<Exchange, Object> getPriceAllExchanges(Long chatId, String coinCode, String currency) throws CurrencyEqualsCodeException;

    Map<String, Object> getFavouriteCoinsPrice(Long chatId, String currency) throws CurrencyEqualsCodeException;


    void setExchange(Long chatId, Exchange exchange);

    Exchange getExchange(Long chatId);


    void addFavouriteCoins(Long chatId, List<String> coinCodes) throws ExchangeServerException, NoCoinPairOnExchangeException, CurrencyEqualsCodeException;

    void removeFavouriteCoin(Long chatId, String coinCodes);

    List<String> getFavouriteCoins(Long chatId);


    Notification createNotification(Long chatId, String request) throws WrongNotificationFormatException, ExchangeServerException, NoCoinPairOnExchangeException, NotificationConditionAlreadyDoneException, CurrencyEqualsCodeException;

    void removeNotification(Long chatId, Long notificationId);

    List<Notification> getActiveNotifications(Long chatId);

    void stopChat(Long chatId);

    void deleteChat(Long chatId);

    void checkNotifications();


}
