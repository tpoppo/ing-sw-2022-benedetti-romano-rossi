package it.polimi.ingsw.controller;

import it.polimi.ingsw.controller.messages.ClientMessage;

public class MessageEnvelope {
    private final LobbyPlayer sender;
    private final ClientMessage message;

    public MessageEnvelope(LobbyPlayer sender, ClientMessage message){
        this.sender = sender;
        this.message = message;
    }

    public LobbyPlayer getSender() {
        return sender;
    }

    public ClientMessage getMessage() {
        return message;
    }
}
