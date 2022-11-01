CREATE TABLE chat
(
    id           BIGINT PRIMARY KEY,
    chat_id      BIGINT UNIQUE NOT NULL,
    exchange     varchar(20)   NOT NULL,
    status       varchar(20)   NOT NULL,
    chat_started timestamp     NOT NULL
);

CREATE TABLE chat_coin
(
    chat_id   BIGINT      NOT NULL,
    coin_code varchar(20) NOT NULL
);

CREATE TABLE notification
(
    id              BIGINT PRIMARY KEY,
    coin_code       varchar(50)      NOT NULL,
    currency        varchar(50)      NOT NULL,
    type            varchar(50)      NOT NULL,
    triggered_price double precision NOT NULL,
    chat_id         BIGINT           NOT NULL,
    created         timestamp        NOT NULL
);

ALTER TABLE notification
    ADD CONSTRAINT chat_id_fk FOREIGN KEY (chat_id) references chat (id);
ALTER TABLE chat_coin
    ADD CONSTRAINT chat_id_fk FOREIGN KEY (chat_id) references chat (id);