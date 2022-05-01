package it.polimi.ingsw.view;

import it.polimi.ingsw.controller.GameHandler;
import it.polimi.ingsw.controller.HandlerType;
import it.polimi.ingsw.controller.LobbyHandler;

public class GameContent extends ViewContent{
    public GameContent(GameHandler gameHandler, LobbyHandler lobbyHandler, HandlerType currentHandler, String errorMessage) {
        super(gameHandler, lobbyHandler, currentHandler, errorMessage);
    }
}
