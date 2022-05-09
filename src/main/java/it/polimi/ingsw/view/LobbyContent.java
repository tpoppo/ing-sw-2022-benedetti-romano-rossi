package it.polimi.ingsw.view;

import it.polimi.ingsw.controller.GameHandler;
import it.polimi.ingsw.network.HandlerType;
import it.polimi.ingsw.controller.LobbyHandler;

public class LobbyContent extends ViewContent {
    private final String errorMessage;
    private final LobbyHandler lobbyHandler;

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
