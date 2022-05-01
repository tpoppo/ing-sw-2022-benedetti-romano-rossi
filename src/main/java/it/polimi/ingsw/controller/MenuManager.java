package it.polimi.ingsw.controller;

import it.polimi.ingsw.view.ViewContent;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MenuManager {
    private static MenuManager instance;
    ConcurrentLinkedQueue<MessageEnvelope> message_queue;
    private final Set<ConnectionCEO> subscribers;

    private MenuManager(){
        message_queue = new ConcurrentLinkedQueue<>();
        subscribers = new HashSet<>();

        new Thread(() -> {
            while (true) {
                if (!message_queue.isEmpty()) {
                    MessageEnvelope envelope = message_queue.remove();
                    envelope.message().handle(envelope.connectionCEO(), envelope.sender());
                }
            }
        }).start();

        sendViewContent();
    }

    public static MenuManager getInstance(){
        if(instance == null) instance = new MenuManager();
        return instance;
    }

    private void sendViewContent(){
        new Thread(() -> {
            while(true){
                ViewContent viewContent = new ViewContent();

                for(ConnectionCEO subscriber : subscribers){
                    subscriber.sendViewContent(viewContent);
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    public void subscribe(ConnectionCEO connectionCEO){
        subscribers.add(connectionCEO);
    }

    public void unsubscribe(ConnectionCEO connectionCEO){
        subscribers.remove(connectionCEO);
    }
}
