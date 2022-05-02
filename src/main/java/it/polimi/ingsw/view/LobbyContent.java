package it.polimi.ingsw.view;

import it.polimi.ingsw.controller.GameHandler;
import it.polimi.ingsw.network.HandlerType;
import it.polimi.ingsw.controller.LobbyHandler;

public class LobbyContent extends ViewContent{
    public LobbyContent(GameHandler gameHandler, LobbyHandler lobbyHandler, HandlerType currentHandler, String errorMessage) {
        super(gameHandler, lobbyHandler, currentHandler, errorMessage);
    }
}
