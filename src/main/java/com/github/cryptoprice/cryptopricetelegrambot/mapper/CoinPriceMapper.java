package com.github.cryptoprice.cryptopricetelegrambot.mapper;

import com.github.cryptoprice.cryptopricetelegrambot.dto.binance.TickerPriceDto;
import com.github.cryptoprice.cryptopricetelegrambot.dto.common.CoinPriceDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", uses = {UtilMapper.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CoinPriceMapper {

    @Mapping(target = "coinCode", source = "symbol", qualifiedByName = "fromBinanceSymbolToCoinCode")
    @Mapping(target = "currency", source = "symbol", qualifiedByName = "fromBinanceSymbolToCurrency")
    CoinPriceDto toCoinPrice(TickerPriceDto source);

    List<CoinPriceDto> toCoinPriceList(List<TickerPriceDto> source);

}
