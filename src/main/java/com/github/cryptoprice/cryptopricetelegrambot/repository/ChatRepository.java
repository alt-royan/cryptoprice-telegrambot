package com.github.cryptoprice.cryptopricetelegrambot.repository;

import com.github.cryptoprice.cryptopricetelegrambot.model.Chat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRepository extends JpaRepository<Chat, Long> {
}
