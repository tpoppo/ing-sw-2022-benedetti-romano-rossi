package it.polimi.ingsw.view;

import it.polimi.ingsw.client.ClientSocket;
import it.polimi.ingsw.controller.LobbyHandler;
import it.polimi.ingsw.controller.LobbyPlayer;
import it.polimi.ingsw.controller.NetworkManager;

import java.io.PrintStream;
import java.util.ArrayList;

public class CLI {

    private ClientSocket client_socket;
    private PrintStream print_stream;
    public CLI(ClientSocket client_socket, PrintStream print_stream) {
        this.client_socket = client_socket;
        this.print_stream = print_stream;
    }
    public CLI(ClientSocket client_socket) {
        this(client_socket, System.out);
    }

    public void run(){

        while(true){
            renderState();
        }
    }

    private void renderState(){

        // clear the console
        // For further references visit: https://stackoverflow.com/questions/2979383/how-to-clear-the-console
        print_stream.print("\033[H\033[2J");
        print_stream.flush();
        ViewContent view = client_socket.getView();
        if(view.getLobbyHandler() == null){ // we are in the menu
            printMenu();
        }else{
            switch(view.getCurrentHandler()) {
                case LOBBY: // we are in the lobby
                    printLobby();
                    break;

                case GAME: // we are in the game
                    printGame();
                    break;
            }
        }
        print_stream.println("> ");
    }

    private void printMenu() {
        print_stream.println("MENU");
        ViewContent view = client_socket.getView();
        ArrayList<NetworkManager> network_managers = view.getLobbies();

        print_stream.println("Lobby Available");
        for(NetworkManager network_manager : network_managers){
            LobbyHandler lobby_handler = network_manager.getLobbyHandler();
            print_stream.printf("%d) %d/%d\n", network_manager.ID, lobby_handler.getPlayers().size(), lobby_handler.getMaxPlayers());
        }
        print_stream.println("c - create a new lobby");
        print_stream.println("j <lobby id> - join the lobby with ID <lobby id>");
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
            print_stream.printf("%s (Wizard: %s)\n", lobby_player.getUsername(), wizard.toString());
        }
        // TODO: show available actions
    }

    private void printGame(){
        print_stream.println("GAME");
    }
}
