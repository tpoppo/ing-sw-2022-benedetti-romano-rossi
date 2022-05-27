package it.polimi.ingsw.network;

import it.polimi.ingsw.controller.LobbyPlayer;
import it.polimi.ingsw.network.messages.MessageEnvelope;
import it.polimi.ingsw.network.messages.StatusCode;
import it.polimi.ingsw.view.viewcontent.MenuContent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MenuManager {
    private final Logger LOGGER = Logger.getLogger(getClass().getName());
    private static MenuManager instance;
    private final LinkedBlockingQueue<MessageEnvelope> message_queue;
    private final Set<ConnectionCEO> subscribers;
    private final HashMap<LobbyPlayer, String> errorMessages;

    private MenuManager(){
        message_queue = new LinkedBlockingQueue<>();
        subscribers = new HashSet<>();
        errorMessages = new HashMap<>();

        new Thread(() -> {
            while (true) {
                LOGGER.log(Level.FINE, "Handling message");

                MessageEnvelope envelope = null;
                try {
                    envelope = message_queue.take(); // blocking (LinkedBlockingQueue)
                } catch (InterruptedException e) {
                    LOGGER.log(Level.SEVERE, e.toString());
                    throw new RuntimeException(e);
                }

                LOGGER.log(Level.FINE, "Message found: {0}", envelope);
                StatusCode statusCode = envelope.message().handle(envelope.connectionCEO(), this, envelope.sender());
                LOGGER.log(Level.INFO, envelope.message() + " => " + statusCode);

                if(statusCode.equals(StatusCode.OK)){
                    notifySubscribers();
                    LOGGER.log(Level.INFO, "Subscribers notified");
                } else{
                    String subscriberUsername = envelope.sender().getUsername();

                    subscribers.stream()
                            .filter(x -> x.getPlayer().getUsername().equals(subscriberUsername))
                            .reduce((a, b) -> {
                                throw new IllegalStateException("Multiple elements: " + a + ", " + b);
                            }).ifPresent(subscriber -> {
                                notifyError(subscriber);
                                LOGGER.log(Level.INFO, "Subscriber found and notified");
                            });

                    // this should happen only if someone is sending an invalid message
                    // while being in a lobby or game
                    LOGGER.log(Level.INFO, "Not notified (subscriber not found)");
                }

                if(statusCode == StatusCode.NOT_IMPLEMENTED){
                    LOGGER.log(Level.SEVERE, "This message has not been implemented correctly: {0}");
                }
            }
        }).start();
    }

    public static MenuManager getInstance(){
        if(instance == null) instance = new MenuManager();
        return instance;
    }

    public void addMessage(MessageEnvelope envelope){
        message_queue.add(envelope);
    }

    public void addErrorMessage(LobbyPlayer player, String message){
        errorMessages.put(player, message);
    }

    private void notifySubscribers() {
        MenuContent menuContent = new MenuContent();

        for (ConnectionCEO subscriber : subscribers) {
            subscriber.sendViewContent(menuContent);
        }
    }

    // sends a viewContent containing an errorMessage to the given subscriber
    private void notifyError(ConnectionCEO subscriber){
        String errorMessage = errorMessages.get(subscriber.getPlayer());
        errorMessages.remove(subscriber.getPlayer());

        MenuContent menuContent = new MenuContent(errorMessage);

        subscriber.sendViewContent(menuContent);
    }

    public void subscribe(ConnectionCEO connectionCEO){
        subscribers.add(connectionCEO);
        notifySubscribers();
    }

    public void unsubscribe(ConnectionCEO connectionCEO){
        subscribers.remove(connectionCEO);
        notifySubscribers();
    }

    public boolean isSubscribed(ConnectionCEO connectionCEO){
        return subscribers.contains(connectionCEO);
    }
}
