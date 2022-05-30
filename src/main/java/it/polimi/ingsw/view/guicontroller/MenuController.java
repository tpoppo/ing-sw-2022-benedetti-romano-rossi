package it.polimi.ingsw.view.guicontroller;

import it.polimi.ingsw.client.ClientSocket;
import it.polimi.ingsw.network.messages.CreateLobbyMessage;
import it.polimi.ingsw.network.messages.JoinLobbyMessage;
import it.polimi.ingsw.utils.ReducedLobby;
import it.polimi.ingsw.view.GUI;
import it.polimi.ingsw.view.viewcontent.ViewContent;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.control.MenuItem;

import java.util.ArrayList;

public class MenuController {

    @FXML
    private SplitMenuButton choose_lobby;

    public void Create_Lobby_2_Players(ActionEvent event){
        CreateLobbyMessage createlobbymessage = new CreateLobbyMessage(2);
        GUI.getClientSocket().send(createlobbymessage);
    }

    public void Create_Lobby_3_Players(ActionEvent event){
        CreateLobbyMessage createLobbyMessage = new CreateLobbyMessage(3);
        GUI.getClientSocket().send(createLobbyMessage);
    }

    public void Choose_Lobby(ActionEvent event){
        ArrayList<MenuItem> menuitem = new ArrayList<>();
        ViewContent view = GUI.getView();
        ArrayList<ReducedLobby> lobbies = view.getLobbies();

        for(int i=0; i<lobbies.size(); i++){
            menuitem.add(new MenuItem("Lobby " + lobbies.get(i).getID()));
        }
        choose_lobby.getItems().addAll(menuitem);

        for(int i=0; i< menuitem.size(); i++){
            final int pos = i;
            menuitem.get(i).setText("Lobby " + lobbies.get(i).getID());
            menuitem.get(i).setOnAction((e)-> {
                JoinLobbyMessage joinlobbymessage = new JoinLobbyMessage(lobbies.get(pos).getID());
                GUI.getClientSocket().send(joinlobbymessage);
            });
        }
    }
}
