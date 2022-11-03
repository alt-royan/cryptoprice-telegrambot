package com.github.cryptoprice.cryptopricetelegrambot.bot.command;

import com.github.cryptoprice.cryptopricetelegrambot.bot.command.commandImpl.UnknownCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
@RequiredArgsConstructor
public class CommandContainer {

    private final HashMap<String, Command> commandMap;
    private final Command unknownCommand = new UnknownCommand();


    public Command retrieveCommand(String commandIdentifier) {
        return commandMap.getOrDefault(commandIdentifier, unknownCommand);
    }

    public void registerCommand(Command command) {
        commandMap.put(command.getCommandName().getCommandIdentifier(), command);
    }

}