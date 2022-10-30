package com.github.cryptoprice.cryptopricetelegrambot.model;

import com.github.cryptoprice.cryptopricetelegrambot.model.enums.ChatStatus;
import com.github.cryptoprice.cryptopricetelegrambot.model.enums.Exchange;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.Instant;
import java.util.List;

@Entity
@Data
public class Chat {

    @Id
    private Long id;

    private Long chatId;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "chat_coin", joinColumns = @JoinColumn(name = "chat_id"))
    @Column(name = "coin_code")
    private List<String> favoriteCoins;

    @OneToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REMOVE})
    @JoinTable(name = "chat_notification",
            joinColumns = @JoinColumn(name = "chat_id"),
            inverseJoinColumns = @JoinColumn(name = "notification_id"))
    private List<Notification> notifications;

    @Enumerated(EnumType.STRING)
    private Exchange exchange;

    @Enumerated(EnumType.STRING)
    private ChatStatus status;

    @CreationTimestamp
    private Instant chatStarted;
}
