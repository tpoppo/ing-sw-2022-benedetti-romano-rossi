package it.polimi.ingsw.view.guicontroller;

import it.polimi.ingsw.controller.LobbyHandler;
import it.polimi.ingsw.controller.LobbyPlayer;
import it.polimi.ingsw.network.messages.ChooseWizardMessage;
import it.polimi.ingsw.network.messages.StartGameMessage;
import it.polimi.ingsw.utils.exceptions.FullLobbyException;
import it.polimi.ingsw.utils.exceptions.WizardNotAvailableException;
import it.polimi.ingsw.view.GUI;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.effect.BoxBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;


import java.net.URL;
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
    @FXML
    private ImageView wizard1;
    @FXML
    private ImageView wizard2;
    @FXML
    private ImageView wizard3;
    @FXML
    private ImageView wizard4;

    private List<Label> player_labels;
    private List<ImageView> wizards_selections;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        player_labels = List.of(player0, player1, player2);
        wizards_selections = List.of(wizard1, wizard2, wizard3, wizard4);

        updateLobby();
    }

    public void startGame(ActionEvent actionEvent) {
        GUI.getClientSocket().send(new StartGameMessage(expert_mode.isSelected()));
    }

    private void selectWizard(ActionEvent actionEvent, int value){
        GUI.getClientSocket().send(new ChooseWizardMessage(value));
    }
    public void selectWizard1(ActionEvent actionEvent){
        selectWizard(actionEvent, 1);
    }
    public void selectWizard2(ActionEvent actionEvent){
        selectWizard(actionEvent, 2);
    }
    public void selectWizard3(ActionEvent actionEvent){
        selectWizard(actionEvent, 3);
    }
    public void selectWizard4(ActionEvent actionEvent){
        selectWizard(actionEvent, 4);
    }


    public void updateLobby() {
        // LobbyHandler lobbyHandler = GUI.getView().getLobbyHandler();
        LobbyHandler lobbyHandler = new LobbyHandler(10);
        try {
            lobbyHandler.addPlayer(new LobbyPlayer("tpoppo"));
            lobbyHandler.chooseWizard(1, lobbyHandler.getPlayers().get(0));
        } catch (FullLobbyException | WizardNotAvailableException e) {
            throw new RuntimeException(e);
        }

        // set username and chosen wizard icon
        for(int i=0; i<3; i++){
            if(i < lobbyHandler.getPlayers().size()){
                LobbyPlayer lobbyPlayer = lobbyHandler.getPlayers().get(i);
                player_labels.get(i).setText(lobbyPlayer.getUsername());
                if(lobbyPlayer.getWizard() != null){
                    chosen_wizard0.setImage(new Image("/graphics/assistants/back/back_" +lobbyPlayer.getWizard()+".png"));
                }
            } else{
                player_labels.get(i).setText("");
            }
        }

        // show available wizards
        // disable all
        for(ImageView imageView : wizards_selections){
            imageView.setOnMouseClicked(mouseEvent -> {});
            imageView.setEffect(new BoxBlur());
        }

        for(Integer wizard : lobbyHandler.getAvailableWizards()){
            ImageView imageView = wizards_selections.get(wizard-1);
            imageView.setOnMouseClicked(mouseEvent -> {});
            imageView.setEffect(null);
        }
    }
}
