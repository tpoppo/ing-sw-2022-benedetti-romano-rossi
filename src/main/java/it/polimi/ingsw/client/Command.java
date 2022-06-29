package it.polimi.ingsw.client;

import it.polimi.ingsw.controller.GameState;

import java.util.ArrayList;
import java.util.List;

/**
 * This class helps to define a command in the CLI shell and to check which command are usable in each state
 */
public class Command {
    private final String name;
    private final List<String> aliases;
    private final List<String> arguments;
    private String description;
    private final CommandType commandType;
    private final List<GameState> admittedStates;

    /**
     * Constructor, creates a Command with the given name and sets its type.
     *
     * @param name the name of the command.
     * @param commandType the type of the command.
     */
    public Command(String name, CommandType commandType){
        this.name = name;
        this.commandType = commandType;
        this.arguments = new ArrayList<>();
        this.aliases = new ArrayList<>();
        admittedStates = new ArrayList<>();
    }

    public void addArgument(String argument){
        arguments.add(argument);
    }

    public void addAlias(String alias){
        aliases.add(alias);
    }

    public void addState(GameState state){
        admittedStates.add(state);
    }

    public void addStates(List<GameState> states){
        admittedStates.addAll(states);
    }

    /**
     * It returns command info string
     * @return command info
     */
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

    /**
     * Checks whether the command is call the with given command name.
     * @param command command name
     * @return true if the name is callable with the given name
     */
    public boolean checkName(String command){
        if(name.equalsIgnoreCase(command)) return true;
        for(String alias : aliases){
            if(alias.equalsIgnoreCase(command)) return true;
        }
        return false;
    }
}
