package com.github.cryptoprice.cryptopricetelegrambot.exception;

public class WrongCommandFormatException extends EditableMessageException {

    public WrongCommandFormatException() {
        super();
    }

    public WrongCommandFormatException(Integer editableMessageId) {
        super(editableMessageId);
    }
}
