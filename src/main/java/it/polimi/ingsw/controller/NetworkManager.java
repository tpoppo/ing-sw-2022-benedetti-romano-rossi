package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.view.ViewContent;

import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class NetworkManager {
    private static int count = 0;
    public final int ID;
    private HandlerType current_handler;
    private final ConcurrentLinkedQueue<MessageEnvelope> message_queue;
    private final HashMap<LobbyPlayer, String> errorMessages;
    private final LobbyHandler lobby_handler;
    private GameHandler game_handler;

    // not thread safe
    private NetworkManager(int max_players){
        ID = count;
        count++;
        message_queue = new ConcurrentLinkedQueue<>();
        errorMessages = new HashMap<>();

        current_handler = HandlerType.LOBBY;
        lobby_handler = new LobbyHandler(max_players);

        new Thread(() -> {
            while(true){
                if(!message_queue.isEmpty()){
                    MessageEnvelope envelope = message_queue.remove();
                    envelope.getMessage().handle(this, envelope.getSender());
                }
            }
        }).start();
    }

    // thread safe
    public static synchronized NetworkManager createNetworkManager(int max_players){
        return new NetworkManager(max_players);
    }

    public void startGame(boolean expert_mode){
        game_handler = new GameHandler(expert_mode, lobby_handler);
        current_handler = HandlerType.GAME;
    }

    public void addErrorMessage(LobbyPlayer player, String message){
        errorMessages.put(player, message);
    }

    public ViewContent createViewContent(LobbyPlayer lobbyPlayer){
        return new ViewContent(game_handler, lobby_handler, current_handler, errorMessages.get(lobbyPlayer));
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
