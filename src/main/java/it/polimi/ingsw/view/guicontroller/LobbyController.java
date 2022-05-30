package it.polimi.ingsw.view.guicontroller;

import it.polimi.ingsw.controller.LobbyHandler;
import it.polimi.ingsw.controller.LobbyPlayer;
import it.polimi.ingsw.network.messages.ChooseWizardMessage;
import it.polimi.ingsw.network.messages.StartGameMessage;
import it.polimi.ingsw.view.GUI;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.effect.BoxBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;


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
    @FXML
    private Button start_game;

    private List<Label> player_labels;
    private List<ImageView> wizards_selections;
    private List<ImageView> chosen_wizards;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        player_labels = List.of(player0, player1, player2);
        chosen_wizards = List.of(chosen_wizard0, chosen_wizard1, chosen_wizard2);

        wizards_selections = List.of(wizard1, wizard2, wizard3, wizard4);

        updateLobby();
    }

    public void startGame(ActionEvent actionEvent) {
        GUI.getClientSocket().send(new StartGameMessage(expert_mode.isSelected()));
    }

    private void selectWizard(MouseEvent mouseEvent, int value){
        GUI.getClientSocket().send(new ChooseWizardMessage(value));
    }
    public void selectWizard1(MouseEvent mouseEvent){
        selectWizard(mouseEvent, 1);
    }
    public void selectWizard2(MouseEvent mouseEvent){
        selectWizard(mouseEvent, 2);
    }
    public void selectWizard3(MouseEvent mouseEvent){
        selectWizard(mouseEvent, 3);
    }
    public void selectWizard4(MouseEvent mouseEvent){
        selectWizard(mouseEvent, 4);
    }

    public void updateLobby() {
        LobbyHandler lobbyHandler = GUI.getView().getLobbyHandler();

        // set username and chosen wizard icon
        boolean all_has_chosen = true;
        for(int i=0; i<3; i++){
            if(i < lobbyHandler.getPlayers().size()){
                LobbyPlayer lobbyPlayer = lobbyHandler.getPlayers().get(i);
                player_labels.get(i).setText(lobbyPlayer.getUsername());
                if(lobbyPlayer.getWizard() != null){
                    chosen_wizards.get(i).setImage(new Image("/graphics/assistants/back/back_" +lobbyPlayer.getWizard()+".png"));
                } else {
                    all_has_chosen = false;
                }
            } else{
                player_labels.get(i).setText("");
            }
        }

        boolean has_chosen_wizard = lobbyHandler.getPlayers()
                .stream()
                .filter(player -> player.getWizard() != null)
                .filter(player -> player.getUsername().equals(GUI.getUsername()))
                .count() == 1;

        // show available wizards
        // disable all
        for(int wizard=1; wizard<=4; wizard++){
            ImageView imageView = wizards_selections.get(wizard-1);
            if(!has_chosen_wizard && lobbyHandler.getAvailableWizards().contains(wizard)){
                imageView.setEffect(null);
            } else {
                imageView.setOnMouseClicked(mouseEvent -> {});
                imageView.setEffect(new BoxBlur());
            }
        }

        if(!all_has_chosen || lobbyHandler.getPlayers().size() == 1) {
            start_game.setDisable(true);
            start_game.setOnAction(event -> {});
        }
    }
}
