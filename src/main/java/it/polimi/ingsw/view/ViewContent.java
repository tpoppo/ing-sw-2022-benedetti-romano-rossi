package it.polimi.ingsw.view;

import it.polimi.ingsw.controller.*;

import java.util.ArrayList;

public class ViewContent {
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
}
