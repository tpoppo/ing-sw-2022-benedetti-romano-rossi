package it.polimi.ingsw.view.guicontroller;

import it.polimi.ingsw.network.messages.CreateLobbyMessage;
import it.polimi.ingsw.network.messages.JoinLobbyMessage;
import it.polimi.ingsw.utils.ReducedLobby;
import it.polimi.ingsw.view.GUI;
import it.polimi.ingsw.view.viewcontent.ViewContent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.util.ArrayList;

/**
 * It manages the GUI components (JavaFX) of the menu
 */
public class MenuController implements GUIController{
    @FXML private Text usernameLabel;
    @FXML private VBox lobbiesBox;
    @FXML private Pane createLobbyPane;
    @FXML private ImageView background;
    @FXML private Pane mainPane;
    @FXML private Label errorLabel;

    /**
     * It initializes all the components based on the current GUI.getView() ({@link GUI})
     */
    @Override
    public void setup() {
        ViewContent view = GUI.getView();
        ArrayList<ReducedLobby> lobbies = view.getLobbies();

        closeLobbyCreation();

        // print username
        usernameLabel.setText(GUI.getUsername());
        lobbiesBox.setSpacing(5);

        // clearing error label
        errorLabel.setText("");

        // print lobbies
        int availableLobbies = 0;
        lobbiesBox.getChildren().clear();
        for(ReducedLobby lobby : lobbies){
            // the lobby is shown only if it's not full
            if(lobby.getNumPlayer() != lobby.getMaxPlayers()){
                HBox hBox = new HBox();
                hBox.setAlignment(Pos.CENTER);
                Label lobbyText = new Label("Lobby " + lobby.getID());
                lobbyText.setFont(Font.font("System", FontWeight.BOLD, 20));
                Label sizeText = new Label(lobby.getNumPlayer() + "/" + lobby.getMaxPlayers());
                sizeText.setFont(Font.font("System", FontWeight.BOLD, 18));

                hBox.getChildren().add(lobbyText);
                hBox.setSpacing(380);
                hBox.getChildren().add(sizeText);

                StackPane stackPane = new StackPane();
                stackPane.getChildren().add(hBox);

                stackPane.getStyleClass().add("lobbyButton");

                stackPane.setOnMouseClicked(mouseEvent -> {
                    JoinLobbyMessage joinlobbymessage = new JoinLobbyMessage(lobby.getID());
                    GUI.getClientSocket().send(joinlobbymessage);
                });

                lobbiesBox.getChildren().add(stackPane);
                availableLobbies++;
            }
        }

        // error management
        if(availableLobbies == 0)
            errorLabel.setText("No lobbies available");

        if(view.getErrorMessage() != null){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("A player disconnected from the game");
            alert.show();
            GUI.me.setup();
        }

        if(GUI.isCreatingLobby())
            openLobbyCreation();
    }

    /**
     * It is the mouse event that tries to create a new lobby.
     * It is called by the on click event of the specific button
     * @param event mouse event
     */
    public void createLobby(MouseEvent event){
        CreateLobbyMessage createLobbyMessage = null;

        if(((Shape)event.getSource()).getId().equals("two")){
            createLobbyMessage = new CreateLobbyMessage(2);
        } else if (((Shape)event.getSource()).getId().equals("three")) {
            createLobbyMessage = new CreateLobbyMessage(3);
        }

        GUI.getClientSocket().send(createLobbyMessage);
    }

    /**
     * It shows the create lobby window
     */
    public void openLobbyCreation(){
        createLobbyPane.setVisible(true);
        mainPane.setDisable(true);
        mainPane.setEffect(new ColorAdjust(0.0, 0.0, -0.5, 0.0));
        background.setEffect(new ColorAdjust(0.0, 0.0, -0.5, 0.0));
        GUI.setCreatingLobby(true);
    }

    /**
     * It hides the create lobby window
     */
    public void closeLobbyCreation(){
        createLobbyPane.setVisible(false);
        mainPane.setDisable(false);
        mainPane.setEffect(null);
        background.setEffect(null);
        GUI.setCreatingLobby(false);
    }
}
