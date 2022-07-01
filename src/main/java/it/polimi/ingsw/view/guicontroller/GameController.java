package it.polimi.ingsw.view.guicontroller;

import it.polimi.ingsw.controller.Game;
import it.polimi.ingsw.controller.GameHandler;
import it.polimi.ingsw.controller.GameState;
import it.polimi.ingsw.controller.LobbyPlayer;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.board.*;
import it.polimi.ingsw.model.characters.Character;
import it.polimi.ingsw.model.characters.Colorblind;
import it.polimi.ingsw.model.characters.Demolisher;
import it.polimi.ingsw.network.NetworkManager;
import it.polimi.ingsw.network.messages.*;
import it.polimi.ingsw.utils.DeepCopy;
import it.polimi.ingsw.utils.Pair;
import it.polimi.ingsw.view.GUI;
import it.polimi.ingsw.view.viewcontent.ViewContent;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.Glow;
import javafx.scene.effect.SepiaTone;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * It manages all the components of the game in the GUI
 */
public class GameController implements GUIController {
    protected static final String PIECE_CLASS = "piece";
    protected  static final String BORDERED_TEXT = "borderedText";

    @FXML private Pane mainPane;
    @FXML private Pane islandsPane;

    @FXML private GridPane playOrderGrid;
    @FXML private Text bagCapacityText;

    @FXML private GridPane entranceGrid;
    @FXML private GridPane greenDining, redDining, yellowDining, magentaDining, cyanDining;
    @FXML private GridPane professorsGrid;
    @FXML private GridPane towersGrid;
    @FXML private GridPane schoolboardButtonsGrid;

    @FXML private Label turnLabel, actionLabel, usernameLabel;
    @FXML private ImageView nextTurnButton;
    @FXML private Label errorMsg;
    @FXML private Pane errorPane;
    @FXML private Pane assistantsPane;

    // expert mode components
    @FXML private StackPane coinsPane;
    @FXML private GridPane charactersGrid;
    @FXML private Pane characterPane;

    @FXML private ImageView characterImage;
    @FXML private Label characterNameLabel, characterDescriptionLabel;
    @FXML private Text characterCostText;
    @FXML private VBox characterStuff;
    @FXML private Button activateCharacterButton;
    @FXML private ImageView closeCharacterPaneButton;

    @FXML private Pane colorSelectionPane;
    @FXML private ImageView chooseRed, chooseYellow, chooseGreen, chooseCyan, chooseMagenta;

    @FXML private ImageView endingScreen;

    private ViewContent view;
    private Player thisPlayer;
    private String schoolboardPlayerUsername;
    private Map<Player, TowerColor> playerTowerColorMap;
    private ImageView selectedEntrance;
    private List<Pane> cloudPanes;

    private ImageView selectedStudentCard;
    private ArrayList<ImageView> selectedOnCard;
    private ArrayList<ImageView> selectedOnEntrance;
    private ArrayList<ImageView> selectedOnDining;

    private ArrayList<Pane> islandPanes;
    private Map<Color, ImageView> chooseColor;
    private HashMap<Integer, VBox> characterStuffSmallMap;
    private ArrayList<Consumer<String>> onFillSchoolboard;

    /**
     * It initializes oll the elements
     */
    @Override
    public void setup() {
        this.view = GUI.getView();
        System.out.println(view);

        GameHandler gameHandler = view.getGameHandler();
        Game game = gameHandler.getModel();

        cloudPanes = new ArrayList<>();
        chooseColor = Map.of(Color.RED, chooseRed, Color.YELLOW, chooseYellow, Color.GREEN, chooseGreen, Color.CYAN, chooseCyan, Color.MAGENTA, chooseMagenta);
        playerTowerColorMap = new HashMap<>();
        selectedOnDining = new ArrayList<>();
        selectedOnEntrance = new ArrayList<>();
        selectedOnCard = new ArrayList<>();
        thisPlayer = game.usernameToPlayer(GUI.getUsername());
        islandPanes = new ArrayList<>();
        characterStuffSmallMap = new HashMap<>();
        onFillSchoolboard = new ArrayList<>();

        // setting up player - towerColor association
        for (int i = 0; i < view.getGameHandler().getModel().getPlayers().size(); i++)
            playerTowerColorMap.put(view.getGameHandler().getModel().getPlayers().get(i), TowerColor.values()[i]);


        resetBoard();
        setupState();
        setupIslands();
        setupSchoolboard();
        setupAssistants();
        setupErrorMessage();
        setupClouds();
        setupMotherNature();
        setupPlayOrder();

        // displaying bag capacity
        bagCapacityText.setText(String.valueOf(game.getBag().capacity()));

        // expert mode
        if (view.getGameHandler().getModel().getExpertMode()) {
            setupNextTurnButton();
            setupCharacters();
            showExpertModeComponents();
        } else {
            hideExpertModeComponents();
        }

        if (GUI.getSelectingCharacter() != null)
            showCharacterInfo(GUI.getSelectingCharacter());

        setupActions();

        // adding the listeners after building the schoolboard (the first time)
        for (Consumer<String> f : onFillSchoolboard) {
            f.accept(schoolboardPlayerUsername);
        }
    }

    /**
     * It initializes the schoolboard
     */
    private void resetBoard() {
        // reset node to their initial value
        nextTurnButton.setEffect(null);
        islandsPane.getChildren().clear();
        List.of(greenDining, cyanDining, redDining, magentaDining, yellowDining).forEach(e -> e.setEffect(null));
        List.of(greenDining, cyanDining, redDining, magentaDining, yellowDining).forEach(e -> e.setOnMouseClicked(null));
        selectedStudentCard = null;
        selectedEntrance = null;

        if(schoolboardPlayerUsername == null)
            schoolboardPlayerUsername = thisPlayer.getUsername();
    }

