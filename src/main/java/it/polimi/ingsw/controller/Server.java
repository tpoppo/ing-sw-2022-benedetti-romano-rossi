package it.polimi.ingsw.controller;

import it.polimi.ingsw.utils.Consts;
import it.polimi.ingsw.utils.exceptions.FullLobbyException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server{
    private final Logger LOGGER = Logger.getLogger(getClass().getName());
    private static Server instance;
    final private int PORT;
    private ServerSocket serverSocket;
    private Socket clientSocket;

    private final MenuManager menuManager;
    private final ArrayList<NetworkManager> networkManagers;
    private final ArrayList<LobbyPlayer> player_list;

    private Server(){
        PORT = Consts.SERVER_PORT;
        menuManager = MenuManager.getInstance();
        networkManagers = new ArrayList<>();
        player_list = new ArrayList<>();

        retrieveSavedState();

        try {
            serverSocket = new ServerSocket(PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Server getInstance(){
        if(instance == null) {
            instance = new Server();

            try {
                instance.startServer();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return instance;
    }

    private void startServer() throws IOException {
        while(true)
            new MessageHandler(serverSocket.accept()).start();
    }

    private void closeConnection() throws IOException {
        serverSocket.close();
        clientSocket.close();
    }

    private void retrieveSavedState(){
        String path = Consts.PATH_SAVES;

        File directory = new File(path);

        File[] files = directory.listFiles();
        if(files != null){
            for(File file : files){
                try (FileInputStream fis = new FileInputStream(file);
                     ObjectInputStream inputStream = new ObjectInputStream(fis)){

                    NetworkManager networkManager;
                    try {
                        GameHandler gameHandler = (GameHandler) inputStream.readObject();

                        networkManager = NetworkManager.createNetworkManager(gameHandler);
                        networkManagers.add(networkManager);
                    } catch (ClassNotFoundException e) {
                        LOGGER.log(Level.SEVERE, "Invalid file format: {0}", e);
                        throw new RuntimeException(e);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }else {
            if(!directory.mkdir())
                LOGGER.log(Level.SEVERE, "Couldn't create directory in path {0}", directory.getAbsolutePath());
        }
    }

    public NetworkManager createLobby(int max_players){
        NetworkManager new_networkManager = NetworkManager.createNetworkManager(max_players);
        networkManagers.add(new_networkManager);

        return new_networkManager;
    }

    public Optional<NetworkManager> joinLobby(int lobbyID, LobbyPlayer player){
        Optional<NetworkManager> networkManager = networkManagers.stream().filter(x -> x.ID == lobbyID).findFirst();
        if(networkManager.isPresent()){
            LobbyHandler lobbyHandler = networkManager.get().getLobbyHandler();
            try {
                lobbyHandler.addPlayer(player);
            } catch (FullLobbyException ignored) {
                return Optional.empty();
            }
        }
        return networkManager;
    }

    public ArrayList<LobbyPlayer> getPlayerList() {
        return player_list;
    }

    // Checks the uniqueness of the username
    public boolean checkUsername(String username){
        for(LobbyPlayer player : player_list)
            if(player.getUsername().equals(username))
                return false;
        return true;
    }

    // Returns the networkManager containing the lobbyPlayer given
    public NetworkManager findPlayerLocation(LobbyPlayer player){
        for(NetworkManager networkManager : networkManagers){
            // Searches the player inside the currentHandler
            switch (networkManager.getCurrentHandler()){
                case LOBBY:
                    if(networkManager.getLobbyHandler().getPlayers().contains(player))
                        return networkManager;
                    break;
                case GAME:
                    if(networkManager.getGameHandler().getModel().getPlayers().contains(player)) // this should work, right?
                        return networkManager;
                    break;
            }
        }

        // This line should never be reached
        return null;
    }

    // Returns all the networkManagers currently in the lobby state
    public ArrayList<NetworkManager> getLobbies() {
        ArrayList<NetworkManager> lobbies = new ArrayList<>();

        for(NetworkManager networkManager : networkManagers){
            if(networkManager.getCurrentHandler().equals(HandlerType.LOBBY))
                lobbies.add(networkManager);
        }

        return lobbies;
    }
}
