package com.github.cryptoprice.cryptopricetelegrambot.repository;

import com.github.cryptoprice.cryptopricetelegrambot.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findAllByChatId(Long chatId);


}