    /**
     * It generates and initializes the islands
     */
    private void setupIslands() {
        List<Island> islands = view.getGameHandler().getModel().getIslands();
        List<Node> islandNodes = new ArrayList<>();

        // getting the active character for later usage
        Optional<Character> activeCharacter = view.getGameHandler().getModel().getActiveCharacter();

        for (int i = 0; i < islands.size(); i++) {
            Island island = islands.get(i);

            // every island has its own pane
            Pane islandPane = new Pane();
            islandPane.setCursor(Cursor.HAND);
            List<Node> islandTopping = new ArrayList<>();
            int islandSize = island.getNumIslands();

            // adding island image
            ImageView islandImage = new ImageView();
            Image image = new Image("/graphics/islands/" + i % 3 + ".png");
            islandImage.setImage(image);

            int islandImageSize = 130;
            double scaleFactor = (double) islandSize * islandImageSize / 5 / 0.4 - (double) islandImageSize / 5 / 0.4;
            int dimX = (int) (islandImageSize + scaleFactor); // FIXME: choose a better scale factor
            int dimY = (int) (islandImageSize + scaleFactor);
            islandImage = resizeImageView(islandImage, dimX, dimY);

            islandPane.getChildren().add(islandImage);

            // adding island's elements on top
            // adding students, getting the colors of the students currently on the island
            HashMap<Color, Integer> presentStudents = new HashMap<>();
            island.getStudents().forEach((color, quantity) -> {
                if (quantity > 0)
                    presentStudents.put(color, quantity);
            });

            // presentStudents.forEach((color, quantity) -> System.out.println(color + " " + quantity));

            // adding towers
            int numTowers = island.getNumTowers();
            if (numTowers > 0) {
                StackPane towerStackPane = new StackPane();
                String towerColor = String.valueOf(playerTowerColorMap.get(island.getOwner())).toLowerCase();

                ImageView towerImage;

                // if the demolisher is active, loads the broken version of the tower image
                if(activeCharacter.isPresent() && (activeCharacter.get() instanceof Demolisher)){
                    towerImage = new ImageView("graphics/pieces/towers/tower_" + towerColor + "_broken.png");
                }else towerImage = new ImageView("graphics/pieces/towers/tower_" + towerColor + ".png");

                towerImage = resizeImageView(towerImage, 35, 35);
                towerImage.getStyleClass().add(PIECE_CLASS);

                Text towerNumber = new Text(String.valueOf(numTowers));
                towerNumber.getStyleClass().add(BORDERED_TEXT);

                towerStackPane.getChildren().add(towerImage);
                towerStackPane.getChildren().add(towerNumber);

                double x = islandPane.getBoundsInParent().getWidth() / 2 - towerStackPane.getBoundsInParent().getWidth() / 2;
                double y = islandPane.getBoundsInParent().getHeight() / 2 - towerStackPane.getBoundsInParent().getHeight() / 2;

                // if there's only one student color, the tower gets added on top of it, otherwise it is displayed in the center
                if (presentStudents.size() == 1)
                    y -= 35;

                towerStackPane.setLayoutX(x);
                towerStackPane.setLayoutY(y);
                islandPane.getChildren().add(towerStackPane);
            }

            if (presentStudents.keySet().size() > 0) {
                // creating a stackPane with image + quantity for each student color present
                for (Color studentColor : presentStudents.keySet()) {
                    int numStudents = presentStudents.get(studentColor);

                    StackPane studentStackPane = new StackPane();
                    ImageView studentImage = new ImageView("/graphics/pieces/" + studentColor.toString().toLowerCase() + "_student.png");
                    studentImage = resizeImageView(studentImage, 30, 30);
                    studentImage.getStyleClass().add(PIECE_CLASS);

                    // if colorblind character is active, make the inhibited students black and white
                    if(activeCharacter.isPresent() && (activeCharacter.get() instanceof Colorblind)){
                        if(view.getGameHandler().getModel().getGameModifiers().getInhibitColor().equals(studentColor)) {
                            studentImage.getStyleClass().remove(PIECE_CLASS);
                            addBlackWhiteEffect(studentImage);
                        }
                    }

                    Text studentNumber = new Text(String.valueOf(numStudents));
                    studentNumber.getStyleClass().add(BORDERED_TEXT);

                    studentStackPane.setPrefSize(studentImage.getFitWidth(), studentImage.getFitHeight());
                    studentStackPane.getChildren().add(studentImage);
                    studentStackPane.getChildren().add(studentNumber);

                    islandTopping.add(studentStackPane);
                }

                // adding all the pieces on top of the islandPane
                double radius;
                if(numTowers != 0) radius = islandPane.getBoundsInParent().getWidth() / 4;
                else radius = islandPane.getBoundsInParent().getWidth() / 5;

                placeNodes(islandPane, islandTopping, radius);
            }

            // adding no entry tiles over the island
            if(island.getNoEntryTiles() > 0){
                int numTiles = island.getNoEntryTiles();

                StackPane noEntryTilesPane = new StackPane();

                ImageView tileImage = new ImageView("graphics/other/no_entry_tile.png");
                tileImage = resizeImageView(tileImage, 35, 35);

                Text tileNumber = new Text(String.valueOf(numTiles));
                tileNumber.getStyleClass().add(BORDERED_TEXT);

                noEntryTilesPane.getChildren().add(tileImage);
                noEntryTilesPane.getChildren().add(tileNumber);

                double x = islandPane.getBoundsInParent().getWidth() / 2 - noEntryTilesPane.getBoundsInParent().getWidth() / 2;
                double y = islandPane.getBoundsInParent().getHeight() / 2 - noEntryTilesPane.getBoundsInParent().getHeight() / 2;

                noEntryTilesPane.setLayoutX(x);
                noEntryTilesPane.setLayoutY(y + 60);
                islandPane.getChildren().add(noEntryTilesPane);
            }

            // adding the updated islandPane to the list
            islandNodes.add(islandPane);
            islandPanes.add(islandPane);
        }

        // making a regular polygon with the provided nodes in the given container
        double radius = islandsPane.getBoundsInParent().getWidth() * 2 / 3 / 2;
        placeNodes(islandsPane, islandNodes, radius);
    }

