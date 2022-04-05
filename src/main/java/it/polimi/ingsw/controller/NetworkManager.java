package it.polimi.ingsw.controller;

import it.polimi.ingsw.controller.messages.ClientMessage;

import java.util.concurrent.ConcurrentLinkedQueue;

public class NetworkManager {
    private static int count = 0;
    public final int ID;
    private HandlerType current_handler;
    private ConcurrentLinkedQueue<ClientMessage> message_queue;

    private LobbyHandler lobbyHandler;
    private GameHandler gameHandler;

    // not thread safe
    private NetworkManager(int max_players){
        ID = count;
        count++;
        message_queue = new ConcurrentLinkedQueue<>();

        current_handler = HandlerType.LOBBY;
        lobbyHandler = new LobbyHandler(max_players);
    }

    // thread safe
    public static synchronized NetworkManager createNetworkManager(int max_players){
        return new NetworkManager(max_players);
    }

    public LobbyHandler getLobbyHandler() {
        return lobbyHandler;
    }
}
