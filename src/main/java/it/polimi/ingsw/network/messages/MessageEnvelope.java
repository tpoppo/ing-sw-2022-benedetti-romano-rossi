package it.polimi.ingsw.network.messages;

import it.polimi.ingsw.controller.LobbyPlayer;
import it.polimi.ingsw.network.ConnectionCEO;

public record MessageEnvelope(LobbyPlayer sender, ClientMessage message, ConnectionCEO connectionCEO) {}
