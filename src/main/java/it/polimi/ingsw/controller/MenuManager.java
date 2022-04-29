package it.polimi.ingsw.controller;

import java.util.concurrent.ConcurrentLinkedQueue;

public class MenuManager {
    private static MenuManager instance;
    ConcurrentLinkedQueue<MessageEnvelope> message_queue;

    private MenuManager(){
        message_queue = new ConcurrentLinkedQueue<>();

        new Thread(() -> {
            while (true) {
                if (!message_queue.isEmpty()) {
                    MessageEnvelope envelope = message_queue.remove();
                    envelope.message().handle(envelope.sender());
                }
            }
        }).start();
    }

    public static MenuManager getInstance(){
        if(instance == null) instance = new MenuManager();
        return instance;
    }
}
