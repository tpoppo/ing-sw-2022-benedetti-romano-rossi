package it.polimi.ingsw.controller;

import java.util.concurrent.ConcurrentLinkedQueue;

public class MenuManager {
    private static MenuManager instance;
    ConcurrentLinkedQueue<MessageEnvelope> message_queue;

    private MenuManager(){
        message_queue = new ConcurrentLinkedQueue<>();
    }

    public static MenuManager getInstance(){
        if(instance == null) instance = new MenuManager();
        return instance;
    }
}
