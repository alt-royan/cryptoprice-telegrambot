package com.github.cryptoprice.cryptopricetelegrambot.service.scheduling;

import com.github.cryptoprice.cryptopricetelegrambot.service.common.BotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class NotificationScheduler {

    private final BotService service;

    private static final String TASK_NAME = "checkNotificationCondition";

    @Scheduled(fixedDelay = 5000L)
    public void checkNotifications() {
        service.checkNotifications();
    }

}
