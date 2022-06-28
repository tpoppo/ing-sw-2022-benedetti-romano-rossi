package it.polimi.ingsw.network;

import it.polimi.ingsw.controller.GameHandler;
import it.polimi.ingsw.controller.LobbyHandler;
import it.polimi.ingsw.controller.LobbyPlayer;
import it.polimi.ingsw.network.messages.MessageEnvelope;
import it.polimi.ingsw.network.messages.StatusCode;
import it.polimi.ingsw.utils.Constants;
import it.polimi.ingsw.view.viewcontent.GameContent;
import it.polimi.ingsw.view.viewcontent.LobbyContent;
import it.polimi.ingsw.view.viewcontent.ViewContent;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class manages the lobby and game phases of a specific game instance.
 */
public class NetworkManager {
    private final Logger LOGGER = Logger.getLogger(getClass().getName());
    private static int count = 0;
    public final int ID;
    private HandlerType current_handler;
    private final LinkedBlockingQueue<MessageEnvelope> message_queue;
    private final Map<LobbyPlayer, String> errorMessages;
    private LobbyHandler lobby_handler;
    private GameHandler game_handler;
    private final Set<ConnectionCEO> subscribers;
    private boolean alive;

    /**
     * default constructor for the NetworkManager, starts in the lobby state. it is not thread safe
     * @param max_players maximum number of players
     */
    private NetworkManager(int max_players){
        ID = count;
        count++;
        message_queue = new LinkedBlockingQueue<>();
        errorMessages = Collections.synchronizedMap(new HashMap<>());
        subscribers = Collections.synchronizedSet(new HashSet<>());

        current_handler = HandlerType.LOBBY;
        lobby_handler = new LobbyHandler(ID, max_players);

        handleMessages();
    }


    /**
     * constructor for Server.retrieveSavedState(), starts in the game state without passing through the lobby
     * @param gameHandler saved {@link GameHandler}
     */
    private NetworkManager(GameHandler gameHandler){
        ID = count;
        count++;
        message_queue = new LinkedBlockingQueue<>();
        errorMessages = Collections.synchronizedMap(new HashMap<>());
        subscribers = Collections.synchronizedSet(new HashSet<>());

        current_handler = HandlerType.GAME;
        this.game_handler = gameHandler;

        handleMessages();
    }

    /**
     * It starts a thread to handle the new message
     */
    private void handleMessages(){
        new Thread(() -> {
            alive = true;

            while(alive){
                LOGGER.log(Level.FINE, "Handling message");

                MessageEnvelope envelope;
                try {
                    envelope = message_queue.take(); // blocking (LinkedBlockingQueue)
                } catch (InterruptedException e) {
                    LOGGER.log(Level.SEVERE, e.toString());
                    throw new RuntimeException(e);
                }

                LOGGER.log(Level.FINE, "Message found: {0}", envelope);
                StatusCode statusCode = envelope.message().handle(this, envelope.sender());
                LOGGER.log(Level.INFO, envelope.message() + " => " + statusCode);

                if(!errorMessages.isEmpty())
                    LOGGER.log(Level.WARNING, errorMessages.toString());

                if(statusCode == StatusCode.NOT_IMPLEMENTED){
                    LOGGER.log(Level.SEVERE, "This message has not been implemented correctly: {0}");
                }

                if(statusCode.equals(StatusCode.OK)) {
                    notifySubscribers();
                    LOGGER.log(Level.INFO, "Subscribers notified");

                    // saves the networkManager state for persistence
                     if(current_handler != null && current_handler.equals(HandlerType.GAME)) {
                          saveState();
                          LOGGER.log(Level.INFO, "Game saved");
                     }
                }else {
                    String subscriberUsername = envelope.sender().getUsername();
                    subscribers.stream()
                            .filter(x -> x.getPlayer().getUsername().equals(subscriberUsername))
                            .reduce((a, b) -> {
                                throw new IllegalStateException("Multiple elements: " + a + ", " + b);
                            }).ifPresent(subscriber -> {
                                notifyError(subscriber);
                                LOGGER.log(Level.INFO, "Subscriber found and notified");
                            });

                    // this should only happen if someone is sending an invalid message
                    // while not being in a lobby or game
                    LOGGER.log(Level.INFO, "Not notified (subscriber not found)");
                }
            }

            LOGGER.log(Level.INFO, "Goodbye.");
        }).start();
    }

