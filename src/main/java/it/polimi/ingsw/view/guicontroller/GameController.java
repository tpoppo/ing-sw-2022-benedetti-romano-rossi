package it.polimi.ingsw.view.guicontroller;

import it.polimi.ingsw.client.ClientSocket;
import it.polimi.ingsw.controller.Game;
import it.polimi.ingsw.controller.GameHandler;
import it.polimi.ingsw.controller.GameState;
import it.polimi.ingsw.controller.LobbyPlayer;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.board.Assistant;
import it.polimi.ingsw.network.NetworkManager;
import it.polimi.ingsw.network.messages.ClientMessage;
import it.polimi.ingsw.network.messages.NextStateMessage;
import it.polimi.ingsw.network.messages.PlayAssistantMessage;
import it.polimi.ingsw.network.messages.StatusCode;
import it.polimi.ingsw.utils.Constants;
import it.polimi.ingsw.utils.DeepCopy;
import it.polimi.ingsw.view.GUI;
import it.polimi.ingsw.view.viewcontent.ViewContent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.effect.Shadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.util.List;
import java.util.Queue;
import java.util.ResourceBundle;

public class GameController implements Initializable {

    @FXML
    Label turnLabel;
    @FXML
    Label actionLabel;
    @FXML
    Label usernameLabel;
    @FXML
    private ImageView characterImage0;
    @FXML
    private ImageView characterImage1;
    @FXML
    private ImageView characterImage2;
    @FXML
    private ImageView assistant1, assistant2, assistant3, assistant4, assistant5, assistant6, assistant7, assistant8, assistant9, assistant10;
    @FXML
    private ImageView characterInfoButton;
    @FXML
    private ImageView nextTurnButton;
    @FXML
    private Label firstPlayerLabel;
    @FXML
    private Label secondPlayerLabel;
    @FXML
    private Label thirdPlayerLabel;
    @FXML
    private Label errorMsg;




    private List<ImageView> characterImages;
    private List<ImageView> assistantCards;





    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ViewContent view = GUI.getView();
        System.out.println(view);

        assistantCards = List.of(assistant1, assistant2, assistant3, assistant4, assistant5, assistant6, assistant7, assistant8, assistant9, assistant10);
        characterImages = List.of(characterImage0, characterImage1, characterImage2);
        GameHandler gameHandler = view.getGameHandler();
        Game game = gameHandler.getModel();


        // set values
        turnLabel.setText("Turn: " + gameHandler.getModel().getCurrentPlayer().getUsername());
        usernameLabel.setText(GUI.getUsername());

        // disable characters when not in expert mode
        if(!game.getExpertMode()){
            for(ImageView imageView : characterImages) {
                imageView.setVisible(false);
            }
            characterInfoButton.setVisible(false);
        } else {
            /*
            for(int i=0; i<game.getCharacters().size(); i++){
                characterImages.get(i).setImage(new Image("/graphics/characters/CarteTOT_front" +game.getCharacters().get(i).getClass().getName()+".png"));
            }
            */
        }



        // effect on played character
        Player player = game.usernameToPlayer(GUI.getUsername());
        for(Assistant assistant : Assistant.getAssistants(player.getWizard())){
            ImageView imageView = assistantCards.get(assistant.getPower()-1);

            if(player.getPlayerHand().contains(assistant)){
                imageView.setOnMouseClicked(mouseEvent -> {
                    GUI.getClientSocket().send(new PlayAssistantMessage(player.getPlayerHand().indexOf(assistant)));
                });
                if(!(checkMessage(new PlayAssistantMessage(player.getPlayerHand().indexOf(assistant)), player) == StatusCode.OK)
                        && game.getCurrentPlayer().equals(player)
                        && gameHandler.getCurrentState() == GameState.PLAY_ASSISTANT) {
                    imageView.setEffect(new ColorAdjust(0.0, 0.0, -0.5, 0.0));
                }
            } else {
                imageView.setEffect(new ColorAdjust(0.0, 0.0, -0.5, 0.0));
            }


        }

        // setup next turn button
        if(!game.getExpertMode()){
            nextTurnButton.setVisible(false);
        } else if(checkMessage(new NextStateMessage(), player) == StatusCode.OK){
            nextTurnButton.setOnMouseClicked(mouseEvent -> {
               GUI.getClientSocket().send(new NextStateMessage());
            });
        } else {
            nextTurnButton.setEffect(new ColorAdjust(0.0, 0.0, -0.5, 0.0));
        }

        // setup the error message
        if(view.getErrorMessage() != null){
            errorMsg.setText(view.getErrorMessage());
        } else {
            errorMsg.setVisible(false);
        }


        // enable and create objects depending on the current state
        String action_text = null;
        if (gameHandler.getModel().getCurrentPlayer().getUsername().equals(GUI.getUsername())) { // if it is your turn
            switch (gameHandler.getCurrentState()) {
                case PLAY_ASSISTANT -> {
                    action_text = "Play an assistant: ";
                    preparePlayAssistant();
                }
                case CHOOSE_CLOUD -> {
                    action_text = "Choose a cloud: ";
                }
                case MOVE_MOTHER_NATURE -> {
                    action_text = "Move mother nature: ";
                }
                case MOVE_STUDENT -> {
                    action_text = "Move a student (" + gameHandler.getStudentMoves() + " left): ";
                }
                case ACTIVATE_CHARACTER -> {
                    action_text = "Activate a character: ";
                }
                case ENDING -> {
                    action_text = "The end: ";
                }
            }
        }
        actionLabel.setText(action_text);
    }

    private void preparePlayAssistant() {
        Game game = GUI.getView().getGameHandler().getModel();
        Player player = game.usernameToPlayer(GUI.getUsername());
        for(Assistant assistant : Assistant.getAssistants(player.getWizard())){
            ImageView imageView = assistantCards.get(assistant.getPower()-1);

            if(checkMessage(new PlayAssistantMessage(player.getPlayerHand().indexOf(assistant)), player) == StatusCode.OK) { // can be used
                imageView.setOnMouseClicked(mouseEvent -> {
                    GUI.getClientSocket().send(new PlayAssistantMessage(player.getPlayerHand().indexOf(assistant)));
                });
            } else {
                imageView.setEffect(new ColorAdjust(0.0, 0.0, -0.5, 0.0));
            }
        }
    }



    public void onMouseEnteredAssistant(MouseEvent event) {
        ImageView imageView = (ImageView) event.getSource();
        imageView.setY(imageView.getY()-50);
    }
    public void onMouseExitedAssistant(MouseEvent event) {
        ImageView imageView = (ImageView) event.getSource();
        imageView.setY(imageView.getY()+50);
    }

    StatusCode checkMessage(ClientMessage clientMessage, Player player){
        GameHandler gameHandler = (GameHandler) DeepCopy.copy(GUI.getView().getGameHandler());
        NetworkManager networkManager = NetworkManager.createNetworkManager(gameHandler);
        return clientMessage.handle(networkManager, player);
    }

}
