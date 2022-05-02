package it.polimi.ingsw.network;

import it.polimi.ingsw.network.messages.MessageEnvelope;
import it.polimi.ingsw.network.messages.StatusCode;
import it.polimi.ingsw.view.MenuContent;
import it.polimi.ingsw.view.ViewContent;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MenuManager {
    private final Logger LOGGER = Logger.getLogger(getClass().getName());
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
                    StatusCode statusCode = envelope.message().handle(envelope.connectionCEO(), envelope.sender());
                    if(statusCode == StatusCode.NOT_IMPLEMENTED){
                        LOGGER.log(Level.SEVERE, "This message has not been implemented correctly: {0}");
                    }
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
                MenuContent menuContent = new MenuContent();

                for(ConnectionCEO subscriber : subscribers){
                    subscriber.sendViewContent(menuContent);
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
