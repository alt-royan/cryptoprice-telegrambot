package com.github.cryptoprice.cryptopricetelegrambot.model;

import com.github.cryptoprice.cryptopricetelegrambot.model.enums.Currency;
import com.github.cryptoprice.cryptopricetelegrambot.model.enums.NotificationType;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Data
public class Notification {

    @Id
    @GeneratedValue
    private Long id;

    private String coinCode;

    @Enumerated(EnumType.STRING)
    private Currency currency;

    @Enumerated(EnumType.STRING)
    private NotificationType type;

    private Double triggeredPrice;

    private Long chatId;

    @CreationTimestamp
    private Instant created;

    @Override
    public String toString() {
        return String.format("%s %s %f %s", coinCode.toUpperCase(), type.getSign(), triggeredPrice, currency.toString());
    }
}
