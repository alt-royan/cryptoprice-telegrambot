package com.github.cryptoprice.cryptopricetelegrambot.mapper;

import com.github.cryptoprice.cryptopricetelegrambot.dto.binance.TickerPrice24hDto;
import com.github.cryptoprice.cryptopricetelegrambot.dto.common.CoinPrice24hDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", uses = {UtilMapper.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CoinPrice24hMapper {

    @Mapping(target = "coinCode", source = "symbol", qualifiedByName = "fromBinanceSymbolToCoinCode")
    @Mapping(target = "currency", source = "symbol", qualifiedByName = "fromBinanceSymbolToCurrency")
    CoinPrice24hDto toCoinPrice24h(TickerPrice24hDto source);

    List<CoinPrice24hDto> toCoinPrice24hList(List<TickerPrice24hDto> source);

}