    /**
     * It generates and initializes the islands
     */
    private void setupClouds() {
        Game model = view.getGameHandler().getModel();
        for (Students cloud : model.getClouds()) {
            Pane cloudPane = new Pane();
            cloudPane.setCursor(Cursor.HAND);
            ImageView cloudImage = new ImageView("graphics/islands/cloud.png");
            cloudImage = resizeImageView(cloudImage, 100, 100);

            cloudPane.getChildren().add(cloudImage);
            cloudPanes.add(cloudPane);

            if (cloud.count() == 0){
                addLightEffect(cloudImage);
            } else {
                List<Node> cloudTopping = new ArrayList<>();
                for (Color key : cloud.keySet()) {
                    if (cloud.get(key) > 0) {
                        StackPane cloudStackPane = new StackPane();

                        ImageView studentImage = new ImageView("graphics/pieces/" + key.toString().toLowerCase() + "_student.png");
                        studentImage = resizeImageView(studentImage, 30, 30);
                        studentImage.getStyleClass().add(PIECE_CLASS);

                        Text quantity = new Text(String.valueOf(cloud.get(key)));
                        quantity.getStyleClass().add(BORDERED_TEXT);

                        cloudStackPane.getChildren().add(studentImage);
                        cloudStackPane.getChildren().add(quantity);

                        cloudTopping.add(cloudStackPane);
                    }
                }

                double radius = cloudPane.getBoundsInParent().getWidth() / 4;
                placeNodes(cloudPane, cloudTopping, radius);

            }
        }

        List<Node> cloudNodes = new ArrayList<>(cloudPanes);
        double radius = islandsPane.getBoundsInParent().getWidth() * 1 / 5 / 2;
        placeNodes(islandsPane, cloudNodes, radius);
    }

    /**
     * It generates and initializes the character cards
     */
    private void setupCharacters() {
        Game model = view.getGameHandler().getModel();

        ArrayList<Character> characters = model.getCharacters();

        int count = 0;
        for (Character character : characters) {
            // setting the image
            int id = count;
            ImageView characterImage = (ImageView) ((Pane) charactersGrid.getChildren().get(count)).getChildren().get(0);
            characterImage.setImage(new Image("graphics/characters/" + character.getClass().getSimpleName() + ".jpg"));
            characterImage.setOnMouseClicked(mouseEvent -> showCharacterInfo(id));

            // if the character is selected, make it glow
            if(character.equals(view.getGameHandler().getSelectedCharacter()))
                characterImage.setEffect(new SepiaTone(0.65));
            else characterImage.setEffect(null);

            // setting coin number
            Text coinText = (Text) ((StackPane) ((Pane) charactersGrid.getChildren().get(count)).getChildren().get(1)).getChildren().get(1);
            coinText.setText(String.valueOf(character.getCost()));

            VBox characterStuffSmall = (VBox) ((Pane) charactersGrid.getChildren().get(count)).getChildren().get(2);
            characterStuffSmall.getChildren().clear();
            characterStuffSmallMap.put(id, characterStuffSmall);
            characterStuffSmall.setSpacing(3);

            // setting students (if any)
            if (character.getStudents() != null) {
                int totalStudents = character.getStudents().count();

                for (Color studentColor : character.getStudents().keySet()) {
                    int numOfStudents = character.getStudents().get(studentColor);

                    for (int i = 0; i < numOfStudents; i++) {
                        int size = Math.min(112 / totalStudents, 30);

                        ImageView studentImage = new ImageView("graphics/pieces/" + studentColor.toString().toLowerCase() + "_student.png");
                        studentImage = resizeImageView(studentImage, size, size);
                        studentImage.setCursor(Cursor.HAND);

                        characterStuffSmall.getChildren().add(studentImage);
                    }
                }
            }

            // setting noEntryTiles (if any)
            for (int i = 0; i < character.getNoEntryTiles(); i++) {
                ImageView tileImage = new ImageView("graphics/other/no_entry_tile.png");
                tileImage = resizeImageView(tileImage, 25, 25);

                characterStuffSmall.getChildren().add(tileImage);
            }

            count++;
        }
    }

    /**
     * It generates and initializes the labels with the order of the players
     */
    private void setupPlayOrder() {
        Queue<Player> playOrder = view.getGameHandler().getModel().getPlayOrder();

        for (Player player : playOrder)
            System.out.println(player.getUsername());

        playOrderGrid.getChildren().clear();
        // fill the grid with the usernames
        int count = 0;
        for (Player player : playOrder) {
            Label playerUsername = new Label(player.getUsername());
            playerUsername.getStyleClass().clear();
            playerUsername.getStyleClass().add("username");
            playOrderGrid.addRow(count, playerUsername);

            count++;
        }
    }

    /**
     * It is the initialization part that is dependent on the current action state.
     * It calls the specific method depending on the current {@link GameState}.
     */
    private void setupActions() {
        GameHandler gameHandler = view.getGameHandler();

        // enable and create objects depending on the current state
        if (gameHandler.getCurrentState() == GameState.ENDING) {
            prepareEnding();
        } else if (gameHandler.getModel().getCurrentPlayer().getUsername().equals(GUI.getUsername())) { // if it is your turn

            if (!gameHandler.isActionCompleted()) {
                switch (gameHandler.getCurrentState()) {
                    case CHOOSE_CLOUD -> prepareChooseCloud();
                    case MOVE_MOTHER_NATURE -> prepareMotherNature();
                    case MOVE_STUDENT -> prepareMoveStudent();
                    case ACTIVATE_CHARACTER -> prepareActivateCharacter();
                }
            }
        }
    }

    /**
     * It generates and initializes the mother nature image
     */
    private void setupMotherNature() {
        // the position of the mother nature is the convex interpolation between the center and the island (in which mother nature is currently placed)
        final double lambda = 0.3; // convex interpolation factor. It must be in [0, 1]
        int position = view.getGameHandler().getModel().findMotherNaturePosition();
        Pane island = islandPanes.get(position);
        ImageView motherNature = new ImageView(new Image("/graphics/pieces/mothernature/mother_nature.png"));
        motherNature = resizeImageView(motherNature, 32, 32);
        island.getChildren().add(motherNature);

        double centerX = -island.getParent().getBoundsInParent().getCenterX() + islandsPane.getBoundsInParent().getWidth() / 2;
        double centerY = -island.getParent().getBoundsInParent().getCenterY() + islandsPane.getBoundsInParent().getHeight() / 2;
        double islandX = 0;
        double islandY = 0;

        /*
            System.out.println("center: " + centerX + " " + centerY);
            System.out.println("island:" + islandX + " " + islandY);
         */

        motherNature.setLayoutX(lambda * centerX + (1-lambda) * islandX - motherNature.getBoundsInParent().getWidth()  / 2 + island.getBoundsInParent().getWidth() / 2);
        motherNature.setLayoutY(lambda * centerY + (1-lambda) * islandY - motherNature.getBoundsInParent().getHeight() / 2 + island.getBoundsInParent().getHeight() / 2);
    }

