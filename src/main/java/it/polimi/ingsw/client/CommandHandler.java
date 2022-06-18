package it.polimi.ingsw.client;


import it.polimi.ingsw.controller.GameState;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.board.Color;
import it.polimi.ingsw.model.characters.Character;
import it.polimi.ingsw.network.HandlerType;
import it.polimi.ingsw.network.messages.*;
import it.polimi.ingsw.utils.Constants;
import it.polimi.ingsw.view.CLI;
import it.polimi.ingsw.view.CLIArt;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * This class manage command received by the CLI (both the Cli and the CliArt)
 */
public class CommandHandler {
    private static final List<Command> commands = new ArrayList<>();
    protected static final String NEWLINE = "<--NEWLINE-->";

    /**
     * Creates and defines the available commands.
     */
    public static void createCommands(){
        // General clientside commands
        Command clean = new Command("clean", CommandType.GENERAL);
        clean.setDescription("Cleans errors from the screen");
        commands.add(clean);

        Command board = new Command("board", CommandType.GENERAL);
        board.addArgument("username");
        board.setDescription("Displays [" + board.getArguments().get(0) + "]'s board");
        commands.add(board);

        Command help = new Command("help", CommandType.GENERAL);
        help.setDescription("Displays the help screen.");
        commands.add(help);

        Command exit = new Command("exit", CommandType.GENERAL);
        exit.setDescription("Exit from the help screen.");
        commands.add(exit);

        Command characterInfo = new Command("characterinfo", CommandType.GENERAL);
        characterInfo.setDescription("Displays detailed info about the characters.");
        characterInfo.addAlias("cinfo");
        commands.add(characterInfo);


        // Menu commands
        Command create = new Command("create", CommandType.MENU);
        create.addArgument("lobby size");
        create.setDescription("Creates a lobby of the given size (2 - 3).");
        commands.add(create);

        Command join = new Command("join", CommandType.MENU);
        join.addArgument("lobby id");
        join.setDescription("Joins the specified lobby.");
        commands.add(join);


        // Lobby commands
        Command start = new Command("start", CommandType.LOBBY);
        start.addArgument("expert mode");
        start.setDescription("Starts the game. Expert mode can be either 0 or 1.");
        commands.add(start);

        Command wizard = new Command("wizard", CommandType.LOBBY);
        wizard.addArgument("number");
        wizard.setDescription("Selects the wizard (1 - 4).");
        commands.add(wizard);


        // Game commands
        Command cloud = new Command("cloud", CommandType.GAME);
        cloud.addArgument("number");
        cloud.setDescription("Selects the [" + cloud.getArguments().get(0) + "] cloud.");
        cloud.addState(GameState.CHOOSE_CLOUD);
        commands.add(cloud);

        Command mm = new Command("mothernature", CommandType.GAME);
        mm.addArgument("island number");
        mm.setDescription("Moves mother nature to the given [" + mm.getArguments().get(0) + "] island.");
        mm.addAlias("mm");
        mm.addState(GameState.MOVE_MOTHER_NATURE);
        commands.add(mm);

        Command ms = new Command("student", CommandType.GAME);
        ms.addArgument("color");
        ms.addArgument("island number");
        ms.setDescription("Moves a student of the color [" + ms.getArguments().get(0) + "] " +
                "from the entrance to the dining room." + NEWLINE +
                "If [" + ms.getArguments().get(1) + "] is specified, it moves the student to the selected island instead.");
        ms.addAlias("ms");
        ms.addState(GameState.MOVE_STUDENT);
        commands.add(ms);

        Command pass = new Command("pass", CommandType.GAME);
        pass.setDescription("Passes the turn.");
        pass.addAlias("p");
        pass.addStates(List.of(GameState.MOVE_MOTHER_NATURE, GameState.CHOOSE_CLOUD, GameState.ACTIVATE_CHARACTER, GameState.MOVE_STUDENT, GameState.PLAY_ASSISTANT));
        commands.add(pass);

        Command assistant = new Command("assistant", CommandType.GAME);
        assistant.addArgument("number");
        assistant.setDescription("Selects the [" + assistant.getArguments().get(0) + "] assistant from the list.");
        assistant.addState(GameState.PLAY_ASSISTANT);
        commands.add(assistant);

        Command character = new Command("character", CommandType.GAME);
        character.addArgument("number");
        character.setDescription("Selects the character that you want to be played");
        pass.addStates(List.of(GameState.MOVE_MOTHER_NATURE, GameState.CHOOSE_CLOUD, GameState.MOVE_STUDENT, GameState.PLAY_ASSISTANT));
        commands.add(character);

        Command activate = new Command("activate", CommandType.GAME);
        activate.addArgument("requirements");
        activate.setDescription("Activates the selected character." + NEWLINE +
                "The [" + activate.getArguments().get(0)+ "] vary character to character.");
        activate.addState(GameState.ACTIVATE_CHARACTER);
        commands.add(activate);
    }

