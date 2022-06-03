package it.polimi.ingsw.view.guicontroller;

import it.polimi.ingsw.controller.Game;
import it.polimi.ingsw.controller.GameHandler;
import it.polimi.ingsw.controller.GameState;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.board.*;
import it.polimi.ingsw.model.characters.Character;
import it.polimi.ingsw.network.NetworkManager;
import it.polimi.ingsw.network.messages.*;
import it.polimi.ingsw.utils.DeepCopy;
import it.polimi.ingsw.view.GUI;
import it.polimi.ingsw.view.viewcontent.ViewContent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import it.polimi.ingsw.model.board.Assistant;
import it.polimi.ingsw.model.board.Color;
import it.polimi.ingsw.model.board.Students;
import it.polimi.ingsw.network.messages.ClientMessage;
import it.polimi.ingsw.network.messages.NextStateMessage;
import it.polimi.ingsw.network.messages.PlayAssistantMessage;
import it.polimi.ingsw.network.messages.StatusCode;
import it.polimi.ingsw.model.board.Island;
import it.polimi.ingsw.utils.Pair;

import java.net.URL;
import java.util.*;


public class GameController implements Initializable {
    @FXML
    private Pane mainPane;
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
    private ImageView cloud0, cloud1, cloud2;
    @FXML
    private ImageView nextTurnButton;
    @FXML
    private GridPane playOrderGrid;
    @FXML
    private Label errorMsg;
    @FXML
    private Text bagCapacityText;

    // expert mode components
    @FXML
    private Pane charactersPane;
    @FXML
    private StackPane coinsPane;
    @FXML
    private GridPane charactersGrid;
    @FXML
    private Pane characterPane;
    @FXML
    private ImageView characterImage;
    @FXML
    private Label characterNameLabel;
    @FXML
    private Label characterDescriptionLabel;
    @FXML
    private Text characterCostText;
    @FXML
    private VBox characterStuff;
    @FXML
    private Button activateCharacterButton;
    @FXML
    private ImageView closeCharacterPaneButton;
    @FXML
    private ImageView motherNature;

    @FXML //TODO: not added yet
    private ImageView endingScreen;

    @FXML
    private Pane islandsPane;

    private ViewContent view;
    private List<ImageView> assistantCards;
    private Player thisPlayer;
    private Player schoolboardPlayer;
    private Map<Player, TowerColor> playerTowerColorMap;

    private ImageView selectedEntrance;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.view = GUI.getView();
        ViewContent view = GUI.getView();
        System.out.println(view);

        GameHandler gameHandler = view.getGameHandler();
        Game game = gameHandler.getModel();
        assistantCards = List.of(assistant1, assistant2, assistant3, assistant4, assistant5, assistant6, assistant7, assistant8, assistant9, assistant10);
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
        setupCloud();
        setupActions();
        setupPlayOrder();
        bagCapacityText.setText(String.valueOf(game.getBag().capacity()));

        // expert mode
        if(view.getGameHandler().getModel().getExpertMode()) {
            setupNextTurnButton();
            setupCharacters();
        }else{
            hideExpertModeComponents();
        }

        if(GUI.getSelectingCharacter() != null)
            showCharacterInfo(GUI.getSelectingCharacter());

        playerTowerColorMap = new HashMap<>();

        // setting up player - towerColor association
        for(int i=0; i<view.getGameHandler().getModel().getPlayers().size(); i++)
            playerTowerColorMap.put(view.getGameHandler().getModel().getPlayers().get(i), TowerColor.values()[i]);

        double x = islandsPane.getWidth();
        double y = islandsPane.getHeight();
        double cx = x + (islandsPane.getPrefWidth()) / 2;
        double cy = y + (islandsPane.getPrefHeight()) / 2;
        ArrayList<Island> islands = view.getGameHandler().getModel().getIslands();
        int n_items = islands.size();
        double delta_angle = 360 / n_items;
        double current_angle = 180;
        ArrayList<Double> angle = new ArrayList<Double>();
        for(int i=0; i<n_items; i++){
            angle.add(current_angle);
            current_angle -= delta_angle;
        }
        ArrayList<Pair<Double, Double> > islands_centers = new ArrayList<>();
        double d = 300;
        for(int i=0; i<n_items; i++){
            Pair pair = new Pair(cx + d * Math.cos(angle.get(i) * Math.PI / 180), cy - d * Math.sin(angle.get(i) * Math.PI / 180));
            islands_centers.add(pair);
        }
        for(int i=0; i<n_items; i++){
            ImageView image_view = new ImageView();
            Image image = new Image("/graphics/islands/" + i % 3 + ".png");
            image_view.setImage(image);
            int dimx = (int)(0.5 * image.getWidth());
            int dimy = (int)(0.5 * image.getHeight());
            image_view = resizeImageView(image_view, dimx, dimy);
            image_view.setX(islands_centers.get(i).getFirst() - image_view.getFitWidth() / 2);
            image_view.setY(islands_centers.get(i).getSecond() - image_view.getFitHeight() / 2);
            islandsPane.getChildren().add(image_view);
        }

