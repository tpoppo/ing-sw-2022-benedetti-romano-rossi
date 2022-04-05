package it.polimi.ingsw.controller;

import it.polimi.ingsw.controller.messages.ClientMessage;

import java.util.concurrent.ConcurrentLinkedQueue;

public class MenuManager {
    private static MenuManager instance;
    ConcurrentLinkedQueue<ClientMessage> message_queue;

    private MenuManager(){
        message_queue = new ConcurrentLinkedQueue<>();
    }

    public static MenuManager getInstance(){
        if(instance == null) instance = new MenuManager();
        return instance;
    }
}