    /**
     * Normalizes the length of the command's description.
     *
     * @param command the command whose description is to be normalized.
     * @return the String representing the normalized description.
     */
    public static String normalizeDescription(Command command){
        String replacement = "\n";
        for(int i=0; i<51; i++)
            replacement = replacement.concat(" ");
        String finalReplacement = replacement;

        return command.getDescription().replace(NEWLINE, finalReplacement);
    }

    public static List<Command> getCommands() {
        return commands;
    }

    /**
     * Finds and returns the Command with the given name
     *
     * @param name the name of the command to be searched.
     * @return the Command with the given name.
     */
    public static Command get(String name){
        return commands.stream().filter(command -> command.getName().equals(name)).findFirst().get();
    }

    /**
     * Parses and sends the given command.
     *
     * @param inputCommand command that must be parsed
     * @param client_socket where to send the message
     * @param cli the cli that sent the input
     * @return null if there are no errors otherwise the string with an error
     */
    public static String sendInput(String inputCommand, ClientSocket client_socket, CLI cli){
        String[] command = inputCommand.split(" ");
        if(command.length == 0) return "No command found";

        List<Command> commands = getCommands();
        Optional<Command> optionalCommand = commands.stream().filter(x -> x.checkName(command[0])).findFirst();
        if(optionalCommand.isPresent()) {
            command[0] = optionalCommand.get().getName();
            switch (command[0]) {
                // clientside commands
                case "clean" -> { // removes the error message (and cleans errors displayed with printErrorRelative)
                    cli.cleanErrorMessage();

                    return null;
                }

                case "board" -> {
                    if (cli.getView().getCurrentHandler() != HandlerType.GAME)
                        return "You don't have a board... yet";

                    if(!(cli instanceof CLIArt))
                        return "Error: this command is only available in CLIArt";

                    if (command.length == 1) {// own board
                        cli.setSchoolboardPlayerUsername(client_socket.getUsername());
                    } else if (command.length == 2) {// requested board
                        Optional<Player> requestedPlayer = cli.getView().getGameHandler().getModel().getPlayers().stream()
                                .filter(player -> player.getUsername().equals(command[1]))
                                .findFirst();

                        if (requestedPlayer.isEmpty())
                            return "Player not found";
                        else cli.setSchoolboardPlayerUsername(requestedPlayer.get().getUsername());
                    } else return "Invalid number of arguments";

                    cli.printRequestedBoard();

                    return null;
                }

                case "help" -> {
                    cli.printHelpScreen();

                    return null;
                }

                case "exit" -> {
                    cli.refresh();

                    return null;
                }

                case "characterinfo" -> {
                    if(cli.getView().getCurrentHandler() == HandlerType.GAME) {
                        if (cli.getView().getGameHandler().getModel().getExpertMode())
                            cli.printCharacterInfo();
                        else return "This command is only available in expert mode!";
                    }else return "This command is only available in game!";

                    return null;
                }

                // menu commands
                case "create" -> {
                    if(cli.getView().getCurrentHandler() != null) return "You cannot use this command here";
                    if (command.length != 2) return "Invalid number of arguments";

                    try {
                        client_socket.send(new CreateLobbyMessage(Integer.parseInt(command[1])));
                    } catch (NumberFormatException e) {
                        return "Invalid input";
                    }
                    return null;
                }

                case "join" -> {
                    if(cli.getView().getCurrentHandler() != null) return "You cannot use this command here";
                    if (command.length != 2) return "Invalid number of arguments";

                    try {

                        client_socket.send(new JoinLobbyMessage(Integer.parseInt(command[1])));
                    } catch (NumberFormatException e) {
                        return "Invalid input";
                    }
                    return null;
                }

                // lobby commands
                case "start" -> {
                    if(cli.getView().getCurrentHandler() != HandlerType.LOBBY) return "You cannot use this command here";
                    if (command.length != 2) return "Invalid number of arguments";

                    boolean boolean_command = false;
                    if (Constants.TRUE_STRING.contains(command[1].toLowerCase())) boolean_command = true;
                    else if (!Constants.FALSE_STRING.contains(command[1].toLowerCase())) return "Invalid input.";
                    client_socket.send(new StartGameMessage(boolean_command));
                    return null;
                }

                case "wizard" -> {
                    if(cli.getView().getCurrentHandler() != HandlerType.LOBBY) return "You cannot use this command here";
                    if (command.length != 2) return "Invalid number of arguments";

                    try {
                        client_socket.send(new ChooseWizardMessage(Integer.parseInt(command[1])));
                    } catch (NumberFormatException e) {
                        return "Invalid input";
                    }
                    return null;
                }

                // game commands
                case "activate" -> { // ActivateCharacterMessage
                    if(cli.getView().getCurrentHandler() != HandlerType.GAME) return "You cannot use this command here";

                    Character selected_character = cli.getView().getGameHandler().getSelectedCharacter();
                    PlayerChoicesSerializable player_choices_serializable = new PlayerChoicesSerializable();
                    if (selected_character == null) return "You need to select a character first.";

                    switch (selected_character.require()) {
                        case ISLAND -> {
                            if (command.length != 2) return "Invalid number of arguments";
                            try {
                                player_choices_serializable.setIsland(Integer.parseInt(command[1]));
                            } catch (NumberFormatException e) {
                                return "Invalid input";
                            }
                        }
                        case CARD_STUDENT, STUDENT_COLOR, SWAP_DINING_ENTRANCE, SWAP_CARD_ENTRANCE -> {
                            if (command.length < 2) return "Invalid number of arguments";
                            ArrayList<Color> colors = new ArrayList<>();
                            for (int i = 1; i < command.length; i++) {
                                colors.add(Color.parseColor(command[i]));
                            }
                            if (colors.contains(null))
                                return "Invalid input. Must be RED, GREEN, BLUE, YELLOW or MAGENTA (case insensitive).";
                            player_choices_serializable.setStudent(colors);
                        }
                        case MOVE_CARD_ISLAND -> {
                            if (command.length != 3) return "Invalid number of arguments";
                            int island_pos = Integer.parseInt(command[1]);
                            player_choices_serializable.setIsland(island_pos);
                            Color color = Color.parseColor(command[2]);
                            if(color == null) return "Invalid input. Must be RED, GREEN, BLUE, YELLOW or MAGENTA (case insensitive).";
                            player_choices_serializable.setStudent(color);
                        }
                        case NOTHING -> {
                            if (command.length != 3) return "Invalid number of arguments";
                        }
                        default -> {
                            return "This character does not exist!"; // this line should be unreachable :)
                        }
                    }

                    client_socket.send(new ActivateCharacterMessage(player_choices_serializable));

                    return null;
                }

                case "cloud" -> {
                    if(cli.getView().getCurrentHandler() != HandlerType.GAME) return "You cannot use this command here";
                    if (command.length != 2) return "Invalid number of arguments";

                    try {
                        client_socket.send(new ChooseCloudMessage(Integer.parseInt(command[1])));
                    } catch (NumberFormatException e) {
                        return "Invalid input";
                    }

                    return null;
                }

                case "mothernature" -> {
                    if (command.length != 2) return "Invalid number of arguments";
                    if(cli.getView().getCurrentHandler() != HandlerType.GAME) return "You cannot use this command here";

                    try {
                        client_socket.send(new MoveMotherNatureMessage(Integer.parseInt(command[1])));
                    } catch (NumberFormatException e) {
                        return "Invalid input";
                    }

                    return null;
                }

                case "student" -> {
                    if(cli.getView().getCurrentHandler() != HandlerType.GAME) return "You cannot use this command here";

                    Integer island_position = null;
                    if (command.length > 3 || command.length < 2) return "Invalid number of arguments";
                    if (command.length == 3) {
                        try {
                            island_position = Integer.parseInt(command[2]);
                        } catch (NumberFormatException e) {
                            return "Invalid input";
                        }
                    }

                    Color color = Color.parseColor(command[1]);
                    if (color == null)
                        return "Invalid input. Must be RED, GREEN, BLUE, YELLOW or MAGENTA (case insensitive).";

                    client_socket.send(new MoveStudentMessage(Color.parseColor(command[1]), island_position));

                    return null;
                }

                case "pass" -> {
                    if(cli.getView().getCurrentHandler() != HandlerType.GAME) return "You cannot use this command here";
                    if (command.length != 1) return "Invalid number of arguments";
                    client_socket.send(new NextStateMessage());
                    return null;
                }

                case "assistant" -> {
                    if(cli.getView().getCurrentHandler() != HandlerType.GAME) return "You cannot use this command here";
                    if (command.length != 2) return "Invalid number of arguments";
                    try {
                        client_socket.send(new PlayAssistantMessage(Integer.parseInt(command[1])));
                    } catch (NumberFormatException e) {
                        return "Invalid input";
                    }

                    return null;
                }

                case "character" -> {
                    if(cli.getView().getCurrentHandler() != HandlerType.GAME) return "You cannot use this command here";
                    if (command.length != 2) return "Invalid number of arguments";

                    try {
                        client_socket.send(new SelectedCharacterMessage(Integer.parseInt(command[1])));
                    } catch (NumberFormatException e) {
                        return "Invalid input";
                    }
                    return null;
                }
                default -> {
                    return "Command not implemented"; // this should be impossible
                }
            }
        }
        return "Invalid command";
    }
}
