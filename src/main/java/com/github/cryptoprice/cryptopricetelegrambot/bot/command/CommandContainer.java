package com.github.cryptoprice.cryptopricetelegrambot.bot.command;

import com.github.cryptoprice.cryptopricetelegrambot.bot.command.commandImpl.UnknownCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
@Slf4j
@RequiredArgsConstructor
public class CommandContainer {

    private final HashMap<String, Command> commandMap;
    private final Command unknownCommand = new UnknownCommand();


    public Command retrieveCommand(String commandIdentifier) {
        return commandMap.getOrDefault(commandIdentifier, unknownCommand);
    }

    public void registerCommand(Command command) {
        commandMap.put(command.getCommandName().getCommandIdentifier(), command);
        log.info("Command " + command.getClass().getSimpleName() + " was registered");
    }

}