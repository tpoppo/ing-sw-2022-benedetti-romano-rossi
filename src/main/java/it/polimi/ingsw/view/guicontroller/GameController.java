package it.polimi.ingsw.view.guicontroller;

import it.polimi.ingsw.controller.Game;
import it.polimi.ingsw.controller.GameHandler;
import it.polimi.ingsw.controller.GameState;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.board.Assistant;
import it.polimi.ingsw.network.NetworkManager;
import it.polimi.ingsw.network.messages.ClientMessage;
import it.polimi.ingsw.network.messages.NextStateMessage;
import it.polimi.ingsw.network.messages.PlayAssistantMessage;
import it.polimi.ingsw.network.messages.StatusCode;
import it.polimi.ingsw.utils.DeepCopy;
import it.polimi.ingsw.model.board.Color;
import it.polimi.ingsw.model.board.Professors;
import it.polimi.ingsw.model.board.SchoolBoard;
import it.polimi.ingsw.model.board.Students;
import it.polimi.ingsw.view.GUI;
import it.polimi.ingsw.view.viewcontent.ViewContent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.scene.control.Label;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.util.*;

public class GameController implements Initializable {
    @FXML
    private GridPane entranceGrid;
    @FXML
    private GridPane greenDining, redDining, yellowDining, magentaDining, cyanDining;
    @FXML
    private GridPane professorsGrid;
    @FXML
    private GridPane towersGrid;
    @FXML
    private GridPane schoolboardButtonsGrid;
    @FXML
    private Label turnLabel;
    @FXML
    private Label actionLabel;
    @FXML
    private Label usernameLabel;
    @FXML
    private ImageView assistant1, assistant2, assistant3, assistant4, assistant5, assistant6, assistant7, assistant8, assistant9, assistant10;
    @FXML
    private ImageView nextTurnButton;
    @FXML
    private GridPane playOrderGrid;
    @FXML
    private Label errorMsg;

    // expert mode components
    @FXML
    private Pane charactersPane;
    @FXML
    private StackPane coinsPane;
    @FXML
    private ImageView characterImage0, characterImage1, characterImage2;

    private ViewContent view;
    private List<ImageView> characterImages;
    private List<ImageView> assistantCards;
    private Player thisPlayer;
    private Player schoolboardPlayer;
    private Map<Player, TowerColor> playerTowerColorMap;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.view = GUI.getView();
        System.out.println(view);

        GameHandler gameHandler = view.getGameHandler();
        Game game = gameHandler.getModel();
        assistantCards = List.of(assistant1, assistant2, assistant3, assistant4, assistant5, assistant6, assistant7, assistant8, assistant9, assistant10);
        characterImages = List.of(characterImage0, characterImage1, characterImage2);
        playerTowerColorMap = new HashMap<>();
        thisPlayer = game.usernameToPlayer(GUI.getUsername());
        schoolboardPlayer = thisPlayer;

        // setting up player - towerColor association
        for(int i=0; i<view.getGameHandler().getModel().getPlayers().size(); i++)
            playerTowerColorMap.put(view.getGameHandler().getModel().getPlayers().get(i), TowerColor.values()[i]);

        setupState();
        updateSchoolboard();
        setupAssistants();
        setupErrorMessage();
        setupActions();
        setupPlayOrder();

        // expert mode
        if(view.getGameHandler().getModel().getExpertMode()) {
            setupNextTurnButton();
        }else{
            hideExpertModeComponents();
        }

