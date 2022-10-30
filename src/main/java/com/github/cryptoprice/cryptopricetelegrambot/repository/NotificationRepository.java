package com.github.cryptoprice.cryptopricetelegrambot.repository;

import com.github.cryptoprice.cryptopricetelegrambot.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
}