    /**
     * It generates and initializes the end of the game logos and images
     */
    private void prepareEnding() {
        if (view.getGameHandler().getModel().winner().getUsername().equals(GUI.getUsername())) {
            endingScreen.setImage(new Image("graphics/other/victory.png"));
        } else {
            endingScreen.setImage(new Image("graphics/other/defeat.png"));
        }
        mainPane.setDisable(true);
        mainPane.setEffect(new ColorAdjust(0.0, 0.0, -0.5, 0.0));

        endingScreen.setOnMouseClicked(mouseEvent -> GUI.getClientSocket().send(new EndingMessage()));
        endingScreen.setVisible(true);
    }

    /**
     * It generates and initializes the elements when the {@link GameState} is ACTIVATE_CHARACTER.
     */
    private void prepareActivateCharacter() {
        Game game = view.getGameHandler().getModel();
        Character character = view.getGameHandler().getSelectedCharacter();
        if (character == null) {
            updateErrorMessage("Invalid character"); // it should be unreachable
            return;
        }
        // get the position of the selected character
        int position = 0;

        while (!game.getCharacters().get(position).equals(character)) position++;

        switch (character.require()) {
            case ISLAND -> {
                // it wants a single island
                for (int i = 0; i < islandPanes.size(); i++) {
                    int finalI = i;
                    Pane islandPane = islandPanes.get(i);
                    islandPane.setOnMouseClicked(mouseEvent -> {
                        PlayerChoicesSerializable playerChoicesSerializable = new PlayerChoicesSerializable();
                        playerChoicesSerializable.setIsland(finalI);
                        GUI.getClientSocket().send(new ActivateCharacterMessage(playerChoicesSerializable));
                    });
                    addLightEffect(islandPane);
                }
            }

            case STUDENT_COLOR -> {
                // it wants a color
                colorSelectionPane.setVisible(true);
                for (var entry : chooseColor.entrySet()) {
                    entry.getValue().setOnMouseClicked(mouseEvent -> {
                        PlayerChoicesSerializable playerChoicesSerializable = new PlayerChoicesSerializable();
                        playerChoicesSerializable.setStudent(entry.getKey());
                        GUI.getClientSocket().send(new ActivateCharacterMessage(playerChoicesSerializable));

                        mainPane.setDisable(false);
                        mainPane.setEffect(null);
                        colorSelectionPane.setVisible(false);
                    });
                }
                mainPane.setDisable(true);
                mainPane.setEffect(new ColorAdjust(0.0, 0.0, -0.5, 0.0));
            }

            case CARD_STUDENT -> {
                // it wants a color available in the card
                VBox characterStuffSmall = characterStuffSmallMap.get(position);
                characterStuffSmall.getChildren().forEach(node -> {
                    ImageView imageView = (ImageView) node;
                    imageView.setOnMouseClicked(e -> {
                        // get the color of the student
                        Color color = imageViewToColor(imageView);

                        // send the message
                        PlayerChoicesSerializable playerChoicesSerializable = new PlayerChoicesSerializable();
                        playerChoicesSerializable.setStudent(color);
                        GUI.getClientSocket().send(new ActivateCharacterMessage(playerChoicesSerializable));
                    });
                });
            }

            case NOTHING -> {
                // it wants nothing
                PlayerChoicesSerializable playerChoicesSerializable = new PlayerChoicesSerializable();
                GUI.getClientSocket().send(new ActivateCharacterMessage(playerChoicesSerializable));
            }
            case MOVE_CARD_ISLAND -> {
                // it wants a student from this card and an island

                // 1) select a student from this card
                VBox characterStuffSmall = characterStuffSmallMap.get(position);
                characterStuffSmall.getChildren().forEach(node -> {
                    ImageView imageView = (ImageView) node;
                    imageView.setOnMouseClicked(e -> {
                        if (selectedStudentCard == imageView) {
                            selectedStudentCard.setEffect(null);
                            selectedStudentCard = null;
                            islandPanes.forEach(island -> island.setEffect(null));
                        } else {
                            if (selectedStudentCard != null) {
                                selectedStudentCard.setEffect(null);
                            }
                            selectedStudentCard = imageView;
                            addLightEffect(selectedStudentCard);
                            islandPanes.forEach(this::addLightEffect);
                        }
                    });
                });

                // 2) select an island
                for (int i = 0; i < islandPanes.size(); i++) {
                    int finalI = i;
                    Pane islandPane = islandPanes.get(i);
                    islandPane.setOnMouseClicked(mouseEvent -> {
                        if (selectedStudentCard != null) {
                            Color color = imageViewToColor(selectedStudentCard);
                            PlayerChoicesSerializable playerChoicesSerializable = new PlayerChoicesSerializable();
                            playerChoicesSerializable.setIsland(finalI);
                            playerChoicesSerializable.setStudent(color);
                            GUI.getClientSocket().send(new ActivateCharacterMessage(playerChoicesSerializable));
                        } else {
                            updateErrorMessage("You must select a student from the character first");
                        }
                    });
                }
            }

            case SWAP_CARD_ENTRANCE -> {
                // it wants up to 3 students from the entrance and up to 3 students from the entrance

                // method for the button dark/light effect
                Runnable updateButton = () -> {
                    if (selectedOnCard.size() == selectedOnEntrance.size()) {
                        nextTurnButton.setEffect(null);
                    } else {
                        nextTurnButton.setEffect(new ColorAdjust(0.0, 0.0, -0.5, 0.0));
                    }
                };

                int finalPosition = position;
                onFillSchoolboard.add(player -> {
                    // setup global variables and objects
                    selectedOnEntrance.clear();
                    updateButton.run();

                    // student can be selected only if this is my schoolboard!
                    if (!player.equals(thisPlayer.getUsername())) return;


                    // 1) select up to 3 students from this card
                    VBox characterStuffSmall = characterStuffSmallMap.get(finalPosition);
                    selectStudent(updateButton, characterStuffSmall.getChildren(), selectedOnCard);

                    // 2) select up to 3 students from the entrance
                    selectStudent(updateButton, entranceGrid.getChildren(), selectedOnEntrance);

                    // 3) confirm selection
                    confirmSelection(selectedOnCard);
                });
            }

            case SWAP_DINING_ENTRANCE -> {
                // it wants up to 2 students from the entrance and up to 2 students from the entrance

                // method for the button dark/light effect
                Runnable updateButton = () -> {
                    if (selectedOnDining.size() == selectedOnEntrance.size()) {
                        nextTurnButton.setEffect(null);
                    } else {
                        nextTurnButton.setEffect(new ColorAdjust(0.0, 0.0, -0.5, 0.0));
                    }
                };

                onFillSchoolboard.add(player -> {
                    // setup global variables
                    selectedOnEntrance.clear();
                    selectedOnDining.clear();
                    updateButton.run();

                    // student can be selected only if this is my schoolboard!
                    if (!player.equals(thisPlayer.getUsername())) return;

                    // 1) select up to 2 students from this card
                    List.of(greenDining, magentaDining, cyanDining, redDining, yellowDining).forEach(gridPane ->
                        selectUpToTwoStudents(updateButton, gridPane, selectedOnDining)
                    );

                    // 2) select up to 2 students from the entrance
                    selectUpToTwoStudents(updateButton, entranceGrid, selectedOnEntrance);

                    // 3) confirm selection
                    confirmSelection(selectedOnDining);
                });
            }
        }
    }

