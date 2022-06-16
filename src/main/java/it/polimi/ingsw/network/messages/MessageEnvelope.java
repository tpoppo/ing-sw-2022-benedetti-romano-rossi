package it.polimi.ingsw.network.messages;

import it.polimi.ingsw.controller.LobbyPlayer;
import it.polimi.ingsw.network.ConnectionCEO;
/**
 * This class is a wrapper around ClientMessage with additional information of the sender
 */
public record MessageEnvelope(LobbyPlayer sender, ClientMessage message, ConnectionCEO connectionCEO) {}
