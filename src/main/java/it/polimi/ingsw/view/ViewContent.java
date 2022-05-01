package it.polimi.ingsw.view;
import it.polimi.ingsw.controller.*;

import java.io.Serializable;
import java.util.ArrayList;

public class ViewContent implements Serializable{
    private final GameHandler gameHandler;
    private final LobbyHandler lobbyHandler;
    private final HandlerType currentHandler;
    private final String errorMessage;
    private final ArrayList<NetworkManager> lobbies;

    public ViewContent(GameHandler gameHandler, LobbyHandler lobbyHandler, HandlerType currentHandler, String errorMessage) {
        this.gameHandler = gameHandler;
        this.lobbyHandler = lobbyHandler;
        this.currentHandler = currentHandler;
        this.errorMessage = errorMessage;

        lobbies = Server.getInstance().getLobbies();
    }

    public GameHandler getGameHandler() {
        return gameHandler;
    }

    public LobbyHandler getLobbyHandler() {
        return lobbyHandler;
    }

    public HandlerType getCurrentHandler() {
        return currentHandler;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public ArrayList<NetworkManager> getLobbies() {
        return new ArrayList<>(lobbies);
    }
}