    /**
     * It adds the onclick effect used to manage the student selection and glowing effect.
     * @param updateButton it updates the button state (called at the end of the function)
     * @param children list of object in which the on click effect must be added
     * @param selected students selected
     */
    private void selectStudent(Runnable updateButton, ObservableList<Node> children, ArrayList<ImageView> selected) {
        children.forEach(node -> {
            ImageView imageView = (ImageView) node;
            imageView.setOnMouseClicked(e -> {
                if (selected.contains(imageView)) { // deselect a student
                    selected.remove(imageView);
                    imageView.setEffect(null);
                } else {
                    selected.add(imageView);
                    addLightEffect(imageView);

                    if (selected.size() > 3) { // remove the first, if there are more than 3 cards
                        selected.get(0).setEffect(null);
                        selected.remove(0);
                    }
                }
                updateButton.run();
            });
        });
    }

    /**
     * It adds the onclick effect used to manage the student selection and glowing effect.
     * @param updateButton it updates the button state (called at the end of the function)
     * @param children list of object in which the on click effect must be added
     * @param selected selected student
     */
    private void selectUpToTwoStudents(Runnable updateButton, GridPane children, ArrayList<ImageView> selected) {
        children.getChildren().forEach(node -> {
            ImageView imageView = (ImageView) node;

            imageView.setOnMouseClicked(e -> {
                if (selected.contains(imageView)) { // deselect a student
                    selected.remove(imageView);
                    imageView.setEffect(null);
                } else {
                    selected.add(imageView);
                    addLightEffect(imageView);
                    if (selected.size() > 2) { // remove the first, if there are more than 3 cards
                        selected.get(0).setEffect(null);
                        selected.remove(0);
                    }
                }

                updateButton.run();
            });

        });
    }

    /**
     * It adds the OnMouseClicked event to the nextTurnButton object to confirm the selected students
     * The selected students are from the selected parameter and the selectedOnEntrance one (the students selected from the entrance)
     * @param selected selected students
     */
    private void confirmSelection(ArrayList<ImageView> selected) {
        nextTurnButton.setOnMouseClicked(mouseEvent -> {
            if (selected.size() == selectedOnEntrance.size()) {
                PlayerChoicesSerializable playerChoicesSerializable = new PlayerChoicesSerializable();
                for (int i = 0; i < selected.size(); i++) {
                    playerChoicesSerializable.setStudent(imageViewToColor(selectedOnEntrance.get(i)));
                    playerChoicesSerializable.setStudent(imageViewToColor(selected.get(i)));
                }
                GUI.getClientSocket().send(new ActivateCharacterMessage(playerChoicesSerializable));
            } else {
                updateErrorMessage("You must select the same number of students");
            }
        });
    }

    /**
     * It generates and initializes the objects used in the {@link GameState} MOVE_STUDENT
     */
    private void prepareMoveStudent() {
        selectedEntrance = null;
        for (int i = 0; i < islandPanes.size(); i++) {
            int finalI = i;
            islandPanes.get(i).setOnMouseClicked(mouseEvent -> {
                if (selectedEntrance != null) {
                    Color color = imageViewToColor(selectedEntrance);
                    GUI.getClientSocket().send(new MoveStudentMessage(color, finalI));
                    selectedEntrance = null;
                } else {
                    updateErrorMessage("You must select a student first");
                }
            });
        }

        onFillSchoolboard.add(player -> {
            // reset the current selection
            selectedEntrance = null;
            islandPanes.forEach(e -> e.setEffect(null));
            List.of(greenDining, cyanDining, redDining, magentaDining, yellowDining).forEach(e -> e.setOnMouseClicked(null));
            List.of(greenDining, cyanDining, redDining, magentaDining, yellowDining).forEach(e -> e.setEffect(null));

            // student can be selected only if this is my schoolboard!
            if (!player.equals(thisPlayer.getUsername())) return;

            for (Node node : entranceGrid.getChildren()) {
                ImageView imageView = (ImageView) node;

                imageView.setOnMouseClicked(mouseEvent -> {
                    if (imageView != selectedEntrance) { // select a student
                        if (selectedEntrance != null) {
                            selectedEntrance.setEffect(null);
                        }
                        selectedEntrance = imageView;
                        addLightEffect(imageView);

                        // highlight valid action
                        islandPanes.forEach(e -> e.setEffect(new ColorAdjust(0.0, 0.0, 0.5, 0.0)));
                        List.of(greenDining, cyanDining, redDining, magentaDining, yellowDining)
                                .forEach(e -> e.setEffect(new ColorAdjust(0.0, 0.0, 0.5, 0.0)));

                    } else { // when clicked on the selected image -> deselect it
                        selectedEntrance = null;

                        // disable highlighting
                        imageView.setEffect(null);
                        islandPanes.forEach(e -> e.setEffect(null));
                        List.of(greenDining, cyanDining, redDining, magentaDining, yellowDining)
                                .forEach(e -> e.setEffect(null));
                    }
                });
            }

            List.of(greenDining, cyanDining, redDining, magentaDining, yellowDining)
                    .forEach(d ->
                            d.setOnMouseClicked(mouseEvent -> {
                                if (selectedEntrance != null) {
                                    Color color = imageViewToColor(selectedEntrance);
                                    GUI.getClientSocket().send(new MoveStudentMessage(color));
                                } else {
                                    updateErrorMessage("You must select a student first");
                                }
                            })
                    );
        });
    }

