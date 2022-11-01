package com.github.cryptoprice.cryptopricetelegrambot.service.common.impl;

import com.github.cryptoprice.cryptopricetelegrambot.exception.NotFoundException;
import com.github.cryptoprice.cryptopricetelegrambot.model.Chat;
import com.github.cryptoprice.cryptopricetelegrambot.model.enums.Exchange;
import com.github.cryptoprice.cryptopricetelegrambot.service.chat.ChatService;
import com.github.cryptoprice.cryptopricetelegrambot.service.common.BotService;
import com.github.cryptoprice.cryptopricetelegrambot.service.common.ExchangeService;
import com.github.cryptoprice.cryptopricetelegrambot.service.common.ServiceExecutor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
@RequiredArgsConstructor
@Slf4j
public class ServiceExecutorImpl implements BotService, ServiceExecutor {

    private final HashMap<Exchange, ExchangeService> services;

    private final ChatService chatService;

    private ExchangeService getService(Long chatId) {
        Chat chat;
        try {
            chat = chatService.getByChatId(chatId);
        } catch (NotFoundException e) {
            chat = chatService.registerChat(chatId);
        }
        return services.get(chat.getExchange());
    }

    @Override
    public void registerService(ExchangeService service) {
        services.put(service.getExchange(), service);
        log.info(service.getClass().getSimpleName() + " was registered for exchange " + service.getExchange());
    }
}
