package it.polimi.ingsw.view.viewcontent;
import it.polimi.ingsw.controller.GameHandler;
import it.polimi.ingsw.controller.LobbyHandler;
import it.polimi.ingsw.network.*;
import it.polimi.ingsw.utils.ReducedLobby;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * This abstract class represents a generic view sent by the server
 */
public abstract class ViewContent implements Serializable{
    @Serial
    private static final long serialVersionUID = 5549652044436173756L;

    public GameHandler getGameHandler(){
        return null;
    }

    public LobbyHandler getLobbyHandler(){
        return null;
    }

    public HandlerType getCurrentHandler(){
        return null;
    }

    public String getErrorMessage(){
        return null;
    }

    public ArrayList<ReducedLobby> getLobbies(){
        return new ArrayList<>();
    }

}