    /**
     * It generates and initializes the {@link GameState} MOVE_MOTHER_NATURE
     */
    private void prepareMotherNature() {
        for (int i = 0; i < islandPanes.size(); i++) {
            Pane islandPane = islandPanes.get(i);
            System.out.println(i + ": " + checkMessage(new MoveMotherNatureMessage(i), thisPlayer));
            if (checkMessage(new MoveMotherNatureMessage(i), thisPlayer) == StatusCode.OK) {
                int finalI = i;

                islandPane.setOnMouseClicked(mouseEvent ->
                    GUI.getClientSocket().send(new MoveMotherNatureMessage(finalI))
                );
                addLightEffect(islandPane);
            } else {
                islandPane.setEffect(new ColorAdjust(0.0, 0.0, -0.5, 0.0));
            }
        }
    }

    /**
     * It generates and initializes the {@link GameState} CHOOSE_CLOUD
     */
    private void prepareChooseCloud() {
        for (int i = 0; i < cloudPanes.size(); i++) {
            Pane cloudPane = cloudPanes.get(i);
            ChooseCloudMessage cloudMessage = new ChooseCloudMessage(i);

            if (checkMessage(cloudMessage, view.getGameHandler().getModel().usernameToPlayer(GUI.getUsername())) == StatusCode.OK) {
                cloudPane.setOnMouseClicked(mouseEvent ->
                    GUI.getClientSocket().send(cloudMessage)
                );
            } else {
                cloudPane.setOnMouseClicked(null);
                cloudPane.setEffect(new ColorAdjust(0.0, 0.0, -0.5, 0.0));
            }
        }
    }
    /**
     * It generates and initializes the name of the current player and the current action
     */
    private void setupState() {
        GameHandler gameHandler = view.getGameHandler();

        String currentPlayerUsername = gameHandler.getModel().getCurrentPlayer() != null ?
                gameHandler.getModel().getCurrentPlayer().getUsername() :
                "";

        turnLabel.setText("Turn: " + currentPlayerUsername);
        usernameLabel.setText(thisPlayer.getUsername());


        // update the action state
        String action_text = null;
        if (gameHandler.getCurrentState() == GameState.ENDING) {
            action_text = "The end";
        } else {
            if (!gameHandler.isActionCompleted()) {
                switch (gameHandler.getCurrentState()) {
                    case PLAY_ASSISTANT -> action_text = "Play an assistant";
                    case CHOOSE_CLOUD -> action_text = "Choose a cloud";
                    case MOVE_MOTHER_NATURE -> action_text = "Move mother nature";
                    case MOVE_STUDENT -> action_text = "Move a student (" + gameHandler.getStudentMoves() + " left)";
                    case ACTIVATE_CHARACTER -> action_text = "Activate a character";
                }
            } else {
                action_text = "Pass";
            }
        }
        actionLabel.setText(action_text);
    }

    /**
     * It shows the error message (if present)
     */
    private void setupErrorMessage() {
        if (view.getErrorMessage() != null) {
            updateErrorMessage(view.getErrorMessage());
        } else errorPane.setVisible(GUI.isShowingError());
    }

    /**
     * It generates and initializes the next turn button.
     * It is used to send the {@link NextStateMessage}
     */
    private void setupNextTurnButton() {
        if (checkMessage(new NextStateMessage(), thisPlayer) == StatusCode.OK) {
            nextTurnButton.setOnMouseClicked(mouseEvent -> GUI.getClientSocket().send(new NextStateMessage()));
        } else {
            nextTurnButton.setEffect(new ColorAdjust(0.0, 0.0, -0.5, 0.0));
        }
    }

    /**
     * It updates the onclick effect of the assistants
     */
    private void setupAssistants() {
        Game model = view.getGameHandler().getModel();

        ArrayList<Assistant> assistants = thisPlayer.getPlayerHand();
        Assistant currentAssistant = thisPlayer.getCurrentAssistant();
        Map<String, Assistant> playedAssistantsMap = model.getPlayers().stream()
                .filter(player -> player.getCurrentAssistant() != null)
                .collect(Collectors.toMap(LobbyPlayer::getUsername, Player::getCurrentAssistant));

        int count = 0;
        for (Assistant assistant : Assistant.getAssistants(thisPlayer.getWizard())) {
            Pane assistantPane = (Pane) assistantsPane.getChildren().get(count);
            ImageView assistantImage = (ImageView) assistantPane.getChildren().get(1);
            Label assistantOwner = (Label) assistantPane.getChildren().get(0);

            assistantOwner.setText("");
            assistantPane.setLayoutY(0);
            assistantImage.setEffect(null);

            int indexOfAssistant = thisPlayer.getPlayerHand().indexOf(assistant);

            // current assistant is up
            if(assistant.equals(currentAssistant)) {
                assistantPane.setLayoutY(-40);
                assistantImage.setOnMouseEntered(null);
                assistantImage.setOnMouseExited(null);
                // assistants played by other players are yellow
            }else if(playedAssistantsMap.containsValue(assistant) && checkMessage(new PlayAssistantMessage(indexOfAssistant), thisPlayer) == StatusCode.OK) {
                assistantImage.setEffect(new SepiaTone(1));
                assistantImage.setOnMouseEntered(null);
                assistantImage.setOnMouseExited(null);
                // already played assistants are grey
            }else if(!assistants.contains(assistant) && !assistant.equals(currentAssistant)) {
                assistantImage.setEffect(new ColorAdjust(0.0, 0.0, -0.75, 0.0));
                assistantImage.setOnMouseEntered(null);
                assistantImage.setOnMouseExited(null);
            }

            if(playedAssistantsMap.containsValue(assistant)) {
                List<String> usernames = playedAssistantsMap.keySet().stream()
                        .filter(key -> playedAssistantsMap.get(key).equals(assistant))
                        .toList();

                usernames.forEach(username -> assistantOwner.setText(assistantOwner.getText() + " " + username));
            }

            // if the player has this assistant, the listener is added
            if(assistants.contains(assistant)) {
                assistantImage.setOnMouseClicked(mouseEvent ->
                        GUI.getClientSocket().send(new PlayAssistantMessage(thisPlayer.getPlayerHand().indexOf(assistant)))
                );
            }else assistantImage.setOnMouseClicked(null);

            count++;
        }
    }

