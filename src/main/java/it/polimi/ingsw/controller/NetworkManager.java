package it.polimi.ingsw.controller;

import it.polimi.ingsw.controller.messages.ClientMessage;

import java.util.concurrent.ConcurrentLinkedQueue;

public class NetworkManager {
    private static int count = 0;
    private final int ID;
    private HandlerType current_handler;
    private ConcurrentLinkedQueue<ClientMessage> message_queue;

    // not thread safe
    private NetworkManager(){
        ID = count;
        count++;
    }

    // thread safe
    public synchronized NetworkManager createNetworkManager(){
        return new NetworkManager();
    }
}