        for(int i=0; i<n_items; i++){
            int n_students = 0;
            for(Map.Entry<Color, Integer> entry : islands.get(i).getStudents().entrySet()) {
                Color key = entry.getKey();
                int value = entry.getValue();
                if (value > 0) {
                    n_students++;
                }
            }
            switch(n_students){
                //:TODO case(0), case(1), case(2)
                default:
                    System.out.println(i);
                    ImageView image_view = new ImageView();
                    Image image = new Image("/graphics/islands/" + i % 3 + ".png");
                    double cix = islands_centers.get(i).getFirst();
                    double ciy = islands_centers.get(i).getSecond();
                    double xi = cix - image_view.getFitWidth() / 2;
                    double yi = ciy - image_view.getFitHeight() / 2;
                    ArrayList<Double> angle_island = new ArrayList<Double>();
                    double delta_angle_island = 360 / n_students;
                    double current_angle_island = 180;
                    for(int j=0; j<n_students; j++){
                        angle_island.add(current_angle_island);
                        current_angle_island -= delta_angle_island;
                    }
                    ArrayList<Pair<Double, Double> > students_centers = new ArrayList<>();
                    double di = 35;
                    for(int j=0; j<n_students; j++){
                        Pair pair = new Pair(cix + di * Math.cos(angle_island.get(j) * Math.PI / 180), ciy - di * Math.sin(angle_island.get(j) * Math.PI / 180));
                        students_centers.add(pair);
                    }
                    int position = 0;
                    for(Map.Entry<Color, Integer> entry : islands.get(i).getStudents().entrySet()){
                        Color key = entry.getKey();
                        int value = entry.getValue();
                        String current_color = new String();
                        if(key == Color.CYAN) current_color = "cyan";
                        if(key == Color.GREEN) current_color = "green";
                        if(key == Color.MAGENTA) current_color = "magenta";
                        if(key == Color.RED) current_color = "red";
                        if(key == Color.YELLOW) current_color = "yellow";
                        if(value > 0){
                            ImageView image_view1 = new ImageView();
                            Image image1 = new Image("/graphics/pieces/" + current_color + "_student.png");
                            image_view1.setImage(image1);
                            int dimxi = (int)(0.1 * image1.getWidth());
                            int dimyi = (int)(0.1 * image1.getHeight());
                            image_view1 = resizeImageView(image_view1, dimxi, dimyi);
                            image_view1.setX(students_centers.get(position).getFirst() - image_view1.getFitWidth() / 2);
                            image_view1.setY(students_centers.get(position).getSecond() - image_view1.getFitHeight() / 2);
                            Text coin_number = new Text(String.valueOf(value));
                            coin_number.setX(students_centers.get(position).getFirst());
                            coin_number.setY(students_centers.get(position).getSecond());
                            islandsPane.getChildren().add(image_view1);
                            islandsPane.getChildren().add(coin_number);
                            //:FIXME seems that the link passed at towercolor is'nt right
                            /*String towerColor = String.valueOf(playerTowerColorMap.get(islands.get(i).getOwner())).toLowerCase();
                            ImageView tower_image = new ImageView("graphics/pieces/towers/tower_" + towerColor + ".png");
                            tower_image.setX(islands_centers.get(i).getFirst() - tower_image.getFitWidth() / 2);
                            tower_image.setY(islands_centers.get(i).getSecond() - tower_image.getFitHeight() / 2);
                            islandsPane.getChildren().add(tower_image);*/
                            position++;
                        }
                    }
                    break;
            }
        }
    }

    private void setupCloud() {
        if(view.getGameHandler().getModel().getPlayers().size() == 2){
            cloud2.setVisible(false);
        }
    }

    private void setupCharacters(){
        Game model = view.getGameHandler().getModel();

        ArrayList<Character> characters = model.getCharacters();

        int count = 0;
        for(Character character : characters){
            // setting the image
            int id = count;
            ImageView characterImage = (ImageView)((Pane)charactersGrid.getChildren().get(count)).getChildren().get(0);
            characterImage.setImage(new Image("graphics/characters/"+character.getClass().getSimpleName()+".jpg"));
            characterImage.setOnMouseClicked(mouseEvent -> showCharacterInfo(id));

            // setting coin number
            Text coinText = (Text)((StackPane)((Pane)charactersGrid.getChildren().get(count)).getChildren().get(1)).getChildren().get(1);
            coinText.setText(String.valueOf(character.getCost()));

            VBox characterStuffSmall = (VBox)((Pane)charactersGrid.getChildren().get(count)).getChildren().get(2);
            characterStuffSmall.setSpacing(3);

            // setting students (if any)
            if(character.getStudents() != null){
                int totalStudents = character.getStudents().count();

                for(Color studentColor : character.getStudents().keySet()) {
                    int numOfStudents = character.getStudents().get(studentColor);

                    for(int i=0; i<numOfStudents; i++){
                        int size = 112 / totalStudents; // FIXME: this probably creates funny stuff with few students

                        ImageView studentImage = new ImageView("graphics/pieces/"+studentColor.toString().toLowerCase()+"_student.png");
                        studentImage = resizeImageView(studentImage, size, size);

                        characterStuffSmall.getChildren().add(studentImage);
                    }
                }
            }

            // setting noEntryTiles (if any)
            for(int i=0; i<character.getNoEntryTiles(); i++){
                ImageView tileImage = new ImageView("graphics/other/no_entry_tile.png");
                tileImage = resizeImageView(tileImage, 25, 25);

                characterStuffSmall.getChildren().add(tileImage);
            }

            count++;
        }
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
        if(gameHandler.getCurrentState() == GameState.ENDING){
            action_text = "The end";
            prepareEnding();
        } else if (gameHandler.getModel().getCurrentPlayer().getUsername().equals(GUI.getUsername())) { // if it is your turn

            if(!gameHandler.isActionCompleted()){
                switch (gameHandler.getCurrentState()) {
                    case PLAY_ASSISTANT -> {
                        action_text = "Play an assistant";
                        preparePlayAssistant();
                        // FIXME: I don't like this
                    }
                    case CHOOSE_CLOUD -> {
                        action_text = "Choose a cloud";
                        prepareChooseCloud();
                    }
                    case MOVE_MOTHER_NATURE -> {
                        action_text = "Move mother nature";
                        prepareMotherNature();
                    }
                    case MOVE_STUDENT -> {
                        action_text = "Move a student (" + gameHandler.getStudentMoves() + " left)";
                        prepareMoveStudent();
                    }
                    case ACTIVATE_CHARACTER -> {
                        action_text = "Activate a character";
                        prepareActivateCharacter();
                    }
                }
            } else {
                action_text = "Pass";
            }
        }
        actionLabel.setText(action_text);
    }

    private void prepareEnding() {
        if(view.getGameHandler().getModel().winner().getUsername().equals(GUI.getUsername())) {
            endingScreen.setImage(new Image("graphics/other/victory.png"));
        } else {
            endingScreen.setImage(new Image("graphics/other/defeat.png"));
        }
        endingScreen.setOnMouseClicked(mouseEvent -> {
            GUI.getClientSocket().send(new EndingMessage());
        });
        endingScreen.setVisible(true);
    }

    private void prepareActivateCharacter() {

    }

    private void prepareMoveStudent() {
        Game game = view.getGameHandler().getModel();
        ArrayList<Island> islands = game.getIslands();
        for(int i=0; i<islands.size(); i++){
            //TODO: update onclick move student
        }

        for(Node node : entranceGrid.getChildren()) {
            ImageView imageView = (ImageView) node;

            imageView.setOnMouseClicked(mouseEvent -> {
                if(imageView != selectedEntrance) {
                    if(selectedEntrance != null) {
                        selectedEntrance.setEffect(null);
                    }
                    selectedEntrance = imageView;
                    imageView.setEffect(new ColorAdjust(0.0, 0.0, 0.5, 0.0));
                } else {
                    selectedEntrance = null;
                    imageView.setEffect(null);
                }
            });
        }

        List.of(greenDining, cyanDining, redDining, magentaDining, yellowDining)
                .forEach(d -> {
                    d.setOnMouseClicked(mouseEvent -> {
                        if(selectedEntrance != null){
                            System.out.println(selectedEntrance.getImage().getUrl());
                            String[] sname = selectedEntrance.getImage().getUrl().split("[^a-zA-Z]");
                            Color color = Color.parseColor(sname[sname.length-3]);
                            System.out.println(color + " " + sname[sname.length-3]);
                            GUI.getClientSocket().send(new MoveStudentMessage(color));
                        } else {
                            updateErrorMessage("You must select a student first");
                        }
                    });
                });
    }

    private void prepareMotherNature() {
        // TODO: set the position of mother nature
        // mothernature.setX();
        Game game = view.getGameHandler().getModel();
        ArrayList<Island> islands = game.getIslands();
        for(int i=0; i<islands.size(); i++){
            //TODO: update onclick mother nature
        }
    }

    private void prepareChooseCloud() {
        //TODO: add students
        List<ImageView> clouds = List.of(cloud0, cloud1, cloud2);
        for(int i=0; i<3; i++){
            ChooseCloudMessage cloudMessage = new ChooseCloudMessage(i);
            if(checkMessage(cloudMessage, view.getGameHandler().getModel().usernameToPlayer(GUI.getUsername())) == StatusCode.OK){
                clouds.get(i).setOnMouseClicked(mouseEvent -> {
                    GUI.getClientSocket().send(cloudMessage);
                });
            } else {
                clouds.get(i).setOnMouseClicked(null);
                clouds.get(i).setEffect(new ColorAdjust(0.0, 0.0, -0.5, 0.0));
            }
        }
    }

    private void setupState(){
        String currentPlayerUsername = view.getGameHandler().getModel().getCurrentPlayer().getUsername();

        turnLabel.setText("Turn: " + currentPlayerUsername);
        usernameLabel.setText(thisPlayer.getUsername());
    }

    private void setupErrorMessage(){
        if(view.getErrorMessage() != null){
            updateErrorMessage(view.getErrorMessage());
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
            } else { // it is not in your hand
                imageView.setEffect(new ColorAdjust(0.0, 0.0, -0.75, 0.0));
                imageView.setOnMouseEntered(null);
                imageView.setOnMouseExited(null);
            }
        }
    }

    private void preparePlayAssistant() {
        for(Assistant assistant : Assistant.getAssistants(thisPlayer.getWizard())){
            ImageView imageView = assistantCards.get(assistant.getPower() - 1);
            if(checkMessage(new PlayAssistantMessage(thisPlayer.getPlayerHand().indexOf(assistant)), thisPlayer) != StatusCode.OK) {
                if(imageView.getEffect() == null) imageView.setEffect(new ColorAdjust(0.0, 0.0, -0.5, 0.0));
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
                studentImage.setCursor(Cursor.HAND);
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
            professorImage.setCursor(Cursor.HAND);

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
            towerImage.setCursor(Cursor.HAND);
            towersGrid.add(towerImage, count%2, count/2);

            count++;
        }

        // coins
        Text coinNumber = new Text(String.valueOf(coins));
        coinNumber.getStyleClass().add("coinNumber");
        ImageView coinsImage = new ImageView("graphics/other/coin.png");
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
            studentImage.setCursor(Cursor.HAND);

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

    private void showCharacterInfo(int id){
        Character character = view.getGameHandler().getModel().getCharacters().get(id);

        characterPane.setVisible(true);
        mainPane.setEffect(new ColorAdjust(0.0, 0.0, -0.5, 0.0));

        String characterName = character.getClass().getSimpleName();
        String description = character.getDescription();

        characterImage.setImage(new Image("graphics/characters/" + characterName + ".jpg"));
        characterCostText.setText(String.valueOf(character.getCost()));

        characterStuff.setSpacing(5);
        characterStuff.getChildren().clear();

        // setting students (if any)
        if(character.getStudents() != null){
            for(Color studentColor : character.getStudents().keySet()) {
                int numOfStudents = character.getStudents().get(studentColor);

                for(int i=0; i<numOfStudents; i++){
                    ImageView studentImage = new ImageView("graphics/pieces/"+studentColor.toString().toLowerCase()+"_student.png");
                    studentImage = resizeImageView(studentImage, 60, 60);

                    characterStuff.getChildren().add(studentImage);
                }
            }
        }

        // setting noEntryTiles (if any)
        for(int i=0; i<character.getNoEntryTiles(); i++){
            ImageView tileImage = new ImageView("graphics/other/no_entry_tile.png");
            tileImage = resizeImageView(tileImage, 60, 60);

            characterStuff.getChildren().add(tileImage);
        }

        characterNameLabel.setText(characterName);
        characterDescriptionLabel.setText(description);
        if(checkMessage(new SelectedCharacterMessage(id), thisPlayer) == StatusCode.OK){
            activateCharacterButton.setOnMouseClicked(mouseEvent -> {
                GUI.getClientSocket().send(new SelectedCharacterMessage(id));
            });
        } else {
            activateCharacterButton.setDisable(true);
        }

        closeCharacterPaneButton.setOnMouseClicked(mouseEvent -> closeCharacterInfo());
    }

    private void closeCharacterInfo(){
        characterPane.setVisible(false);
        mainPane.setEffect(null);
        GUI.setSelectingCharacter(null);
    }

    private void updateErrorMessage(String s){
        errorMsg.setText(s);
        errorMsg.setVisible(true);
    }

    public void addGlowEffect(MouseEvent event){
        ((ImageView)event.getTarget()).setEffect(new Glow(1));
    }

    public void removeGlowEffect(MouseEvent event){
        ((ImageView)event.getTarget()).setEffect(null);
    }
}