    /**
     * It hides the components only used in the expert mode (characters, coins and next turn button).
     */
    private void hideExpertModeComponents() {
        charactersGrid.setVisible(false);
        coinsPane.setVisible(false);
        nextTurnButton.setVisible(false);
    }

    /**
     * It shows the components only used in the expert mode (characters, coins and next turn button).
     */
    private void showExpertModeComponents(){
        charactersGrid.setVisible(true);
        coinsPane.setVisible(true);
        nextTurnButton.setVisible(true);
    }

    /**
     * It generates and initializes the schoolboard.
     */
    private void setupSchoolboard() {
        Player schoolboardPlayer = view.getGameHandler().getModel().usernameToPlayer(schoolboardPlayerUsername);

        fillSchoolboard(schoolboardPlayer);
        setSchoolBoardButtons(schoolboardPlayer);
    }

    /**
     * It generates and initializes the schoolboard and the students on it.
     * @param player the owner of the schoolboard
     */
    private void fillSchoolboard(Player player) {
        SchoolBoard schoolBoard = player.getSchoolBoard();
        Students entranceStudents = schoolBoard.getEntranceStudents();
        Students diningStudents = schoolBoard.getDiningStudents();
        Professors professors = schoolBoard.getProfessors();
        int numTowers = schoolBoard.getNumTowers();
        int coins = player.getCoins();

        int count = 0;
        // entrance students
        entranceGrid.getChildren().clear();
        for (Color studentColor : entranceStudents.keySet()) {
            for (int i = 0; i < entranceStudents.get(studentColor); i++) {
                ImageView studentImage = new ImageView("graphics/pieces/" + studentColor.toString().toLowerCase() + "_student.png");
                studentImage = resizeImageView(studentImage, 50, 50);
                studentImage.setCursor(Cursor.HAND);
                studentImage.getStyleClass().add(PIECE_CLASS);
                entranceGrid.add(studentImage, 1 - count % 2, 4 - count / 2);

                count++;
            }
        }

        // dining students
        for (Color studentColor : diningStudents.keySet()) {
            switch (studentColor) {
                case GREEN -> fillDining(diningStudents, studentColor, greenDining);
                case CYAN -> fillDining(diningStudents, studentColor, cyanDining);
                case RED -> fillDining(diningStudents, studentColor, redDining);
                case MAGENTA -> fillDining(diningStudents, studentColor, magentaDining);
                case YELLOW -> fillDining(diningStudents, studentColor, yellowDining);
            }
        }

        // professors
        professorsGrid.getChildren().clear();
        for (Color professorColor : professors) {
            ImageView professorImage = new ImageView("graphics/pieces/" + professorColor.toString().toLowerCase() + "_professor.png");
            professorImage = resizeImageView(professorImage, 55, 55, 90);
            professorImage.setCursor(Cursor.HAND);
            professorImage.getStyleClass().add(PIECE_CLASS);

            switch (professorColor) {
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
        for (int i = 0; i < numTowers; i++) {
            ImageView towerImage = new ImageView("graphics/pieces/towers/tower_" + towerColor + ".png");
            towerImage = resizeImageView(towerImage, 50, 50);
            towerImage.setCursor(Cursor.HAND);
            towerImage.getStyleClass().add(PIECE_CLASS);
            towersGrid.add(towerImage, count % 2, count / 2);

            count++;
        }

        // coins
        coinsPane.getChildren().remove(1);
        Text coinNumber = new Text(String.valueOf(coins));
        coinNumber.getStyleClass().add(BORDERED_TEXT);
        coinsPane.getChildren().add(coinNumber);

        // adding the listeners after building the schoolboard
        for (Consumer<String> f : onFillSchoolboard) {
            f.accept(player.getUsername());
        }
    }

    /**
     * It creates a button for each player for switching schoolboard
     * @param selectedPlayer currently selected schoolboard
     */
    private void setSchoolBoardButtons(Player selectedPlayer) {
        ArrayList<Player> players = view.getGameHandler().getModel().getPlayers();

        schoolboardButtonsGrid.getChildren().clear();

        int count = 0;
        for (Player player : players) {
            Button button = new Button(player.getUsername());
            button.getStyleClass().clear();
            button.getStyleClass().add("schoolboardButton");
            button.setOnMouseClicked(mouseEvent -> {
                fillSchoolboard(player);
                schoolboardButtonsGrid.getChildren().forEach(schoolboardButton -> schoolboardButton.setDisable(false));
                button.setDisable(true);
                schoolboardPlayerUsername = player.getUsername();
            });

            if (player.equals(selectedPlayer))
                button.setDisable(true);
            schoolboardButtonsGrid.addColumn(count, button);

            count++;
        }
    }

    /**
     * It adds the students icon of a given color in the dining room. It clears the
     * @param diningStudents students in the dining room
     * @param studentColor selected color
     * @param colorGrid grid with the given color
     */
    private void fillDining(Students diningStudents, Color studentColor, GridPane colorGrid) {
        colorGrid.getChildren().clear();
        for (int i = 0; i < diningStudents.get(studentColor); i++) {
            ImageView studentImage = new ImageView("graphics/pieces/" + studentColor.toString().toLowerCase() + "_student.png");
            studentImage = resizeImageView(studentImage, 42, 42);
            studentImage.setCursor(Cursor.HAND);
            studentImage.getStyleClass().add(PIECE_CLASS);

            colorGrid.addColumn(i, studentImage);
        }
    }

    /**
     * It shows the character info of a specific character
     * @param id the position of the character
     */
    private void showCharacterInfo(int id) {
        Character character = view.getGameHandler().getModel().getCharacters().get(id);

        characterPane.setVisible(true);
        mainPane.setEffect(new ColorAdjust(0.0, 0.0, -0.5, 0.0));
        mainPane.setDisable(true);

        String characterName = character.getClass().getSimpleName();
        String description = character.getDescription();

        characterImage.setImage(new Image("graphics/characters/" + characterName + ".jpg"));
        characterCostText.setText(String.valueOf(character.getCost()));

        characterStuff.setSpacing(5);
        characterStuff.getChildren().clear();

        // setting students (if any)
        if (character.getStudents() != null) {
            for (Color studentColor : character.getStudents().keySet()) {
                int numOfStudents = character.getStudents().get(studentColor);

                for (int i = 0; i < numOfStudents; i++) {
                    ImageView studentImage = new ImageView("graphics/pieces/" + studentColor.toString().toLowerCase() + "_student.png");
                    studentImage = resizeImageView(studentImage, 60, 60);

                    characterStuff.getChildren().add(studentImage);
                }
            }
        }

        // setting noEntryTiles (if any)
        for (int i = 0; i < character.getNoEntryTiles(); i++) {
            ImageView tileImage = new ImageView("graphics/other/no_entry_tile.png");
            tileImage = resizeImageView(tileImage, 60, 60);

            characterStuff.getChildren().add(tileImage);
        }

        characterNameLabel.setText(characterName);
        characterDescriptionLabel.setText(description);
        if (checkMessage(new SelectedCharacterMessage(id), thisPlayer) == StatusCode.OK) {
            activateCharacterButton.setOnMouseClicked(mouseEvent -> {
                GUI.getClientSocket().send(new SelectedCharacterMessage(id));
                characterPane.setVisible(false);
                mainPane.setEffect(null);
                mainPane.setDisable(false);
            });
            activateCharacterButton.setDisable(false);
        } else {
            activateCharacterButton.setDisable(true);
        }

        closeCharacterPaneButton.setOnMouseClicked(mouseEvent -> closeCharacterInfo());
    }

    /**
     * It hides the character info window
     */
    private void closeCharacterInfo() {
        characterPane.setVisible(false);
        mainPane.setEffect(null);
        mainPane.setDisable(false);
        GUI.setSelectingCharacter(null);
    }

    /**
     * It simulates a message and check whether it is valid
     * @param clientMessage message to check
     * @param player sender
     * @return status code of the message
     */
    private StatusCode checkMessage(ClientMessage clientMessage, Player player) {
        GameHandler gameHandler = (GameHandler) DeepCopy.copy(GUI.getView().getGameHandler());
        NetworkManager networkManager = NetworkManager.createNetworkManager(gameHandler);
        return clientMessage.handle(networkManager, player);
    }

    /**
     * Place the nodes in circle inside the container with a specific radius
     * @param container parent Pane
     * @param nodes list of nodes to add
     * @param radius radius of the circumference
     */
    private void placeNodes(Pane container, List<Node> nodes, double radius) {
        int numNodes = nodes.size();
        double containerCenterX = container.getBoundsInParent().getWidth() / 2;
        double containerCenterY = container.getBoundsInParent().getHeight() / 2;
        double delta_angle = (double) 360 / numNodes;
        double current_angle = 180;
        ArrayList<Double> angle = new ArrayList<>();

        if (numNodes == 1) radius = 0;

        // computing some angle
        for (int i = 0; i < numNodes; i++) {
            angle.add(current_angle);
            current_angle -= delta_angle;
        }

        // adding nodes
        for (int i = 0; i < numNodes; i++) {
            Node node = nodes.get(i);
            Pair<Double, Double> nodeCenter = new Pair<>(containerCenterX + radius * Math.cos(angle.get(i) * Math.PI / 180), containerCenterY - radius * Math.sin(angle.get(i) * Math.PI / 180));

            // wrapping each node in a pane
            Pane nodePane = new Pane(node);
            nodePane.setLayoutX(nodeCenter.getFirst() - nodePane.getBoundsInParent().getWidth() / 2);
            nodePane.setLayoutY(nodeCenter.getSecond() - nodePane.getBoundsInParent().getHeight() / 2);

            container.getChildren().add(nodePane);
        }
    }

    /**
     * Add an error message
     * @param s error message
     */
    private void updateErrorMessage(String s) {
        errorMsg.setText(s);
        errorPane.setVisible(true);
        GUI.setShowingError(true);
    }

    /**
     * It resizes a given {@link ImageView}
     * @param image given image
     * @param width target width
     * @param height target height
     * @return resized image
     */
    private ImageView resizeImageView(ImageView image, int width, int height) {
        return resizeImageView(image, width, height, 0);
    }

    /**
     * It resizes a given {@link ImageView}
     * @param image given image
     * @param width target width
     * @param height target height
     * @param rotate target rotation factor
     * @return resized image
     */
    private ImageView resizeImageView(ImageView image, int width, int height, int rotate) {
        ImageView newImage = new ImageView(image.getImage());
        newImage.setFitWidth(width);
        newImage.setFitHeight(height);
        newImage.setRotate(rotate);

        return newImage;
    }

    /**
     * It brings up the given card (used by the assistant cards)
     * @param event OnMouseEntered event
     */
    public void bringUpCard(MouseEvent event) {
        ImageView imageView = (ImageView) event.getSource();
        imageView.getParent().setLayoutY(-40);
    }

    /**
     * It brings down the given card (used by the assistant cards)
     * @param event OnMouseExited event
     */
    public void bringDownCard(MouseEvent event) {
        ImageView imageView = (ImageView) event.getSource();
        imageView.getParent().setLayoutY(0);
    }

    /**
     * It adds a light effect
     * @param node target element
     */
    public void addLightEffect(Node node){
        node.setEffect(new ColorAdjust(0.0, 0.0, 0.5, 0.0));
    }

    /**
     * It adds a black and white effect
     * @param node target element
     */
    public void addBlackWhiteEffect(Node node) {
        ColorAdjust colorAdjust = new ColorAdjust();
        colorAdjust.setSaturation(-1);

        node.setEffect(colorAdjust);
    }

    /**
     * It adds the glowing effect
     * @param event mouse event
     */
    public void addGlowEffect(MouseEvent event) {
        ((ImageView) event.getTarget()).setEffect(new Glow(1));
    }

    /**
     * It removes the current effect
     * @param event mouse event
     */    public void removeGlowEffect(MouseEvent event) {
        ((ImageView) event.getTarget()).setEffect(null);
    }

    /**
     * It parses to the image url to obtain the student color.
     * @param imageView {@link ImageView} of a student
     * @return color of the student
     */
    public Color imageViewToColor(ImageView imageView) {
        // split the file name to get the color of the student
        String[] split_name = imageView.getImage().getUrl().split("[^a-zA-Z]");
        return Color.parseColor(split_name[split_name.length - 3]);
    }

    /**
     * It hides the error message
     */
    public void hideErrorPane(){
        errorPane.setVisible(false);
        GUI.setShowingError(false);
    }
}
