package it.polimi.ingsw.view;

import it.polimi.ingsw.controller.GameHandler;
import it.polimi.ingsw.network.HandlerType;
import it.polimi.ingsw.controller.LobbyHandler;

public class GameContent extends ViewContent{
    private final GameHandler gameHandler;
    private final String errorMessage;

    public GameContent(GameHandler gameHandler, String errorMessage) {
        this.gameHandler = gameHandler;
        this.errorMessage = errorMessage;
    }

    @Override
    public GameHandler getGameHandler() {
        return gameHandler;
    }

    @Override
    public String getErrorMessage() {
        return errorMessage;
    }

    @Override
    public HandlerType getCurrentHandler() {
        return HandlerType.GAME;
    }
}
