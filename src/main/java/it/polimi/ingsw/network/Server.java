package it.polimi.ingsw.network;

import it.polimi.ingsw.controller.GameHandler;
import it.polimi.ingsw.controller.LobbyHandler;
import it.polimi.ingsw.controller.LobbyPlayer;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.utils.Constants;
import it.polimi.ingsw.utils.exceptions.FullLobbyException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server{
    private static final Logger LOGGER = Logger.getLogger(Server.class.getName());
    private static Server instance;
    final private int PORT;
    private ServerSocket serverSocket;

    private final List<NetworkManager> networkManagers;
    private final List<String> player_list;

    private Server(){
        PORT = Constants.SERVER_PORT;
        networkManagers = Collections.synchronizedList(new ArrayList<>());
        player_list = Collections.synchronizedList(new ArrayList<>());

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

            LOGGER.log(Level.INFO, "Server has been created");

            try {
                instance.run();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return instance;
    }

    private void run() throws IOException {
        while(true)
            new ConnectionCEO(serverSocket.accept()).start();
    }

    private void closeConnection() throws IOException {
        serverSocket.close();
    }

    private void retrieveSavedState(){
        String path = Constants.PATH_SAVES;

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
        Optional<NetworkManager> networkManager = networkManagers.stream().filter(
                x -> x.ID == lobbyID && x.getCurrentHandler() == HandlerType.LOBBY
        ).findFirst();

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

    public List<String> getPlayerList() {
        return player_list;
    }


    // Checks the uniqueness of the username
    public boolean checkUsername(String username){
        for(String user : player_list)
            if(user.equals(username))
                return false;
        return true;
    }

    public void deleteNetworkManager(NetworkManager networkManager){
        networkManagers.remove(networkManager);
    }

    // Returns the networkManager containing the lobbyPlayer given
    public NetworkManager findPlayerLocation(LobbyPlayer lobbyPlayer){
        for(NetworkManager networkManager : networkManagers){
            // Searches the lobbyPlayer inside the currentHandler
            switch (networkManager.getCurrentHandler()){
                case LOBBY:
                    if(networkManager.getLobbyHandler().getPlayers().contains(lobbyPlayer))
                        return networkManager;
                    break;
                case GAME:
                    Player player = networkManager.getGameHandler().lobbyPlayerToPlayer(lobbyPlayer);

                    if(networkManager.getGameHandler().getModel().getPlayers().contains(player)) // this should work, right?
                        return networkManager;
                    break;
            }
        }

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
