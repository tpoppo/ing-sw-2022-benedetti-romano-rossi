package it.polimi.ingsw.controller;

import it.polimi.ingsw.controller.messages.ClientMessage;

import java.util.concurrent.ConcurrentLinkedQueue;

public class NetworkManager {
    private static int count = 0;
    public final int ID;
    private HandlerType current_handler;
    private ConcurrentLinkedQueue<ClientMessage> message_queue;
    private LobbyHandler lobby_handler;
    private GameHandler game_handler;

    // not thread safe
    private NetworkManager(int max_players){
        ID = count;
        count++;
        message_queue = new ConcurrentLinkedQueue<>();

        current_handler = HandlerType.LOBBY;
        lobby_handler = new LobbyHandler(max_players);
    }

    // thread safe
    public static synchronized NetworkManager createNetworkManager(int max_players){
        return new NetworkManager(max_players);
    }

    public LobbyHandler getLobbyHandler() {
        return lobby_handler;
    }

    public GameHandler getGameHandler() {
        return game_handler;
    }

}
