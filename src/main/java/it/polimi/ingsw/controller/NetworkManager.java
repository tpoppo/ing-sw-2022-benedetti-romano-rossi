package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.Player;

import java.util.concurrent.ConcurrentLinkedQueue;

public class NetworkManager {
    private static int count = 0;
    public final int ID;
    private HandlerType current_handler;
    private final ConcurrentLinkedQueue<MessageEnvelope> message_queue;
    private final LobbyHandler lobby_handler;
    private GameHandler game_handler;

    // not thread safe
    private NetworkManager(int max_players){
        ID = count;
        count++;
        message_queue = new ConcurrentLinkedQueue<>();

        current_handler = HandlerType.LOBBY;
        lobby_handler = new LobbyHandler(max_players);

        new Thread(() -> {
            while(true){
                if(!message_queue.isEmpty()){
                    MessageEnvelope envelope = message_queue.remove();
                    Player player = game_handler.lobbyPlayerToPlayer(envelope.getSender());
                    envelope.getMessage().handle(this, player);
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
