package com.github.cryptoprice.cryptopricetelegrambot.service.binance;

import com.binance.connector.client.exceptions.BinanceClientException;
import com.binance.connector.client.exceptions.BinanceConnectorException;
import com.binance.connector.client.exceptions.BinanceServerException;
import com.binance.connector.client.impl.spot.Market;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.cryptoprice.cryptopricetelegrambot.dto.binance.TickerPrice24hDto;
import com.github.cryptoprice.cryptopricetelegrambot.dto.binance.TickerPriceDto;
import com.github.cryptoprice.cryptopricetelegrambot.exception.ClientException;
import com.github.cryptoprice.cryptopricetelegrambot.exception.ExchangeServerException;
import com.github.cryptoprice.cryptopricetelegrambot.model.enums.Currency;
import com.github.cryptoprice.cryptopricetelegrambot.service.common.ExchangeService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class BinanceService implements ExchangeService {

    private final Market market;
    private final ObjectMapper objectMapper;

    @SneakyThrows(value = {JsonProcessingException.class})
    @Override
    public List<TickerPriceDto> getCoinPrice(List<String> coinCodes, Currency currency) throws ClientException, ExchangeServerException {
        var symbols = convertToSymbols(coinCodes, currency);

        LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();
        parameters.put("symbols", symbols);

        try {
            var result = market.tickerSymbol(parameters);
            return objectMapper.readValue(result, new TypeReference<>() {
            });
        } catch (BinanceClientException e) {
            e.printStackTrace();
            throw new ClientException(e.getMessage(), e.getErrorCode());
        } catch (BinanceServerException e) {
            e.printStackTrace();
            throw new ExchangeServerException(e.getMessage(), e.getHttpStatusCode());
        } catch (BinanceConnectorException e) {
            e.printStackTrace();
            throw new ExchangeServerException(e.getMessage());
        }
    }


    @SneakyThrows(value = {JsonProcessingException.class})
    @Override
    public List<TickerPrice24hDto> getCoinPriceFor24h(List<String> coinCodes, Currency currency) throws ClientException, ExchangeServerException {
        var symbols = convertToSymbols(coinCodes, currency);

        LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();
        parameters.put("symbols", symbols);
        parameters.put("type", "FULL");


        try {
            var result = market.ticker24H(parameters);
            return objectMapper.readValue(result, new TypeReference<>() {
            });
        } catch (BinanceClientException e) {
            e.printStackTrace();
            throw new ClientException(e.getMessage(), e.getErrorCode());
        } catch (BinanceServerException e) {
            e.printStackTrace();
            throw new ExchangeServerException(e.getMessage(), e.getHttpStatusCode());
        } catch (BinanceConnectorException e) {
            e.printStackTrace();
            throw new ExchangeServerException(e.getMessage());
        }
    }

    private List<String> convertToSymbols(List<String> coinCodes, Currency currency) {
        return coinCodes.stream()
                .map(code -> {
                    var currencyString = currency.toString();
                    if (code.equals(currencyString)) {
                        throw new IllegalArgumentException("coinCode and currency can`t be equals");
                    }
                    return code + currencyString;
                }).collect(Collectors.toList());
    }
}
