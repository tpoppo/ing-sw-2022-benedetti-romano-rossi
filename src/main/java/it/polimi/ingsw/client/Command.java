package it.polimi.ingsw.client;

import it.polimi.ingsw.controller.GameState;

import java.util.ArrayList;
import java.util.List;

public class Command {
    private final String name;
    private final List<String> aliases;
    private final List<String> arguments;
    private String description;
    private final CommandType commandType;
    private final List<GameState> admittedStates;

    public Command(String name, List<String> aliases, List<String> arguments, String description, CommandType commandType, List<GameState> admittedStates) {
        this.name = name;
        this.aliases = aliases;
        this.arguments = arguments;
        this.description = description;
        this.commandType = commandType;
        this.admittedStates = admittedStates;
    }

    public Command(String name, CommandType commandType){
        this.name = name;
        this.commandType = commandType;
        this.arguments = new ArrayList<>();
        this.aliases = new ArrayList<>();
        admittedStates = new ArrayList<>();
    }

    public Command(String name, List<String> aliases, CommandType commandType){
        this.name = name;
        this.aliases = aliases;
        this.commandType = commandType;
        this.arguments = new ArrayList<>();
        admittedStates = new ArrayList<>();
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

    public void addState(GameState state){
        admittedStates.add(state);
    }

    public void addStates(List<GameState> states){
        admittedStates.addAll(states);
    }

    public String getCommandInfo(){
        String info = name + " ";

        for (String argument : arguments)
            info = info.concat("[" + argument + "] ");

        info = info + "- " + description.replaceAll(CommandHandler.NEWLINE, "\n");

        return info;
    }

    public String getSimpleInfo(){
        return getCommandInfo().substring(0, getCommandInfo().indexOf("-"));
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

    public List<GameState> getAdmittedStates() {
        return admittedStates;
    }

    public List<String> getAliases(){
        return new ArrayList<>(aliases);
    }

    public boolean checkName(String command){
        if(name.equalsIgnoreCase(command)) return true;
        for(String alias : aliases){
            if(alias.equalsIgnoreCase(command)) return true;
        }
        return false;
    }
}
