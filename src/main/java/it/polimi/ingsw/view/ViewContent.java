package it.polimi.ingsw.view;
import it.polimi.ingsw.controller.GameHandler;
import it.polimi.ingsw.controller.LobbyHandler;
import it.polimi.ingsw.network.*;
import it.polimi.ingsw.utils.ReducedLobby;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ViewContent implements Serializable{
    private final GameHandler gameHandler;
    private final LobbyHandler lobbyHandler;
    private final HandlerType currentHandler;
    private final String errorMessage;
    private final ArrayList<ReducedLobby> lobbies;

    public ViewContent(GameHandler gameHandler, LobbyHandler lobbyHandler, HandlerType currentHandler, String errorMessage) {
        this.gameHandler = gameHandler;
        this.lobbyHandler = lobbyHandler;
        this.currentHandler = currentHandler;
        this.errorMessage = errorMessage;

        this.lobbies = new ArrayList<>();

        List<NetworkManager> lobbies = Server.getInstance().getLobbies();
        lobbies = lobbies.stream().filter(x -> x.getCurrentHandler().equals(HandlerType.LOBBY)).collect(Collectors.toList());

        for(NetworkManager lobby : lobbies) {
            ReducedLobby reducedLobby = new ReducedLobby(lobby);
            this.lobbies.add(reducedLobby);
        }
    }
    public ViewContent(){
        this(null, null, null, null);
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

    public ArrayList<ReducedLobby> getLobbies() {
        return new ArrayList<>(lobbies);
    }

    @Override
    public String toString() {
        return "ViewContent{" +
                "gameHandler=" + gameHandler +
                ", lobbyHandler=" + lobbyHandler +
                ", currentHandler=" + currentHandler +
                ", errorMessage='" + errorMessage + '\'' +
                ", lobbies=" + lobbies +
                '}';
    }
}
