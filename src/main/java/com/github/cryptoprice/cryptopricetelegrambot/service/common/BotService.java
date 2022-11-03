package com.github.cryptoprice.cryptopricetelegrambot.service.common;

import com.github.cryptoprice.cryptopricetelegrambot.exception.ClientException;
import com.github.cryptoprice.cryptopricetelegrambot.exception.ExchangeServerException;
import com.github.cryptoprice.cryptopricetelegrambot.model.enums.Currency;
import com.github.cryptoprice.cryptopricetelegrambot.model.enums.Exchange;

import java.util.List;

public interface BotService {

    String checkCoinPrice(Long chatId, String coinCode, Currency currency) throws ClientException, ExchangeServerException;

    String compareDiffExchangePrice(Long chatId, String coinCode, Currency currency) throws ClientException, ExchangeServerException;

    String checkFavouriteCoinsPrice(Long chatId, Currency currency) throws ClientException, ExchangeServerException;

    String setExchange(Long chatId, Exchange exchange) throws ClientException, ExchangeServerException;

    String getExchange(Long chatId) throws ClientException, ExchangeServerException;

    String addFavouriteCoins(Long chatId, List<String> coinCodes) throws ClientException, ExchangeServerException;

    String removeFavouriteCoins(Long chatId, List<String> coinCodes) throws ClientException, ExchangeServerException;

    String getFavouriteCoins(Long chatId) throws ClientException, ExchangeServerException;

    String createNotification(Long chatId, String request) throws ClientException, ExchangeServerException;

    String removeNotification(Long chatId, Long notificationId) throws ClientException, ExchangeServerException;

    String getActiveNotifications(Long chatId) throws ClientException, ExchangeServerException;


}
