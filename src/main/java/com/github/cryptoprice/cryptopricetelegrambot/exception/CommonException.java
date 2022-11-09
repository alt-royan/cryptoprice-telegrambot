package com.github.cryptoprice.cryptopricetelegrambot.exception;

import com.github.cryptoprice.cryptopricetelegrambot.utils.MessageSender;

/**
 * Abstract class for all exceptions
 */
public abstract class CommonException extends Exception {

    private final Integer editableMessageId;

    protected CommonException() {
        super();
        this.editableMessageId = null;
    }

    protected CommonException(Integer editableMessageId) {
        super();
        this.editableMessageId = editableMessageId;
    }

    public boolean hasEditableMessage() {
        return editableMessageId != null;
    }

    public Integer getEditableMessageId() {
        return editableMessageId;
    }

    @Override
    public abstract String getMessage();

    public void handleMessage(long chatId) {
        if (hasEditableMessage()) {
            MessageSender.editMessage(chatId, this.getEditableMessageId(), this.getMessage());
        } else {
            MessageSender.sendMessage(chatId, this.getMessage());
        }
    }
}
