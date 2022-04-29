package it.polimi.ingsw.controller;

import it.polimi.ingsw.controller.messages.ClientMessage;

public record MessageEnvelope(LobbyPlayer sender, ClientMessage message) {}
