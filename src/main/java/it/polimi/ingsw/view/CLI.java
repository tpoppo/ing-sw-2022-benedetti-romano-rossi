package it.polimi.ingsw.view;

import it.polimi.ingsw.client.ClientSocket;
import it.polimi.ingsw.controller.*;
import it.polimi.ingsw.controller.messages.ChooseWizardMessage;
import it.polimi.ingsw.controller.messages.CreateLobbyMessage;
import it.polimi.ingsw.controller.messages.JoinLobbyMessage;
import it.polimi.ingsw.controller.messages.StartGameMessage;
import it.polimi.ingsw.model.board.Color;
import it.polimi.ingsw.model.board.Island;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Scanner;

public class CLI {

    final private ClientSocket client_socket;
    final private PrintStream print_stream;
    final private Scanner read_stream;

    public CLI(ClientSocket client_socket, PrintStream print_stream, InputStream read_stream) {
        this.client_socket = client_socket;
        this.print_stream = print_stream;
        this.read_stream = new Scanner(new BufferedInputStream(read_stream));
    }
    public CLI(ClientSocket client_socket) {
        this(client_socket, System.out, System.in);
    }

    public void run(){
        String username;
        do {
            username = read_stream.nextLine();
        } while(!client_socket.login(username));
        print_stream.println("Logged in");

        while(true){
            renderState();
        }
    }

    private void renderState() {

        // TODO: this solution is not great
        // clear the console
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        print_stream.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
        print_stream.flush();

        ViewContent view = client_socket.getView();

        System.err.println(view);

        // missing view
        if(view == null) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return ;
        }

        // print server errors
        if(view.getErrorMessage() != null) print_stream.println(view.getErrorMessage());

        if(view.getLobbyHandler() == null){ // we are in the menu
            printMenu();
        } else{
            switch(view.getCurrentHandler()) {
                case LOBBY: // we are in the lobby
                    printLobby();
                    break;

                case GAME: // we are in the game
                    printGame();
                    break;
            }
        }
        print_stream.print("> ");

        if(read_stream.hasNextLine()) {
            String input = new String(read_stream.nextLine());
            String out = parseInput(input);
            print_stream.println(out);
        }
    }

    private void printMenu() {
        print_stream.println("MENU");
        ViewContent view = client_socket.getView();
        ArrayList<ReducedLobby> reduced_lobbies = view.getLobbies();

        print_stream.println("Lobby Available");
        for(ReducedLobby reduced_lobby : reduced_lobbies){
            print_stream.printf("%d) %d/%d\n", reduced_lobby.getID(), reduced_lobby.getNumPlayer(), reduced_lobby.getMaxPlayers());
        }
        print_stream.println("mc <max players> - create a new lobby");
        print_stream.println("mj <lobby id> - join the lobby with ID <lobby id>");
    }

    private void printLobby(){
        print_stream.println("LOBBY");

        ViewContent view = client_socket.getView();

        // print the list of the players in the lobby
        LobbyHandler lobby_handler = view.getLobbyHandler();
        ArrayList<LobbyPlayer> players = lobby_handler.getPlayers();
        print_stream.printf("Players: %d/%d\n", players.size(), lobby_handler.getMaxPlayers());
        for(LobbyPlayer lobby_player : players){
            Integer wizard = lobby_player.getWizard();
            print_stream.printf("%s (Wizard: %s)\n", lobby_player.getUsername(), wizard);
        }
        print_stream.println("ls <expert mode> - start the game");
        print_stream.println("lc <id> - choose wizard");

    }

    private void printGame(){
        print_stream.println("GAME");
        GameHandler gameHandler = client_socket.getView().getGameHandler();
        Game game = gameHandler.getModel();
        print_stream.printf("Current player: %s\n", game.getCurrentPlayer() == null ? "-" : game.getCurrentPlayer().getUsername());
        print_stream.printf("Current state: %s-%s-%s-%d\n",
                gameHandler.getCurrentState(),
                gameHandler.isActionCompleted(),
                gameHandler.getSavedState(),
                gameHandler.getStudentMoves());
        print_stream.println("Islands: ");
        for(int i=0; i<game.getIslands().size(); i++){
            Island island = game.getIslands().get(i);
            print_stream.printf("%d-%d)\t%s\t%s\n",
                    i,
                    island.getNumIslands(),
                    island.getStudents(),
                    island.getOwner() == null ? "null" : island.getOwner().getUsername()
            );
        }
    }

    private String parseInput(String s){
        String[] command = s.split(" ");
        if(command.length == 0) return "No command found";
        switch (command[0]) {
            case "mc" -> {
                if (command.length != 2) return "Invalid number of arguments";
                client_socket.send(new CreateLobbyMessage(Integer.parseInt(command[1])));
                return "";
            }
            case "mj" -> {
                if (command.length != 2) return "Invalid number of arguments";
                client_socket.send(new JoinLobbyMessage(Integer.parseInt(command[1])));
                return "";
            }
            case "ls" -> {
                if (command.length != 2) return "Invalid number of arguments";
                client_socket.send(new StartGameMessage(Boolean.parseBoolean(command[1])));
                return "";
            }
            case "lc" -> {
                if (command.length != 2) return "Invalid number of arguments";
                client_socket.send(new ChooseWizardMessage(Integer.parseInt(command[1])));
                return "";
            }
        }
        return "Invalid Command";
    }
}
