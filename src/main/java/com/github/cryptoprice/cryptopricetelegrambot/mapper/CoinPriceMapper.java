package com.github.cryptoprice.cryptopricetelegrambot.mapper;

import com.github.cryptoprice.cryptopricetelegrambot.dto.CoinPriceDto;
import org.knowm.xchange.dto.marketdata.Ticker;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", uses = {UtilMapper.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CoinPriceMapper {

    @Mapping(target = "coinCode", source = "instrument", qualifiedByName = "fromInstrumentToCoinCode")
    @Mapping(target = "currency", source = "instrument", qualifiedByName = "fromInstrumentToCurrency")
    CoinPriceDto toCoinPrice(Ticker source);

    List<CoinPriceDto> toCoinPriceList(List<Ticker> source);

}
