package it.polimi.ingsw.view.guicontroller;

import it.polimi.ingsw.controller.LobbyHandler;
import it.polimi.ingsw.controller.LobbyPlayer;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.network.messages.StartGameMessage;
import it.polimi.ingsw.view.GUI;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;


import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class LobbyController implements Initializable {

    @FXML
    private CheckBox expert_mode;
    @FXML
    private Label player0;
    @FXML
    private Label player1;
    @FXML
    private Label player2;
    @FXML
    private ImageView chosen_wizard0;
    @FXML
    private ImageView chosen_wizard1;
    @FXML
    private ImageView chosen_wizard2;

    private List<Label> player_labels;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        player_labels = List.of(player0, player1, player2);

        updateLobby();
    }

    public void startGame(ActionEvent actionEvent) {
        GUI.getClientSocket().send(new StartGameMessage(expert_mode.isSelected()));
    }

    public void updateLobby(){
        LobbyHandler lobbyHandler = GUI.getView().getLobbyHandler();
        for(int i=0; i<3; i++){
            if(i < lobbyHandler.getPlayers().size()){
                LobbyPlayer lobbyPlayer = lobbyHandler.getPlayers().get(i);
                player_labels.get(i).setText(lobbyPlayer.getUsername());
                if(lobbyPlayer.getWizard() == null){
                    chosen_wizard0.setImage(new Image("assets/assistants/back_"+lobbyPlayer.getWizard()+".png"));
                }
            } else{
                player_labels.get(i).setText("");
            }
        }
    }
}
