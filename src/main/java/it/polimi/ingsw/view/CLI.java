package it.polimi.ingsw.view;

import it.polimi.ingsw.client.ClientSocket;
import it.polimi.ingsw.controller.Game;
import it.polimi.ingsw.controller.GameHandler;
import it.polimi.ingsw.controller.LobbyHandler;
import it.polimi.ingsw.controller.LobbyPlayer;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.board.*;
import it.polimi.ingsw.model.characters.Character;
import it.polimi.ingsw.network.messages.*;
import it.polimi.ingsw.utils.Constants;
import it.polimi.ingsw.utils.ReducedLobby;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.fusesource.jansi.Ansi.ansi;

public class CLI {
    final protected Logger LOGGER = Logger.getLogger(getClass().getName());
    final protected ClientSocket client_socket;
    final protected PrintStream out;
    final protected Scanner read_stream;

    protected GameHandler gameHandler;
    protected Game model;
    protected String username;
    protected Player schoolBoardPlayer;

    public CLI(ClientSocket client_socket, PrintStream out, InputStream read_stream) {
        this.client_socket = client_socket;
        this.out = out;
        this.read_stream = new Scanner(new BufferedInputStream(read_stream));
    }
    public CLI(ClientSocket client_socket) {
        this(client_socket, System.out, System.in);
    }

    public void run(){
        AnsiConsole.systemInstall();

        String username;

        clearScreen();
        out.println(Constants.ERIANTYS);
        out.println(Constants.AUTHORS);
        out.println();

        out.println(ansi().fg(Ansi.Color.GREEN).a("LOGIN").reset());
        out.print("Username: ");

        username = read_stream.nextLine();
        while(!client_socket.login(username)) {
            // TODO: better error handling
            out.println(ansi().fg(Ansi.Color.RED).a("Username already taken").reset());
            username = read_stream.nextLine();
        }
        out.println("Logged in");

        this.username = username;

        renderState();

        while(true){
            getInput();
        }
    }

    private void getInput(){
        read_stream.reset();
        String input = read_stream.nextLine();
        String out = parseInput(input);
        this.out.println(out);
    }

    /**
     * Clears the screen.
     */
    protected void clearScreen(){
        // clear the console
        out.println(ansi().cursor(0, 0).eraseScreen());
        out.flush();
    }

