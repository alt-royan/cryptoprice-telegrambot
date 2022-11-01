package com.github.cryptoprice.cryptopricetelegrambot.repository;

import com.github.cryptoprice.cryptopricetelegrambot.model.Chat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChatRepository extends JpaRepository<Chat, Long> {

    Optional<Chat> findByChatId(Long chatId);

    void deleteByChatId(Long chatId);
}
