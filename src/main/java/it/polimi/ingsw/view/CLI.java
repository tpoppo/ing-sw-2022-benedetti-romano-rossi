package it.polimi.ingsw.view;

import it.polimi.ingsw.client.ClientSocket;
import it.polimi.ingsw.controller.Game;
import it.polimi.ingsw.controller.GameHandler;
import it.polimi.ingsw.controller.LobbyHandler;
import it.polimi.ingsw.controller.LobbyPlayer;
import it.polimi.ingsw.model.board.Color;
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
    private final Logger LOGGER = Logger.getLogger(getClass().getName());
    final private ClientSocket client_socket;
    final private PrintStream out;
    final private Scanner read_stream;

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

        renderState();

        while(true){
            getInput();
        }
    }

    private void getInput(){
        read_stream.reset();
        out.print("> ");
        String input = read_stream.nextLine();
        String out = parseInput(input);
        this.out.println(out);
    }

    private void clearScreen(){
        // clear the console
        // For further references visit: https://stackoverflow.com/questions/2979383/how-to-clear-the-console
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
                        case GAME -> // we are in the game
                                printGame();
                    }
                }

                synchronized (client_socket.mutex){
                    client_socket.setView(null);
                    client_socket.mutex.notifyAll();
                }
            }
        }).start();
    }

    private void printMenu() {
        out.println(Constants.MENU);
        ViewContent view = client_socket.getView();
        ArrayList<ReducedLobby> reduced_lobbies = view.getLobbies();

        out.println("Available lobbies:");
        for(ReducedLobby reduced_lobby : reduced_lobbies){
            out.printf("* %d. Slots: %d/%d\n", reduced_lobby.getID(), reduced_lobby.getNumPlayer(), reduced_lobby.getMaxPlayers());
        }
        out.println("create <max players> - create a new lobby");
        out.println("join <lobby id> - join the lobby with ID <lobby id>");
    }

    private void printLobby(){
        out.println(Constants.LOBBY);

        ViewContent view = client_socket.getView();

        // print the list of the players in the lobby
        LobbyHandler lobby_handler = view.getLobbyHandler();
        ArrayList<LobbyPlayer> players = lobby_handler.getPlayers();

        // TODO: add lobby id inside lobby
        out.printf("Players: %d/%d\n", players.size(), lobby_handler.getMaxPlayers());
        for(LobbyPlayer lobby_player : players){
            Integer wizard = lobby_player.getWizard();
            out.printf("%s (Wizard: %s)\n", lobby_player.getUsername(), wizard);
        }
        out.println("start <expert mode> - start the game");
        out.println("wizard <id> - choose wizard");

    }

    private void printGame(){
        out.println("GAME");
        GameHandler gameHandler = client_socket.getView().getGameHandler();
        Game game = gameHandler.getModel();
        printCurrentPlayer(game);
        out.printf("Current state: %s-%s-%s-%d\n",
                gameHandler.getCurrentState(),
                gameHandler.isActionCompleted(),
                gameHandler.getSavedState(),
                gameHandler.getStudentMoves());
        out.println("Islands: ");
        for(int i=0; i<game.getIslands().size(); i++){
            Island island = game.getIslands().get(i);
            out.printf("%d-%d)\t%s\t%s\n",
                    i,
                    island.getNumIslands(),
                    island.getStudents(),
                    island.getOwner() == null ? "null" : island.getOwner().getUsername()
            );
        }
    }

    private void printCurrentPlayer(Game model){
        out.println(ansi()
                    .bold()
                    .a("TURNO: ")
                    .reset()
                    .a(model.getCurrentPlayer().getUsername())
        );
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
