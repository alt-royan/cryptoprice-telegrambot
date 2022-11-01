package com.github.cryptoprice.cryptopricetelegrambot.service.notification;

import com.github.cryptoprice.cryptopricetelegrambot.model.Notification;
import com.github.cryptoprice.cryptopricetelegrambot.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository repository;


    public List<Notification> getAllNotifications(Long chatId) {
        return repository.findAllByChatId(chatId);
    }

    public Notification createNotification(Long chatId, String request) {
        var notification = NotificationParser.parseNotificationCreateRequest(request);
        notification.setChatId(chatId);
        return repository.save(notification);
    }

    public void deleteNotification(Long id) {
        repository.deleteById(id);
    }
}
