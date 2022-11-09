package com.github.cryptoprice.cryptopricetelegrambot.exception;

import lombok.Getter;

public class AnyRuntimeException extends CommonException {

    @Getter
    private final String message = "Ошибка. Попробуйте ещё раз";

}