    private void renderState(){
        new Thread(() -> {
            while(true){
                ViewContent view;
                synchronized (client_socket.mutex){
                    while(client_socket.getView() == null) {
                        try {
                            client_socket.mutex.wait();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }

                view = client_socket.getView();

                LOGGER.log(Level.INFO, "Rendered view: {0}", view);

                clearScreen();

                // print server errors
                if(view.getErrorMessage() != null) out.println(view.getErrorMessage());

                if(view.getCurrentHandler() == null){ // we are in the menu
                    printMenu();
                    out.print("\n> ");
                } else{
                    switch (view.getCurrentHandler()) {
                        case LOBBY -> { // we are in the lobby
                            printLobby();
                            out.print("\n> ");
                        }
                        case GAME -> { // we are in the game
                            this.gameHandler = client_socket.getView().getGameHandler();
                            this.model = gameHandler.getModel();
                            printGame();
                            print(ansi().a("> ").reset(), 48, 1);
                        }
                    }
                }

                synchronized (client_socket.mutex){
                    client_socket.setView(null);
                    client_socket.mutex.notifyAll();
                }
            }
        }).start();
    }

    protected void printMenu() {
        out.println(Constants.MENU);
        ViewContent view = client_socket.getView();
        ArrayList<ReducedLobby> reduced_lobbies = view.getLobbies();

        if(!reduced_lobbies.isEmpty())
            out.println(ansi().bold().a("Available lobbies:").reset());
        else out.println(ansi().fgRed().a("No lobbies available!").reset());

        for(ReducedLobby reduced_lobby : reduced_lobbies){
            out.printf("Lobby %d: Slots: %d/%d\n", reduced_lobby.getID(), reduced_lobby.getNumPlayer(), reduced_lobby.getMaxPlayers());
        }
        out.println();
        out.println("create <max players> - create a new lobby");
        out.println("join <lobby id> - join the lobby with ID <lobby id>");
    }

    protected void printLobby(){
        out.println(Constants.LOBBY);

        ViewContent view = client_socket.getView();

        // print the list of the players in the lobby
        LobbyHandler lobby_handler = view.getLobbyHandler();
        ArrayList<LobbyPlayer> players = lobby_handler.getPlayers();

        // TODO: add lobby id inside lobby
        out.printf("Players: %d/%d\n", players.size(), lobby_handler.getMaxPlayers());
        for(LobbyPlayer lobby_player : players){
            Integer wizard = lobby_player.getWizard();
            out.printf("%s (Wizard: %s)\n", lobby_player.getUsername().equals(username) ?
                    ansi().bg(Ansi.Color.WHITE).fg(Ansi.Color.BLACK).a(username).reset() : lobby_player.getUsername(),
                    wizard
            );
        }
        out.println();
        out.println("start <expert mode> - start the game");
        out.println("wizard <id> - choose wizard");
    }

    protected void printGame(){
        print(ansi().a(Constants.ERIANTYS), 1, 1);

        // Banner length is 63
        print(ansi().bg(Ansi.Color.WHITE).fg(Ansi.Color.BLACK).a(username).reset(), 2, 80);

        printCurrentPlayer();
        printState();
        printIslands();
        printClouds();
        printAssistants();

        // print board
        if(schoolBoardPlayer == null)
            schoolBoardPlayer = model.getCurrentPlayer();

        printBoard(schoolBoardPlayer);

        if(model.getExpertMode()) {
            printCoins();
            printCharacters();
        }
    }

    private void printCurrentPlayer(){
        StringBuilder text = new StringBuilder();

        if(model.getCurrentPlayer().getUsername().equals(username))
            text.append(ansi().bold().fg(Ansi.Color.GREEN).a("IT'S YOUR TURN!").reset());
        else
            text.append(ansi()
                        .bold()
                        .a("TURN: ")
                        .reset()
                        .a(model.getCurrentPlayer().getUsername())
            );

        print(ansi().a(text.toString()).reset(), 10, 1);
    }

    private void printState(){
        // TODO:
        StringBuilder stateText = new StringBuilder();

        stateText.append(String.format("Current state: %s-%s-%s-%d",
                gameHandler.getCurrentState(),
                gameHandler.isActionCompleted(),
                gameHandler.getSavedState(),
                gameHandler.getStudentMoves())).append(Constants.NEWLINE);

        print(ansi().a(stateText.toString()).reset(), 11, 1);
    }

    private void printClouds(){
        StringBuilder cloudsText = new StringBuilder();

        cloudsText.append(ansi().bold().a("CLOUDS").reset()).append(Constants.NEWLINE);

        int count = 0;
        for(Students cloud : model.getClouds()){
            cloudsText.append("Cloud ").append(count).append(": ");

            for(Color key : cloud.keySet()){
                if(cloud.get(key) > 0)
                    cloudsText.append(ansi().fg(Ansi.Color.valueOf(key.toString())).a(cloud.get(key) + " ").reset());
            }

            cloudsText.append(Constants.NEWLINE);
            count++;
        }

        print(ansi().a(cloudsText.toString()).reset(), 14, 50);
    }

    private void printIslands(){
        StringBuilder islandStr = new StringBuilder();

        islandStr.append(ansi().bold().a("ISLANDS").reset()).append(Constants.NEWLINE);

        for(int i=0; i<model.getIslands().size(); i++){
            Island island = model.getIslands().get(i);
            Students islandStudents = island.getStudents();

            islandStr.append(i).append(" (").append(island.getNumIslands()).append("): ");
            if(island.hasMotherNature())
                islandStr.append(ansi().bg(Ansi.Color.YELLOW).fg(Ansi.Color.BLACK).a("M").reset().a(" "));
            if(island.getNoEntryTiles() > 0)
                islandStr.append(ansi().bg(Ansi.Color.RED).fg(Ansi.Color.BLACK).a(island.getNoEntryTiles()).reset());
            islandStr.append("  ");

            for(Color studentColor : island.getStudents().keySet()){
                if(islandStudents.get(studentColor) > 0)
                    islandStr.append(ansi().fg(Ansi.Color.valueOf(studentColor.toString())).a(islandStudents.get(studentColor) + " ").reset());
            }
            islandStr.append(" ");
            islandStr.append(island.getOwner() == null ?
                    "" :
                    island.getOwner().getUsername() + " - Towers: " + island.getNumTowers()
            );
            islandStr.append(Constants.NEWLINE);
        }

        print(ansi().a(islandStr.toString()).reset(), 14, 1);
    }

    private void printBoard(Player player){
        StringBuilder boardStr = new StringBuilder();

        boardStr.append(ansi().bold().a("SCHOOLBOARD").reset()).append(Constants.NEWLINE);

        Students entranceStudents = player.getSchoolBoard().getEntranceStudents();
        Students diningStudents = player.getSchoolBoard().getDiningStudents();
        int numTowers = player.getSchoolBoard().getNumTowers();
        Professors professors = player.getProfessors();

        boardStr.append("Towers: ").append(numTowers).append(Constants.NEWLINE);
        boardStr.append("Entrance: ");
        for(Color studentColor : entranceStudents.keySet()){
            if(entranceStudents.get(studentColor) > 0)
                boardStr.append(ansi().fg(Ansi.Color.valueOf(studentColor.toString())).a(entranceStudents.get(studentColor) + " ").reset());
        }
        boardStr.append(Constants.NEWLINE).append(Constants.NEWLINE);

        boardStr.append("Dining room:").append(Constants.NEWLINE).append(Constants.NEWLINE);
        for(Color studentColor : diningStudents.keySet()){
            boardStr.append(ansi().bg(Ansi.Color.valueOf(studentColor.toString())).fg(Ansi.Color.BLACK));

            int numOfStudents = diningStudents.get(studentColor);

            for(int i = 0; i< Game.MAX_DINING_STUDENTS; i++){
                if(numOfStudents > 0)
                    boardStr.append("X");
                else if(i > 0 && (i - 2) % 3 == 0) {
                    boardStr.append("C");
                }else boardStr.append("_");
                boardStr.append("   ");

                numOfStudents--;
            }

            if(professors.contains(studentColor))
                boardStr.append("P");

            boardStr.append(ansi().a(Constants.NEWLINE + Constants.NEWLINE).reset());
        }

        print(ansi().a(boardStr.toString()).reset(), 29, 1);
    }

    private void printAssistants(){
        StringBuilder assistantStr = new StringBuilder();

        ArrayList<Assistant> assistants = model.usernameToPlayer(username).getPlayerHand();
        Assistant currentAssistant = model.usernameToPlayer(username).getCurrentAssistant();

        assistantStr.append(ansi().bold().a("ASSISTANTS").reset()).append(Constants.NEWLINE);
        int count = 0;
        // iterating on all assistants
        for(Assistant assistant : Assistant.getAssistants(1)){
            // current assistant is green
            if(assistant.equals(currentAssistant))
                assistantStr.append(ansi().bgBrightGreen().fgBrightDefault());

            // already played assistants are red
            if(!assistants.contains(assistant))
                assistantStr.append(ansi().bgRed().fgBrightDefault());

            assistantStr.append(count).append(": ");
            assistantStr.append("P ").append(assistant.getPower()).append(" - ").append("M ").append(assistant.getSteps());
            assistantStr.append(ansi().reset());
            assistantStr.append(Constants.NEWLINE);

            count++;
        }

        print(ansi().a(assistantStr.toString()).reset(), 29, 50);
    }

    private void printCoins(){
        StringBuilder coinsStr = new StringBuilder();

        int coins = model.getCurrentPlayer().getCoins();

        coinsStr.append(ansi().bold().a("COINS: ").reset());
        coinsStr.append(ansi().fgBrightYellow().a(coins));

        print(ansi().a(coinsStr.toString()).reset(), 30, 33);
    }

    private void printCharacters(){
        StringBuilder text = new StringBuilder();

        ArrayList<Character> characters = model.getCharacters();
        text.append(ansi().bold().a("CHARACTERS").reset()).append(Constants.NEWLINE);
        for(Character character : characters){
            // TODO: print character's specific features (entry tiles, students, ...)
            text.append(character.getClass().getName()).append(" - Cost: ").append(character.getCost());
            text.append(Constants.NEWLINE);
        }

        print(ansi().a(text.toString()).reset(), 40, 19);
    }

    private String parseInput(String s){
        String[] command = s.split(" ");
        if(command.length == 0) return "No command found";
        switch (command[0]) {
            // menu commands
            case "create" -> {
                if (command.length != 2) return "Invalid number of arguments";
                client_socket.send(new CreateLobbyMessage(Integer.parseInt(command[1])));
                return "";
            }
            case "join" -> {
                if (command.length != 2) return "Invalid number of arguments";
                client_socket.send(new JoinLobbyMessage(Integer.parseInt(command[1])));
                return "";
            }

            // lobby commands
            case "start" -> {
                if (command.length != 2) return "Invalid number of arguments";
                client_socket.send(new StartGameMessage(Boolean.parseBoolean(command[1])));
                return "";
            }
            case "wizard" -> {
                if (command.length != 2) return "Invalid number of arguments";
                client_socket.send(new ChooseWizardMessage(Integer.parseInt(command[1])));
                return "";
            }

            // game commands
            case "ac" -> { // ActivateCharacterMessage
                Character selected_character = client_socket.getView().getGameHandler().getSelectedCharacter();
                PlayerChoicesSerializable player_choices_serializable = new PlayerChoicesSerializable();

                switch (selected_character.require()) {
                    case ISLAND -> {
                        if (command.length != 2) return "Invalid number of arguments";
                        player_choices_serializable.setIsland(Integer.parseInt(command[1]));
                    }
                    case CARD_STUDENT, STUDENT_COLOR, SWAP_DINING_ENTRANCE, SWAP_CARD_ENTRANCE -> {
                        if (command.length < 2) return "Invalid number of arguments";
                        ArrayList<Color> colors = new ArrayList<>();
                        for (int i = 1; i < command.length; i++) {
                            colors.add(Color.parseColor(command[i]));
                        }
                        player_choices_serializable.setStudent(colors);
                    }
                }

                client_socket.send(new ActivateCharacterMessage(player_choices_serializable));
                return "";
            }

            case "cc" -> {
                if (command.length != 2) return "Invalid number of arguments";
                client_socket.send(new ChooseCloudMessage(Integer.parseInt(command[1])));
                return "";
            }

            case "mm" -> {
                if (command.length != 2) return "Invalid number of arguments";
                client_socket.send(new MoveMotherNatureMessage(Integer.parseInt(command[1])));
                return "";
            }

            case "ms" -> {
                Integer island_position = null;
                if (command.length > 3 || command.length < 2) return "Invalid number of arguments";
                if(command.length == 3) island_position = Integer.parseInt(command[2]);
                client_socket.send(new MoveStudentMessage(Color.parseColor(command[1]), island_position));
            }

            case "ns" -> {
                if(command.length != 1) return "Invalid number of arguments";
                client_socket.send(new NextStateMessage());
                return "";
            }

            case "pa" -> {
                if(command.length != 2) return "Invalid number of arguments";
                client_socket.send(new PlayAssistantMessage(Integer.parseInt(command[1])));
                return "";
            }

            case "sc" -> {
                if(command.length != 2) return "Invalid number of arguments";
                client_socket.send(new SelectedCharacterMessage(Integer.parseInt(command[1])));
                return "";
            }

        }

        return "Invalid Command";
    }

    private void print(Ansi s, int row, int column) {
        out.print(ansi().cursor(row, column).a(
                        s.toString().replaceAll(
                                Constants.NEWLINE,
                                ansi().a(Constants.NEWLINE).cursorRight(column - 1).toString()
                        )
                )
        );
        out.flush();
    }
}
