package it.polimi.ingsw.controller;

import it.polimi.ingsw.controller.messages.ClientMessage;

public class MessageEnvelope {
    private LobbyPlayer sender;
    private ClientMessage message;

    public MessageEnvelope(LobbyPlayer sender, ClientMessage message){
        this.sender = sender;
        this.message = message;
    }
}
