package it.polimi.ingsw.client;


import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.board.Color;
import it.polimi.ingsw.model.characters.Character;
import it.polimi.ingsw.network.HandlerType;
import it.polimi.ingsw.network.messages.*;
import it.polimi.ingsw.utils.Constants;
import it.polimi.ingsw.view.CLI;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CommandHandler {
    private static List<Command> commands = null;
    private static final String NEWLINE = "<--NEWLINE-->";

    private static void createCommands(){
        Command clean = new Command("clean", CommandType.GENERAL);
        clean.setDescription("Cleans errors from the screen");
        commands.add(clean);

        Command board = new Command("board", CommandType.GENERAL);
        board.addArgument("number");
        board.setDescription("Displays the [" + board.getArguments().get(0) + "] board");
        commands.add(board);

        Command help = new Command("help", CommandType.GENERAL);
        help.setDescription("Displays the help screen.");
        commands.add(help);

        Command exit = new Command("exit", CommandType.GENERAL);
        exit.setDescription("Exit from the help screen.");
        commands.add(exit);

        Command create = new Command("create", CommandType.MENU);
        create.addArgument("lobby size");
        create.setDescription("Creates a lobby of the given size.");
        commands.add(create);

        Command join = new Command("join", CommandType.MENU);
        join.addArgument("lobby id");
        join.setDescription("Joins the specified lobby.");
        commands.add(join);

        Command start = new Command("start", CommandType.LOBBY);
        start.addArgument("expert mode");
        start.setDescription("Starts the game. Expert mode can be either 0 or 1.");
        commands.add(start);

        Command wizard = new Command("wizard", CommandType.LOBBY);
        wizard.addArgument("number");
        wizard.setDescription("Selects the wizard (1 - 4).");
        commands.add(wizard);

        Command cloud = new Command("cloud", CommandType.GAME);
        cloud.addArgument("number");
        cloud.setDescription("Selects the [" + cloud.getArguments().get(0) + "] cloud.");
        commands.add(cloud);

        Command mm = new Command("mothernature", CommandType.GAME);
        mm.addArgument("island number");
        mm.setDescription("Moves mother nature to the given [" + mm.getArguments().get(0) + "] island.");
        mm.addAlias("mm");
        commands.add(mm);

        Command ms = new Command("student", CommandType.GAME);
        ms.addArgument("color");
        ms.addArgument("island number");
        ms.setDescription("Moves a student of the color [" + ms.getArguments().get(0) + "] " +
                "from the entrance to the dining room." + NEWLINE +
                "If [" + ms.getArguments().get(1) + "] is specified, it instead moves the student to the selected island.");
        ms.addAlias("ms");
        commands.add(ms);

        Command pass = new Command("pass", CommandType.GAME);
        pass.setDescription("Passes the turn.");
        commands.add(pass);

        Command assistant = new Command("assistant", CommandType.GAME);
        assistant.addArgument("number");
        assistant.setDescription("Selects the [" + assistant.getArguments().get(0) + "] assistant from the list.");
        commands.add(assistant);

        Command character = new Command("character", CommandType.GAME);
        character.addArgument("number");
        character.setDescription("Selects the character that you want to be played");
        commands.add(character);

        Command activate = new Command("activate", CommandType.GAME);
        activate.addArgument("requirements");
        activate.setDescription("Activates the selected character." + NEWLINE +
                "The [" + activate.getArguments().get(0)+ "] vary character to character.");
        commands.add(activate);
    }

    private static void normalizeDescriptions(){
        /*
        Optional<Integer> maxLen;

        maxLen = commands.stream()
                .map(command -> command.getName().length() + command.getArguments().stream().mapToInt(String::length)
                .sum())
                .reduce((a, b) -> a > b ? a : b);

        maxLen.ifPresent(len -> {

        });
        */

        String replacement = "\n";
        for(int i=0; i<41; i++)
            replacement = replacement.concat(" ");

        String finalReplacement = replacement;
        commands.forEach(command -> {
            String normalizedDescription = command.getDescription().replaceAll(NEWLINE, finalReplacement);
            command.setDescription(normalizedDescription);
        });
    }

    public static List<Command> getCommands() {
        if(commands == null){
            commands = new ArrayList<>();
            createCommands();
            normalizeDescriptions();
        }

        return commands;
    }

    public static String sendInput(String inputCommand, ClientSocket client_socket, CLI cli){
        String[] command = inputCommand.split(" ");

        if(command.length == 0) return "No command found";
        switch (command[0]) {
            // clientside commands
            case "clean" -> { // removes the error message (and cleans errors displayed with printErrorRelative)
                cli.cleanErrorMessage();

                return null;
            }

            case "board" -> {
                if(cli.getView().getCurrentHandler() != HandlerType.GAME)
                    return "You don't have a board... yet";

                if (command.length == 1) {// own board
                    cli.setSchoolboardPlayerUsername(client_socket.getUsername());
                }else if (command.length == 2) {// requested board
                     Optional<Player> requestedPlayer = cli.getView().getGameHandler().getModel().getPlayers().stream()
                                                            .filter(player -> player.getUsername().equals(command[1]))
                                                            .findFirst();

                     if(requestedPlayer.isEmpty())
                         return "Player not found";
                     else cli.setSchoolboardPlayerUsername(requestedPlayer.get().getUsername());
                }else return "Invalid number of arguments";

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

            // menu commands
            case "create" -> {
                if (command.length != 2) return "Invalid number of arguments";
                try {
                    client_socket.send(new CreateLobbyMessage(Integer.parseInt(command[1])));
                } catch(NumberFormatException e){
                    return "Invalid input";
                }
                return null;
            }

            case "join" -> {
                if (command.length != 2) return "Invalid number of arguments";
                try{
                    client_socket.send(new JoinLobbyMessage(Integer.parseInt(command[1])));
                } catch(NumberFormatException e){
                    return "Invalid input";
                }
                return null;
            }

            // lobby commands
            case "start" -> {
                if (command.length != 2) return "Invalid number of arguments";
                boolean boolean_command = false;
                if(Constants.TRUE_STRING.contains(command[1].toLowerCase())) boolean_command = true;
                else if(!Constants.FALSE_STRING.contains(command[1].toLowerCase())) return "Invalid input.";
                client_socket.send(new StartGameMessage(boolean_command));
                return null;
            }

            case "wizard" -> {
                if (command.length != 2) return "Invalid number of arguments";
                try{
                    client_socket.send(new ChooseWizardMessage(Integer.parseInt(command[1])));
                } catch(NumberFormatException e){
                    return "Invalid input";
                }
                return null;
            }

            // game commands
            case "activate" -> { // ActivateCharacterMessage
                Character selected_character = client_socket.getView().getGameHandler().getSelectedCharacter();
                PlayerChoicesSerializable player_choices_serializable = new PlayerChoicesSerializable();

                switch (selected_character.require()) {
                    case ISLAND -> {
                        if (command.length != 2) return "Invalid number of arguments";
                        try{
                            player_choices_serializable.setIsland(Integer.parseInt(command[1]));
                        } catch(NumberFormatException e){
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
                }

                client_socket.send(new ActivateCharacterMessage(player_choices_serializable));

                return null;
            }

            case "cloud" -> {
                if (command.length != 2) return "Invalid number of arguments";
                try {
                    client_socket.send(new ChooseCloudMessage(Integer.parseInt(command[1])));
                } catch(NumberFormatException e){
                    return "Invalid input";
                }

                return null;
            }

            // FIXME: maybe it's better to specify the number of steps (instead of the arrival island's id)
            case "mm" -> {
                if (command.length != 2) return "Invalid number of arguments";
                try {
                    client_socket.send(new MoveMotherNatureMessage(Integer.parseInt(command[1])));
                } catch(NumberFormatException e){
                    return "Invalid input";
                }

                return null;
            }

            case "ms" -> {
                Integer island_position = null;
                if (command.length > 3 || command.length < 2) return "Invalid number of arguments";
                if (command.length == 3) {
                    try {
                        island_position = Integer.parseInt(command[2]);
                    } catch(NumberFormatException e){
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
                if (command.length != 1) return "Invalid number of arguments";
                client_socket.send(new NextStateMessage());
                return null;
            }

            case "assistant" -> {
                if (command.length != 2) return "Invalid number of arguments";
                try {
                    client_socket.send(new PlayAssistantMessage(Integer.parseInt(command[1])));
                } catch(NumberFormatException e){
                    return "Invalid input";
                }

                return null;
            }

            case "character" -> {
                if (command.length != 2) return "Invalid number of arguments";
                try{
                    client_socket.send(new SelectedCharacterMessage(Integer.parseInt(command[1])));
                } catch(NumberFormatException e){
                    return "Invalid input";
                }
                return null;
            }
        }

        return "Invalid command";
    }
}
