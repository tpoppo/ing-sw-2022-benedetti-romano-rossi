package it.polimi.ingsw.view;

import it.polimi.ingsw.controller.GameHandler;
import it.polimi.ingsw.controller.HandlerType;
import it.polimi.ingsw.controller.LobbyHandler;

public class ViewContent {
    private final GameHandler gameHandler;
    private final LobbyHandler lobbyHandler;
    private final HandlerType currentHandler;
    private final String errorMessage;

    public ViewContent(GameHandler gameHandler, LobbyHandler lobbyHandler, HandlerType currentHandler, String errorMessage) {
        this.gameHandler = gameHandler;
        this.lobbyHandler = lobbyHandler;
        this.currentHandler = currentHandler;
        this.errorMessage = errorMessage;
    }
}
