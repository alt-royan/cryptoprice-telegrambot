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
import com.github.cryptoprice.cryptopricetelegrambot.dto.common.CoinPrice24hDto;
import com.github.cryptoprice.cryptopricetelegrambot.dto.common.CoinPriceDto;
import com.github.cryptoprice.cryptopricetelegrambot.exception.ClientException;
import com.github.cryptoprice.cryptopricetelegrambot.exception.ExchangeServerException;
import com.github.cryptoprice.cryptopricetelegrambot.mapper.CoinPrice24hMapper;
import com.github.cryptoprice.cryptopricetelegrambot.mapper.CoinPriceMapper;
import com.github.cryptoprice.cryptopricetelegrambot.model.enums.Currency;
import com.github.cryptoprice.cryptopricetelegrambot.model.enums.Exchange;
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

    private final CoinPrice24hMapper coinPrice24hMapper;

    private final CoinPriceMapper coinPriceMapper;

    @SneakyThrows(value = {JsonProcessingException.class})
    @Override
    public List<CoinPriceDto> getCoinPrice(List<String> coinCodes, Currency currency) throws ClientException, ExchangeServerException {
        var symbols = convertToSymbols(coinCodes, currency);

        LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();
        parameters.put("symbols", symbols);

        try {
            var result = market.tickerSymbol(parameters);
            List<TickerPriceDto> tickerPriceList = objectMapper.readValue(result, new TypeReference<>() {
            });
            return coinPriceMapper.toCoinPriceList(tickerPriceList);
        } catch (BinanceClientException e) {
            e.printStackTrace();
            throw new ClientException(e.getMessage(), e.getErrorCode());
        } catch (BinanceServerException e) {
            e.printStackTrace();
            throw new ExchangeServerException(e.getHttpStatusCode());
        } catch (BinanceConnectorException e) {
            e.printStackTrace();
            throw new ExchangeServerException();
        }
    }


    @SneakyThrows(value = {JsonProcessingException.class})
    @Override
    public List<CoinPrice24hDto> getCoinPriceFor24h(List<String> coinCodes, Currency currency) throws ClientException, ExchangeServerException {
        var symbols = convertToSymbols(coinCodes, currency);

        LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();
        parameters.put("symbols", symbols);
        parameters.put("type", "FULL");


        try {
            var result = market.ticker24H(parameters);
            List<TickerPrice24hDto> tickerPrice24hList = objectMapper.readValue(result, new TypeReference<>() {
            });
            return coinPrice24hMapper.toCoinPrice24hList(tickerPrice24hList);
        } catch (BinanceClientException e) {
            e.printStackTrace();
            throw new ClientException(e.getMessage(), e.getErrorCode());
        } catch (BinanceServerException e) {
            e.printStackTrace();
            throw new ExchangeServerException(e.getHttpStatusCode());
        } catch (BinanceConnectorException e) {
            e.printStackTrace();
            throw new ExchangeServerException();
        }
    }

    @Override
    public Exchange getExchange() {
        return Exchange.BINANCE;
    }

    private List<String> convertToSymbols(List<String> coinCodes, Currency currency) {
        return coinCodes.stream()
                .map(code -> {
                    var coinCode = code.toUpperCase();
                    var currencyString = currency.toString().toUpperCase();
                    if (coinCode.equals(currencyString)) {
                        throw new IllegalArgumentException("coinCode and currency can`t be equals");
                    }
                    return coinCode + currencyString;
                }).collect(Collectors.toList());
    }
}
