package com.github.cryptoprice.cryptopricetelegrambot.service.notification;

import com.github.cryptoprice.cryptopricetelegrambot.exception.NotFoundException;
import com.github.cryptoprice.cryptopricetelegrambot.model.Notification;
import com.github.cryptoprice.cryptopricetelegrambot.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository repository;

    public Notification getNotification(Long notificationId) {
        return repository.findById(notificationId).orElseThrow(() -> new NotFoundException("Notification not found with id " + notificationId));
    }

    public List<Notification> getAllNotifications(Long chatId) {
        return repository.findAllByChatId(chatId);
    }

    @Transactional
    public Notification createNotification(Notification notification) {
        return repository.save(notification);
    }

    @Transactional
    public void deleteNotification(Long id) {
        repository.deleteById(id);
    }

    @Transactional
    public void deleteAllNotification(Long chatId) {
        var notifications = repository.findAllByChatId(chatId);
        if (notifications != null && !notifications.isEmpty()) {
            repository.deleteAll(notifications);
        }
    }

}