    // thread safe
    public static synchronized NetworkManager createNetworkManager(int max_players){
        return new NetworkManager(max_players);
    }

    public static synchronized NetworkManager createNetworkManager(GameHandler gameHandler){
        return new NetworkManager(gameHandler);
    }

    /**
     * It is call when the game starts
     * It creates the GameHandler and the Game
     * @param expert_mode the selected mode
     */
    public void startGame(boolean expert_mode){
        game_handler = new GameHandler(ID, expert_mode, lobby_handler);
        current_handler = HandlerType.GAME;
    }

    public void addMessage(MessageEnvelope envelope){
        message_queue.add(envelope);
    }

    public void addErrorMessage(LobbyPlayer player, String message){
        errorMessages.put(player, message);
    }

    public void saveState(){
        String path = Constants.PATH_SAVES;
        String fileName = path + "/SavedGame_" + ID + ".sav";

        try (FileOutputStream fos = new FileOutputStream(fileName);
             ObjectOutputStream outputStream = new ObjectOutputStream(fos)){
            outputStream.reset();
            outputStream.writeObject(game_handler);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void deleteSaveFile(){
        String path = Constants.PATH_SAVES;
        String fileName = path + "/SavedGame_" + ID + ".sav";

        File saveFile = new File(fileName);

        if(saveFile.delete()){
            LOGGER.log(Level.INFO, "{0} deleted successfully!", saveFile);
        }else LOGGER.log(Level.INFO, "Failed to delete {0}", saveFile);
    }

    public void destroy(){
        alive = false;
        message_queue.clear();
        current_handler = null;
        deleteSaveFile();
        Server.getInstance().deleteNetworkManager(this);
    }

    public void safeDestroy(){
        MenuManager menuManager = MenuManager.getInstance();

        ArrayList<ConnectionCEO> subscribers = new ArrayList<>(getSubscribers());
        getSubscribers().clear();
        destroy();

        for (ConnectionCEO subscriber : subscribers) {
            MenuManager.getInstance().addErrorMessage(subscriber.getPlayer(), "A player disconnected. The game has been terminated");
            menuManager.subscribe(subscriber);
            subscriber.clean();
        }
    }

    // sends an updated viewContent to all the subscribers
    private void notifySubscribers(){
        // sends view updated to subscribers
        for(ConnectionCEO subscriber : subscribers) {
            ViewContent viewContent = null;

            switch (current_handler){
                case GAME -> viewContent = new GameContent(game_handler, null);
                case LOBBY -> viewContent = new LobbyContent(lobby_handler, null);
            }

            subscriber.sendViewContent(viewContent);
        }
    }

    public Set<ConnectionCEO> getSubscribers() {
        return subscribers;
    }

    // sends a viewContent containing an errorMessage to the given subscriber
    private void notifyError(ConnectionCEO subscriber){
        ViewContent viewContent = null;

        String errorMessage = errorMessages.get(subscriber.getPlayer());
        errorMessages.remove(subscriber.getPlayer());

        switch (current_handler){
            case GAME -> viewContent = new GameContent(game_handler, errorMessage);
            case LOBBY -> viewContent = new LobbyContent(lobby_handler, errorMessage);
        }

        subscriber.sendViewContent(viewContent);
    }

    public void subscribe(ConnectionCEO connectionCEO){
        subscribers.add(connectionCEO);
        notifySubscribers();
    }

    public void unsubscribe(ConnectionCEO connectionCEO){
        subscribers.remove(connectionCEO);
        notifySubscribers();
    }

    public HandlerType getCurrentHandler() {
        return current_handler;
    }

    public LobbyHandler getLobbyHandler() {
        return lobby_handler;
    }

    public GameHandler getGameHandler() {
        return game_handler;
    }
}
