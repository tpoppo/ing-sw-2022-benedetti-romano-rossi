package it.polimi.ingsw.view.viewcontent;

import it.polimi.ingsw.network.HandlerType;
import it.polimi.ingsw.controller.LobbyHandler;

import java.io.Serial;

/**
 * This class represents a view sent by the server while the client is in the lobby.
 */
public class LobbyContent extends ViewContent {
    @Serial
    private static final long serialVersionUID = -3625587187827188463L;
    private final LobbyHandler lobbyHandler;
    private final String errorMessage;

    public LobbyContent(LobbyHandler lobbyHandler, String errorMessage) {
        this.errorMessage = errorMessage;
        this.lobbyHandler = lobbyHandler;
    }

    @Override
    public String getErrorMessage() {
        return errorMessage;
    }

    @Override
    public LobbyHandler getLobbyHandler() {
        return lobbyHandler;
    }

    @Override
    public HandlerType getCurrentHandler() {
        return HandlerType.LOBBY;
    }

    @Override
    public String toString() {
        return "LobbyContent{" +
                "errorMessage='" + errorMessage + '\'' +
                ", lobbyHandler=" + lobbyHandler +
                '}';
    }
}
