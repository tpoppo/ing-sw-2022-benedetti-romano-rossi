package it.polimi.ingsw.network.messages;

import it.polimi.ingsw.controller.LobbyPlayer;
import it.polimi.ingsw.network.ConnectionCEO;
import it.polimi.ingsw.network.messages.ClientMessage;

public record MessageEnvelope(LobbyPlayer sender, ClientMessage message, ConnectionCEO connectionCEO) {}
