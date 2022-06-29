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
/**
 * This class manages the server instance. It is a singleton and when initialized for the first time it starts a server socket at the port PORT_INIT
 */
public class Server{
    private static final Logger LOGGER = Logger.getLogger(Server.class.getName());
    static private int PORT_INIT;
    private static Server instance;
    private ServerSocket serverSocket;

    private final List<NetworkManager> networkManagers;
    private final List<String> player_list;

    /**
     * Private constructor called by getInstance
     */
    private Server(){
        int PORT = PORT_INIT;
        networkManagers = Collections.synchronizedList(new ArrayList<>());
        player_list = Collections.synchronizedList(new ArrayList<>());

        retrieveSavedState();

        try {
            serverSocket = new ServerSocket(PORT);
        } catch (IOException e) {
            System.out.println("Port already in use");
            System.exit(0);
        }
    }

    public static void setPort(int port){
        PORT_INIT = port;
    }

    /**
     * It returns the server instance. The first times it also constructs the server.
     * @return Server instance
     */
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

    /**
     * Listen for new connection and create a {@link ConnectionCEO} on them.
     * @throws IOException thrown by {@link ServerSocket}
     */
    private void run() throws IOException {
        while(!serverSocket.isClosed())
            new ConnectionCEO(serverSocket.accept()).start();
    }

    /**
     * Load the saved game state into the memory
     */
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
                        LOGGER.log(Level.SEVERE, "Invalid file format: {0}", new Object[]{e});
                        throw new RuntimeException(e);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            // it rebuilds  the save file directory with the new files
            for (File file : files) {
                if(!file.delete()){
                    LOGGER.log(Level.WARNING, "Cannot remove file {0}", new Object[]{file});
                }
            }
            networkManagers.forEach(NetworkManager::saveState);
        }else {
            if(!directory.mkdir())
                LOGGER.log(Level.SEVERE, "Could not create directory in path {0}", new Object[]{directory.getAbsolutePath()});
        }
    }

    /**
     * It creates a new lobby
     * @param max_players maximum number of players
     * @return networkManager for the new lobby
     */
    public NetworkManager createLobby(int max_players){
        NetworkManager new_networkManager = NetworkManager.createNetworkManager(max_players);
        networkManagers.add(new_networkManager);

        return new_networkManager;
    }

    /**
     * The player join the given lobbyID
     * @param lobbyID ID of the lobby
     * @param player player
     * @return an empty optional if the lobby is full otherwise the {@link NetworkManager} of the selected lobby
     */
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



    /**
     * Checks the uniqueness of the username, and that it's not blank
     * @param username given username
     * @return true if unique and valid otherwise false
     */
    public boolean checkUsername(String username){
        if(username.isBlank()) return false;

        for(String user : player_list)
            if(user.equals(username))
                return false;
        return true;
    }

    /**
     * It removes a {@link NetworkManager} from the list
     * @param networkManager to remove
     */
    public void deleteNetworkManager(NetworkManager networkManager){
        networkManagers.remove(networkManager);
    }


    /**
     * Returns the networkManager containing the given lobbyPlayer
     * @param lobbyPlayer the player
     * @return the requested networkManager. It returns null if it cannot find the player
     */
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

    /**
     * Returns all the networkManagers currently in the lobby state
     * @return networkManagers in the lobby state
     */
    public ArrayList<NetworkManager> getLobbies() {
        ArrayList<NetworkManager> lobbies = new ArrayList<>();

        for(NetworkManager networkManager : networkManagers){
            if(networkManager.getCurrentHandler().equals(HandlerType.LOBBY))
                lobbies.add(networkManager);
        }

        return lobbies;
    }
}
