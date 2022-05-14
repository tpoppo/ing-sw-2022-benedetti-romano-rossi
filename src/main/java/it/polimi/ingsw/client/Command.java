package it.polimi.ingsw.client;

import java.util.ArrayList;
import java.util.List;

public class Command {
    private final String name;
    private final List<String> aliases;
    private final List<String> arguments;
    private String description;
    private final CommandType commandType;

    public Command(String name, List<String> aliases, List<String> arguments, String description, CommandType commandType) {
        this.name = name;
        this.aliases = aliases;
        this.arguments = arguments;
        this.description = description;
        this.commandType = commandType;
    }

    public Command(String name, CommandType commandType){
        this.name = name;
        this.commandType = commandType;
        this.arguments = new ArrayList<>();
        this.aliases = new ArrayList<>();
    }

    public Command(String name, List<String> aliases, CommandType commandType){
        this.name = name;
        this.aliases = aliases;
        this.commandType = commandType;
        this.arguments = new ArrayList<>();
    }

    public void addArgument(String argument){
        arguments.add(argument);
    }

    public void addAlias(String alias){
        aliases.add(alias);
    }

    public void addAliases(List<String> aliases){
        this.aliases.addAll(aliases);
    }

    public List<String> getArguments() {
        return arguments;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public CommandType getCommandType() {
        return commandType;
    }
}
