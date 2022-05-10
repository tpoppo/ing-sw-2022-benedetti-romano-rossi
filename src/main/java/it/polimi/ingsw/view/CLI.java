package it.polimi.ingsw.view;

import it.polimi.ingsw.client.ClientSocket;
import it.polimi.ingsw.controller.Game;
import it.polimi.ingsw.controller.GameHandler;
import it.polimi.ingsw.controller.LobbyHandler;
import it.polimi.ingsw.controller.LobbyPlayer;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.board.Color;
import it.polimi.ingsw.model.board.Professors;
import it.polimi.ingsw.model.board.Students;
import it.polimi.ingsw.model.characters.Character;
import it.polimi.ingsw.network.messages.*;
import it.polimi.ingsw.model.board.Island;
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
                } else{
                    switch (view.getCurrentHandler()) {
                        case LOBBY -> // we are in the lobby
                                printLobby();
                        case GAME -> {
                            this.gameHandler = client_socket.getView().getGameHandler();
                            this.model = gameHandler.getModel();
                            printGame();
                        }// we are in the game

                    }
                }
                out.println();
                out.print("> ");

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

        out.println("Available lobbies:");
        for(ReducedLobby reduced_lobby : reduced_lobbies){
            out.printf("Lobby %d: Slots: %d/%d\n", reduced_lobby.getID(), reduced_lobby.getNumPlayer(), reduced_lobby.getMaxPlayers());
        }
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
        out.println("start <expert mode> - start the game");
        out.println("wizard <id> - choose wizard");
    }

    protected void printGame(){
        out.print(Constants.ERIANTYS);
        out.println();
        out.println();

        out.println(ansi().bg(Ansi.Color.WHITE).fg(Ansi.Color.BLACK).a(username).reset());
        out.println();

        printCurrentPlayer();
        printState();
        out.println();

        printClouds();
        out.println();
        out.println();

        printIslands();
        out.println();
        out.println();

        // print board
        if(schoolBoardPlayer == null)
            schoolBoardPlayer = model.getCurrentPlayer();

        printBoard(schoolBoardPlayer);
        out.println();
    }

    private void printCurrentPlayer(){
        if(model.getCurrentPlayer().getUsername().equals(username))
            out.println(ansi().bold().fg(Ansi.Color.GREEN).a("IT'S YOUR TURN!").reset());
        else
            out.println(ansi()
                        .bold()
                        .a("TURN: ")
                        .reset()
                        .a(model.getCurrentPlayer().getUsername())
            );
    }

    private void printState(){
        out.printf("Current state: %s-%s-%s-%d\n",
                gameHandler.getCurrentState(),
                gameHandler.isActionCompleted(),
                gameHandler.getSavedState(),
                gameHandler.getStudentMoves());
    }

    private void printClouds(){
        out.println(ansi().bold().a("CLOUDS").reset());

        int count = 0;
        for(Students cloud : model.getClouds()){
            out.print("Cloud " + count + ": ");

            for(Color key : cloud.keySet()){
                if(cloud.get(key) > 0)
                    out.print(ansi().fg(Ansi.Color.valueOf(key.toString())).a(cloud.get(key) + " ").reset());
            }

            out.println();
            count++;
        }
    }

    private void printIslands(){
        out.println(ansi().bold().a("ISLANDS").reset());

        for(int i=0; i<model.getIslands().size(); i++){
            Island island = model.getIslands().get(i);
            Students islandStudents = island.getStudents();

            out.print(i + " (" + island.getNumIslands() + "): ");
            if(island.hasMotherNature())
                out.print(ansi().bg(Ansi.Color.YELLOW).fg(Ansi.Color.BLACK).a("M").reset().a(" "));
            if(island.getNoEntryTiles() > 0)
                out.print(ansi().bg(Ansi.Color.RED).fg(Ansi.Color.BLACK).a(island.getNoEntryTiles()).reset());
            out.print("\t\t");

            for(Color studentColor : island.getStudents().keySet()){
                if(islandStudents.get(studentColor) > 0)
                    out.print(ansi().fg(Ansi.Color.valueOf(studentColor.toString())).a(islandStudents.get(studentColor) + " ").reset());
            }
            out.print("\t");
            out.println(island.getOwner() == null ?
                    "" :
                    island.getOwner().getUsername() + " - Towers: " + island.getNumTowers()
            );
        }
    }

    private void printBoard(Player player){
        out.println(ansi().bold().a("SCHOOLBOARD").reset());

        Students entranceStudents = player.getSchoolBoard().getEntranceStudents();
        Students diningStudents = player.getSchoolBoard().getDiningStudents();
        int numTowers = player.getSchoolBoard().getNumTowers();
        Professors professors = player.getProfessors();

        out.println("Towers: " + numTowers);
        out.print("Entrance: ");
        for(Color studentColor : entranceStudents.keySet()){
            if(entranceStudents.get(studentColor) > 0)
                out.print(ansi().fg(Ansi.Color.valueOf(studentColor.toString())).a(entranceStudents.get(studentColor) + " ").reset());
        }
        out.println();
        out.println();

        out.println("Dining room:");
        out.println();
        for(Color studentColor : diningStudents.keySet()){
            out.print(ansi().bg(Ansi.Color.valueOf(studentColor.toString())).fg(Ansi.Color.BLACK));

            int numOfStudents = diningStudents.get(studentColor);

            for(int i = 0; i< Game.MAX_DINING_STUDENTS; i++){
                if(numOfStudents > 0)
                    out.print("X");
                else if(i > 0 && (i - 2) % 3 == 0) {
                    out.print("C");
                }else out.print("_");
                out.print("   ");

                numOfStudents--;
            }

            out.print("\n");
            if(professors.contains(studentColor))
                out.print("P");

            out.println(ansi().reset());
            out.println();
        }
        out.println(ansi().reset());
    }

    private void printAssistants(){

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
}
