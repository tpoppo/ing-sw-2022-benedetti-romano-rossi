package it.polimi.ingsw.network;

import it.polimi.ingsw.controller.GameHandler;
import it.polimi.ingsw.controller.LobbyHandler;
import it.polimi.ingsw.controller.LobbyPlayer;
import it.polimi.ingsw.network.messages.MessageEnvelope;
import it.polimi.ingsw.network.messages.StatusCode;
import it.polimi.ingsw.utils.Consts;
import it.polimi.ingsw.view.GameContent;
import it.polimi.ingsw.view.LobbyContent;
import it.polimi.ingsw.view.ViewContent;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NetworkManager {
    private final Logger LOGGER = Logger.getLogger(getClass().getName());
    private static int count = 0;
    public final int ID;
    private HandlerType current_handler;
    private final ConcurrentLinkedQueue<MessageEnvelope> message_queue;
    private final HashMap<LobbyPlayer, String> errorMessages;
    private LobbyHandler lobby_handler;
    private GameHandler game_handler;
    private Set<ConnectionCEO> subscribers;

    // not thread safe
    // default constructor for the NetworkManager, starts in the lobby state
    private NetworkManager(int max_players){
        ID = count;
        count++;
        message_queue = new ConcurrentLinkedQueue<>();
        errorMessages = new HashMap<>();
        subscribers = new HashSet<>();

        current_handler = HandlerType.LOBBY;
        lobby_handler = new LobbyHandler(max_players);

        handleMessages();
    }

    // constructor for Server.retrieveSavedState(), starts in the game state without passing through the lobby
    private NetworkManager(GameHandler gameHandler){
        ID = count;
        count++;
        message_queue = new ConcurrentLinkedQueue<>();
        errorMessages = new HashMap<>();

        current_handler = HandlerType.GAME;
        this.game_handler = gameHandler;

        handleMessages();
    }

    private void handleMessages(){
        new Thread(() -> {
            while(true){
                if(!message_queue.isEmpty()){
                    MessageEnvelope envelope = message_queue.remove();
                    StatusCode statusCode = envelope.message().handle(this, envelope.sender());
                    if(statusCode == StatusCode.NOT_IMPLEMENTED){
                        LOGGER.log(Level.SEVERE, "This message has not been implemented correctly: {0}");
                    }
                    notifySubscribers();

                    // saves the networkManager state for persistence
                    if(current_handler.equals(HandlerType.GAME)) saveState();
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
        game_handler = new GameHandler(expert_mode, lobby_handler);
        current_handler = HandlerType.GAME;
    }

    public void addErrorMessage(LobbyPlayer player, String message){
        errorMessages.put(player, message);
    }

    public void saveState(){
        String path = Consts.PATH_SAVES;
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

    private void notifySubscribers(){
        // sends view updated to subscribers
        for(ConnectionCEO subscriber : subscribers) {
            String errorMessage = errorMessages.get(subscriber.getPlayer());

            ViewContent viewContent;
            if(current_handler == HandlerType.GAME){
                viewContent = new GameContent(game_handler, errorMessage);
            } else{
                viewContent = new LobbyContent(lobby_handler, errorMessage);
            }

            subscriber.sendViewContent(viewContent);
        }
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

    public ConcurrentLinkedQueue<MessageEnvelope> getMessageQueue() {
        return message_queue;
    }
}
