package it.polimi.ingsw.view.viewcontent;

import it.polimi.ingsw.controller.GameHandler;
import it.polimi.ingsw.network.HandlerType;
import it.polimi.ingsw.view.viewcontent.ViewContent;

import java.io.Serial;

/**
 * This class represents a view sent by the server while the client is playing the game.
 */
public class GameContent extends ViewContent {
    @Serial
    private static final long serialVersionUID = 8999508558097224388L;
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

    @Override
    public String toString() {
        return "GameContent{" +
                "gameHandler=" + gameHandler +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }
}
