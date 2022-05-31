package it.polimi.ingsw.view.guicontroller;

import it.polimi.ingsw.client.ClientSocket;
import it.polimi.ingsw.network.messages.CreateLobbyMessage;
import it.polimi.ingsw.network.messages.JoinLobbyMessage;
import it.polimi.ingsw.utils.ReducedLobby;
import it.polimi.ingsw.view.GUI;
import it.polimi.ingsw.view.viewcontent.ViewContent;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.control.MenuItem;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class MenuController implements Initializable {

    @FXML
    private SplitMenuButton choose_lobby;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ArrayList<MenuItem> menuitem = new ArrayList<>();
        ViewContent view = GUI.getView();
        ArrayList<ReducedLobby> lobbies = view.getLobbies();

        for(int i=0; i<lobbies.size(); i++){
            menuitem.add(new MenuItem("Lobby " + lobbies.get(i).getID()));
            menuitem.get(i).setText("Lobby " + lobbies.get(i).getID());
        }

        for(int i=0; i<menuitem.size(); i++){
            choose_lobby.getItems().add(menuitem.get(i));
        }

        for(int i=0; i<menuitem.size(); i++){
            final int pos = i;
            menuitem.get(i).setOnAction((ActionEvent)-> {
                JoinLobbyMessage joinlobbymessage = new JoinLobbyMessage(lobbies.get(pos).getID());
                GUI.getClientSocket().send(joinlobbymessage);
            });
        }
    }

    public void Create_Lobby_2_Players(ActionEvent event){
        CreateLobbyMessage createlobbymessage = new CreateLobbyMessage(2);
        GUI.getClientSocket().send(createlobbymessage);
    }

    public void Create_Lobby_3_Players(ActionEvent event){
        CreateLobbyMessage createLobbyMessage = new CreateLobbyMessage(3);
        GUI.getClientSocket().send(createLobbyMessage);
    }

    /*public void Choose_Lobby(ActionEvent event){
        ArrayList<MenuItem> menuitem = new ArrayList<>();
        ViewContent view = GUI.getView();
        ArrayList<ReducedLobby> lobbies = view.getLobbies();

        for(int i=0; i<lobbies.size(); i++){
            menuitem.add(new MenuItem("Lobby " + lobbies.get(i).getID()));
            menuitem.get(i).setText("Lobby " + lobbies.get(i).getID());
        }

        for(int i=0; i<menuitem.size(); i++){
            choose_lobby.getItems().add(menuitem.get(i));
        }

        for(int i=0; i<menuitem.size(); i++){
            final int pos = i;
            menuitem.get(i).setOnAction((ActionEvent)-> {
                JoinLobbyMessage joinlobbymessage = new JoinLobbyMessage(lobbies.get(pos).getID());
                GUI.getClientSocket().send(joinlobbymessage);
            });
        }
    }*/
}
