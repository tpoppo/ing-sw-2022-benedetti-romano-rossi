package it.polimi.ingsw.network;

import it.polimi.ingsw.controller.GameHandler;
import it.polimi.ingsw.controller.LobbyHandler;
import it.polimi.ingsw.controller.LobbyPlayer;
import it.polimi.ingsw.network.messages.MessageEnvelope;
import it.polimi.ingsw.network.messages.StatusCode;
import it.polimi.ingsw.utils.Constants;
import it.polimi.ingsw.view.GameContent;
import it.polimi.ingsw.view.LobbyContent;
import it.polimi.ingsw.view.ViewContent;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NetworkManager {
    private final Logger LOGGER = Logger.getLogger(getClass().getName());
    private static int count = 0;
    public final int ID;
    private HandlerType current_handler;
    private final LinkedBlockingQueue<MessageEnvelope> message_queue;
    private final HashMap<LobbyPlayer, String> errorMessages;
    private LobbyHandler lobby_handler;
    private GameHandler game_handler;
    private Set<ConnectionCEO> subscribers;

    // not thread safe
    // default constructor for the NetworkManager, starts in the lobby state
    private NetworkManager(int max_players){
        ID = count;
        count++;
        message_queue = new LinkedBlockingQueue<>();
        errorMessages = new HashMap<>();
        subscribers = new HashSet<>();

        current_handler = HandlerType.LOBBY;
        lobby_handler = new LobbyHandler(ID, max_players);

        handleMessages();
    }

    // constructor for Server.retrieveSavedState(), starts in the game state without passing through the lobby
    private NetworkManager(GameHandler gameHandler){
        ID = count;
        count++;
        message_queue = new LinkedBlockingQueue<>();
        errorMessages = new HashMap<>();
        subscribers = new HashSet<>();

        current_handler = HandlerType.GAME;
        this.game_handler = gameHandler;

        handleMessages();
    }

    private void handleMessages(){
        new Thread(() -> {
            while(true){
                LOGGER.log(Level.FINE, "Handling message");

                MessageEnvelope envelope = null;
                try {
                    envelope = message_queue.take(); // blocking (LinkedBlockingQueue)
                } catch (InterruptedException e) {
                    LOGGER.log(Level.SEVERE, e.toString());
                    throw new RuntimeException(e);
                }

                LOGGER.log(Level.FINE, "Message found: {}", envelope);
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
                    // FIXME: uncomment for persistence

                     if(current_handler.equals(HandlerType.GAME)) {
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
        }).start();
    }

    // thread safe
    public static synchronized NetworkManager createNetworkManager(int max_players){
        return new NetworkManager(max_players);
    }

    public static synchronized NetworkManager createNetworkManager(GameHandler gameHandler){
        return new NetworkManager(gameHandler);
    }

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
            // FIXME: do we also need to save the message_queue?
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
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