        /*
        // disable characters when not in expert mode
        if(!game.getExpertMode()){
            for(ImageView imageView : characterImages) {
                imageView.setVisible(false);
            }
            characterInfoButton.setVisible(false);
        } else {

            for(int i=0; i<game.getCharacters().size(); i++){
                characterImages.get(i).setImage(new Image("/graphics/characters/CarteTOT_front" +game.getCharacters().get(i).getClass().getName()+".png"));
            }

        }

         */
    }

    private void setupPlayOrder(){
        Queue<Player> playOrder = view.getGameHandler().getModel().getPlayOrder();

        for(Player player : playOrder)
            System.out.println(player.getUsername());

        playOrderGrid.getChildren().clear();
        // fill the grid with the usernames
        int count = 0;
        for(Player player : playOrder){
            Label playerUsername = new Label(player.getUsername());
            playerUsername.getStyleClass().clear();
            playerUsername.getStyleClass().add("username");
            playOrderGrid.addRow(count, playerUsername);

            count++;
        }
    }

    private void setupActions(){
        GameHandler gameHandler = view.getGameHandler();

        // enable and create objects depending on the current state
        String action_text = null;
        if (gameHandler.getModel().getCurrentPlayer().getUsername().equals(GUI.getUsername())) { // if it is your turn
            switch (gameHandler.getCurrentState()) {
                case PLAY_ASSISTANT -> {
                    action_text = "Play an assistant: ";
                    preparePlayAssistant();
                    // FIXME: I don't like this
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

    private void setupState(){
        String currentPlayerUsername = view.getGameHandler().getModel().getCurrentPlayer().getUsername();

        turnLabel.setText("Turn: " + currentPlayerUsername);
        usernameLabel.setText(thisPlayer.getUsername());
    }

    private void setupErrorMessage(){
        if(view.getErrorMessage() != null){
            errorMsg.setText(view.getErrorMessage());
        } else {
            errorMsg.setVisible(false);
        }
    }

    private void setupNextTurnButton(){
        if(checkMessage(new NextStateMessage(), thisPlayer) == StatusCode.OK){
            nextTurnButton.setOnMouseClicked(mouseEvent -> GUI.getClientSocket().send(new NextStateMessage()));
        } else {
            nextTurnButton.setEffect(new ColorAdjust(0.0, 0.0, -0.5, 0.0));
        }
    }

    private void setupAssistants(){
        GameHandler gameHandler = view.getGameHandler();
        Game game = gameHandler.getModel();

        for(Assistant assistant : Assistant.getAssistants(thisPlayer.getWizard())){
            ImageView imageView = assistantCards.get(assistant.getPower()-1);

            if(thisPlayer.getPlayerHand().contains(assistant)){
                imageView.setOnMouseClicked(mouseEvent -> {
                    GUI.getClientSocket().send(new PlayAssistantMessage(thisPlayer.getPlayerHand().indexOf(assistant)));
                });

                PlayAssistantMessage playAssistantMessage = new PlayAssistantMessage(thisPlayer.getPlayerHand().indexOf(assistant));
                if(!(checkMessage(playAssistantMessage, thisPlayer) == StatusCode.OK)
                        && game.getCurrentPlayer().equals(thisPlayer)
                        && gameHandler.getCurrentState() == GameState.PLAY_ASSISTANT) {
                    imageView.setEffect(new ColorAdjust(0.0, 0.0, -0.5, 0.0));
                }
            } else {
                imageView.setEffect(new ColorAdjust(0.0, 0.0, -0.5, 0.0));
                imageView.setOnMouseEntered(null);
                imageView.setOnMouseExited(null);
            }
        }
    }
    private void preparePlayAssistant() {
        for(Assistant assistant : Assistant.getAssistants(thisPlayer.getWizard())){
            ImageView imageView = assistantCards.get(assistant.getPower() - 1);

            if(checkMessage(new PlayAssistantMessage(thisPlayer.getPlayerHand().indexOf(assistant)), thisPlayer) == StatusCode.OK) { // can be used
                imageView.setOnMouseClicked(mouseEvent -> {
                    GUI.getClientSocket().send(new PlayAssistantMessage(thisPlayer.getPlayerHand().indexOf(assistant)));
                });
            } else {
                imageView.setEffect(new ColorAdjust(0.0, 0.0, -0.5, 0.0));
            }
        }
    }

    private void hideExpertModeComponents(){
        charactersPane.setVisible(false);
        coinsPane.setVisible(false);
        nextTurnButton.setVisible(false);
    }

    private void updateSchoolboard(){
        // shows this player's schoolboard by default
        fillSchoolboard(schoolboardPlayer);
        setSchoolBoardButtons(schoolboardPlayer);
    }

    private void fillSchoolboard(Player player){
        SchoolBoard schoolBoard = player.getSchoolBoard();
        Students entranceStudents = schoolBoard.getEntranceStudents();
        Students diningStudents = schoolBoard.getDiningStudents();
        Professors professors = schoolBoard.getProfessors();
        int numTowers = schoolBoard.getNumTowers();
        int coins = player.getCoins();

        int count = 0;
        // entrance students
        entranceGrid.getChildren().clear();
        for(Color studentColor : entranceStudents.keySet()){
            for(int i=0; i<entranceStudents.get(studentColor); i++){
                ImageView studentImage = new ImageView("graphics/pieces/" + studentColor.toString().toLowerCase() + "_student.png");
                studentImage = resizeImageView(studentImage, 50, 50);
                entranceGrid.add(studentImage, 1 - count%2, 4 - count/2);

                count++;
            }
        }

        // dining students
        for(Color studentColor : diningStudents.keySet()){
            switch (studentColor){
                case GREEN -> fillDining(diningStudents, studentColor, greenDining);
                case CYAN -> fillDining(diningStudents, studentColor, cyanDining);
                case RED -> fillDining(diningStudents, studentColor, redDining);
                case MAGENTA -> fillDining(diningStudents,studentColor, magentaDining);
                case YELLOW -> fillDining(diningStudents, studentColor, yellowDining);
            }
        }

        // professors
        professorsGrid.getChildren().clear();
        for(Color professorColor : professors){
            ImageView professorImage = new ImageView("graphics/pieces/" + professorColor.toString().toLowerCase() + "_professor.png");
            professorImage = resizeImageView(professorImage, 55, 55, 90);

            switch (professorColor){
                case YELLOW -> professorsGrid.addRow(2, professorImage);
                case GREEN -> professorsGrid.addRow(0, professorImage);
                case MAGENTA -> professorsGrid.addRow(3, professorImage);
                case RED -> professorsGrid.addRow(1, professorImage);
                case CYAN -> professorsGrid.addRow(4, professorImage);
            }
        }

        // towers
        towersGrid.getChildren().clear();
        String towerColor = String.valueOf(playerTowerColorMap.get(player)).toLowerCase();
        count = 0;
        for(int i=0; i<numTowers; i++){
            ImageView towerImage = new ImageView("graphics/pieces/towers/tower_" + towerColor + ".png");
            towerImage = resizeImageView(towerImage, 50, 50);
            towersGrid.add(towerImage, count/2, count%2);

            count++;
        }

        // coins
        Text coinNumber = new Text(String.valueOf(coins));
        ImageView coinsImage = new ImageView("graphics/coin.png");
        coinsImage = resizeImageView(coinsImage, 50, 50);
        coinsPane.getChildren().add(coinsImage);
        coinsPane.getChildren().add(coinNumber);
    }

    private void fillDining(Students diningStudents, Color studentColor, GridPane colorGrid) {
        System.out.println("Adding " + diningStudents.get(studentColor) + " " + studentColor);

        colorGrid.getChildren().clear();
        for(int i=0; i<diningStudents.get(studentColor); i++){
            ImageView studentImage = new ImageView("graphics/pieces/" + studentColor.toString().toLowerCase() + "_student.png");
            studentImage = resizeImageView(studentImage, 45, 45);

            colorGrid.addColumn(i, studentImage);
        }
    }

    private void setSchoolBoardButtons(Player selectedPlayer){
        ArrayList<Player> players = view.getGameHandler().getModel().getPlayers();

        schoolboardButtonsGrid.getChildren().clear();

        int count = 0;
        for(Player player : players){
            Button button = new Button(player.getUsername());
            button.getStyleClass().clear();
            button.getStyleClass().add("schoolboardButton");
            button.setOnMouseClicked(mouseEvent -> {
                fillSchoolboard(player);
                schoolboardButtonsGrid.getChildren().forEach(schoolboardButton -> schoolboardButton.setDisable(false));
                button.setDisable(true);
            });

            if(player.equals(selectedPlayer))
                button.setDisable(true);
            schoolboardButtonsGrid.addColumn(count, button);

            count++;
        }
    }

    private ImageView resizeImageView(ImageView image, int width, int heigth){
        return resizeImageView(image, width, heigth, 0);
    }

    private ImageView resizeImageView(ImageView image, int width, int heigth, int rotate){
        ImageView newImage = new ImageView(image.getImage());
        newImage.setFitWidth(width);
        newImage.setFitHeight(heigth);
        newImage.setRotate(rotate);

        return newImage;
    }

    public void onMouseEnteredAssistant(MouseEvent event) {
        ImageView imageView = (ImageView) event.getSource();
        imageView.setY(imageView.getY() - 50);
    }

    public void onMouseExitedAssistant(MouseEvent event) {
        ImageView imageView = (ImageView) event.getSource();
        imageView.setY(imageView.getY() + 50);
    }

    private StatusCode checkMessage(ClientMessage clientMessage, Player player){
        GameHandler gameHandler = (GameHandler) DeepCopy.copy(GUI.getView().getGameHandler());
        NetworkManager networkManager = NetworkManager.createNetworkManager(gameHandler);
        return clientMessage.handle(networkManager, player);
    }
}
