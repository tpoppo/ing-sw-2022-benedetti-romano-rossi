package it.polimi.ingsw.controller;

import it.polimi.ingsw.utils.exceptions.FullLobbyException;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Optional;

public class Server {
    private static Server instance;
    final private int PORT;
    private ServerSocket serverSocket;
    private Socket connection;

    private MenuManager menuManager;
    private ArrayList<NetworkManager> networkManagers;

    private Server(){
        PORT = 42069;

        try {
            setupConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Server getInstance(){
        if(instance == null) instance = new Server();
        return instance;
    }

    private void setupConnection() throws IOException {
        serverSocket = new ServerSocket(PORT);
        connection = serverSocket.accept();
    }

    private void closeConnection() throws IOException {
        serverSocket.close();
        connection.close();
    }

    public NetworkManager createLobby(int max_players){
        NetworkManager new_networkManager = NetworkManager.createNetworkManager(max_players);
        networkManagers.add(new_networkManager);

        return new_networkManager;
    }

    public Optional<NetworkManager> joinLobby(int lobbyID, LobbyPlayer player){
        Optional<NetworkManager> networkManager = networkManagers.stream().filter(x -> x.ID == lobbyID).findFirst();

        networkManager.ifPresent(x -> {
            LobbyHandler lobbyHandler = x.getLobbyHandler();
            try {
                lobbyHandler.addPlayer(player);
            } catch (FullLobbyException ignored) {}
        });

        return networkManager;
    }

    // FIXME: do we still need this?
    //  public NetworkManager reconnect(String username){}
}
