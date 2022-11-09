package com.github.cryptoprice.cryptopricetelegrambot.exception;

public abstract class EditableMessageException extends Exception {

    private Integer editableMessageId;

    protected EditableMessageException() {
        super();
        editableMessageId = null;
    }

    protected EditableMessageException(Integer editableMessageId) {
        super();
        this.editableMessageId = editableMessageId;
    }

    public boolean hasEditableMessage() {
        return editableMessageId != null;
    }

    public Integer getEditableMessageId() {
        return editableMessageId;
    }
}
