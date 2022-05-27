package it.polimi.ingsw.view.viewcontent;
import it.polimi.ingsw.controller.GameHandler;
import it.polimi.ingsw.controller.LobbyHandler;
import it.polimi.ingsw.network.*;
import it.polimi.ingsw.utils.ReducedLobby;

import java.io.Serializable;
import java.util.ArrayList;

public abstract class ViewContent implements Serializable{
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