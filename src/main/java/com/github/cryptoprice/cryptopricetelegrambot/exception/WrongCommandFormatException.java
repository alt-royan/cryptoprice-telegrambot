package com.github.cryptoprice.cryptopricetelegrambot.exception;

import lombok.Getter;

public class WrongCommandFormatException extends Exception {

    private static final String MSG = "Invalid format";

    @Getter
    private final Integer editableMessageId;

    public WrongCommandFormatException() {
        super(MSG);
        this.editableMessageId = null;
    }

    public WrongCommandFormatException(Integer editableMessageId) {
        super(MSG);
        this.editableMessageId = editableMessageId;
    }
}
