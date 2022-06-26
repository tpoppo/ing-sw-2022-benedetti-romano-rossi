package it.polimi.ingsw.view.guicontroller;

import it.polimi.ingsw.controller.LobbyHandler;
import it.polimi.ingsw.controller.LobbyPlayer;
import it.polimi.ingsw.network.messages.ChooseWizardMessage;
import it.polimi.ingsw.network.messages.StartGameMessage;
import it.polimi.ingsw.view.GUI;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

import java.util.List;

/**
 * It manages all the components of the lobby in the GUI
 */
public class LobbyController implements GUIController {
    @FXML private Pane mainPane;
    @FXML private ImageView background;
    @FXML private CheckBox expert_mode;
    @FXML private Label player0, player1, player2;
    @FXML private ImageView chosen_wizard0, chosen_wizard1, chosen_wizard2;
    @FXML private Pane select_wizard;
    @FXML private GridPane wizard_grid;
    @FXML private Button start_game;

    @FXML private Label lobby_id;

    private List<Label> player_labels;
    private List<ImageView> chosen_wizards;

    /**
     * It initializes the elements
     */
    @Override
    public void setup() {
        player_labels = List.of(player0, player1, player2);
        chosen_wizards = List.of(chosen_wizard0, chosen_wizard1, chosen_wizard2);
        reset();
        updateLobby();
    }

    /**
     * It clears all the components
     */
    private void reset() {
        mainPane.setDisable(false);
        mainPane.setEffect(null);
        background.setEffect(null);
        wizard_grid.getChildren().clear();
        select_wizard.setVisible(true);
    }

    /**
     * It updates the elements depending on the current view
     */
    public void updateLobby() {
        LobbyHandler lobbyHandler = GUI.getView().getLobbyHandler();

        // set username and chosen wizard icon
        boolean all_has_chosen = true;
        for(int i=0; i<3; i++) {
            if (i < lobbyHandler.getPlayers().size()) {
                LobbyPlayer lobbyPlayer = lobbyHandler.getPlayers().get(i);
                player_labels.get(i).setText(lobbyPlayer.getUsername());
                if (lobbyPlayer.getWizard() != null) {
                    chosen_wizards.get(i).setImage(new Image("/graphics/assistants/back/back_" + lobbyPlayer.getWizard() + ".png"));
                } else {
                    chosen_wizards.get(i).setImage(new Image("/graphics/assistants/back/back_others.png"));
                    all_has_chosen = false;
                }
            } else {
                player_labels.get(i).setText("???");
                chosen_wizards.get(i).setImage(new Image("/graphics/assistants/back/back_others.png"));
            }
        }

        lobby_id.setText("Lobby ID: "+lobbyHandler.ID);

        boolean has_chosen_wizard = lobbyHandler.getPlayers()
                .stream()
                .filter(player -> player.getWizard() != null)
                .filter(player -> player.getUsername().equals(GUI.getUsername()))
                .count() == 1;

        // wizard selection pop-up
        // show available wizards
        if(!has_chosen_wizard){
            mainPane.setDisable(true);
            mainPane.setEffect(new ColorAdjust(0.0, 0.0, -0.5, 0.0));
            background.setEffect(new ColorAdjust(0.0, 0.0, -0.5, 0.0));

            for(int wizard=1; wizard<=4; wizard++){
                if(lobbyHandler.getAvailableWizards().contains(wizard)){
                    ImageView imageView = new ImageView();
                    imageView.setImage(new Image("/graphics/assistants/back/back_"+wizard+".png"));
                    imageView.setFitWidth(125);
                    imageView.setPreserveRatio(true);
                    imageView.setCursor(Cursor.HAND);

                    int finalWizard = wizard;
                    imageView.setOnMouseClicked(mouseEvent -> selectWizard(mouseEvent, finalWizard));

                    wizard_grid.setHgap(20);
                    wizard_grid.addRow(0, imageView);
                }
            }
        } else {
            mainPane.setDisable(false);
            mainPane.setEffect(null);
            background.setEffect(null);
            select_wizard.setVisible(false);
        }

        start_game.setDisable(!all_has_chosen || lobbyHandler.getPlayers().size() == 1);
    }

    /**
     * It sends the {@link StartGameMessage}
     * @param actionEvent from the onAction event
     */
    public void startGame(ActionEvent actionEvent) {
        GUI.getClientSocket().send(new StartGameMessage(expert_mode.isSelected()));
    }

    /**
     * It sends the {@link ChooseWizardMessage}
     * @param mouseEvent from the onAction event
     * @param value selected wizard
     */
    private void selectWizard(MouseEvent mouseEvent, int value) {
        GUI.getClientSocket().send(new ChooseWizardMessage(value));
    }
}
