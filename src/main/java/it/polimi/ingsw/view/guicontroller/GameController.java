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
import it.polimi.ingsw.utils.Pair;
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

import java.net.URL;
import java.util.*;
import java.util.function.Consumer;


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
    private Label assistantLabel1, assistantLabel2, assistantLabel3, assistantLabel4, assistantLabel5, assistantLabel6, assistantLabel7, assistantLabel8, assistantLabel9, assistantLabel10;
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
    @FXML
    private ImageView endingScreen;

    @FXML
    private Pane islandsPane;

    @FXML
    private Pane colorSelectionPane;
    @FXML
    private ImageView chooseRed, chooseYellow, chooseGreen, chooseCyan, chooseMagenta;

    private ViewContent view;
    private List<ImageView> assistantCards;
    private Player thisPlayer;
    private Player schoolboardPlayer;
    private Map<Player, TowerColor> playerTowerColorMap;
    private ImageView selectedEntrance;
    private List<Pane> cloudPanes;

    private final String PIECE_CLASS = "piece";

    // FIXME: split class
    private ImageView selectedStudentCard;
    private ArrayList<ImageView> selectedOnCard;
    private ArrayList<ImageView> selectedOnEntrance;
    private ArrayList<ImageView> selectedOnDining;

    private ArrayList<Pane> islandPanes;
    private Map<Color, ImageView> chooseColor;
    private List<Label> assistantLabelCards;
    private HashMap<Integer, VBox> characterStuffSmallMap;

    private ArrayList<Consumer<Player>> onfillSchoolboard;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.view = GUI.getView();
        ViewContent view = GUI.getView();
        System.out.println(view);

        GameHandler gameHandler = view.getGameHandler();
        Game game = gameHandler.getModel();

        cloudPanes = new ArrayList<>();
        assistantCards = List.of(assistant1, assistant2, assistant3, assistant4, assistant5, assistant6, assistant7, assistant8, assistant9, assistant10);
        assistantLabelCards = List.of(assistantLabel1, assistantLabel2, assistantLabel3, assistantLabel4, assistantLabel5, assistantLabel6, assistantLabel7, assistantLabel8, assistantLabel9, assistantLabel10);

        chooseColor = Map.of(Color.RED, chooseRed, Color.YELLOW, chooseYellow, Color.GREEN, chooseGreen, Color.CYAN, chooseCyan, Color.MAGENTA, chooseMagenta);
        playerTowerColorMap = new HashMap<>();
        selectedOnDining = new ArrayList<>();
        selectedOnEntrance = new ArrayList<>();
        selectedOnCard = new ArrayList<>();
        thisPlayer = game.usernameToPlayer(GUI.getUsername());
        schoolboardPlayer = thisPlayer;
        islandPanes = new ArrayList<>();
        characterStuffSmallMap = new HashMap<>();
        onfillSchoolboard = new ArrayList<>();

        // setting up player - towerColor association
        for (int i = 0; i < view.getGameHandler().getModel().getPlayers().size(); i++)
            playerTowerColorMap.put(view.getGameHandler().getModel().getPlayers().get(i), TowerColor.values()[i]);


        // all these functions are less than 300 ms. Less than 20%
        setupState();
        setupIslands();
        updateSchoolboard();
        setupAssistants();
        setupErrorMessage();
        setupClouds();
        setupMotherNature();
        setupActions();
        setupPlayOrder();

        // displaying bag capacity
        bagCapacityText.setText(String.valueOf(game.getBag().capacity()));

        // expert mode
        if (view.getGameHandler().getModel().getExpertMode()) {
            setupNextTurnButton();
            setupCharacters();
        } else {
            hideExpertModeComponents();
        }

        if (GUI.getSelectingCharacter() != null)
            showCharacterInfo(GUI.getSelectingCharacter());

        // adding the listeners after building the schoolboard (the first time)
        for (Consumer<Player> f : onfillSchoolboard) {
            f.accept(thisPlayer);
        }
    }

    private void setupIslands() { //FIXME: it does not create empty island
        List<Island> islands = view.getGameHandler().getModel().getIslands();
        List<Node> islandNodes = new ArrayList<>();

        for (int i = 0; i < islands.size(); i++) {
            // every island has its own pane
            Pane islandPane = new Pane();
            List<Node> studentNodes = new ArrayList<>();
            int islandSize = islands.get(i).getNumIslands();

            // adding island image
            ImageView islandImage = new ImageView("/graphics/islands/" + i % 3 + ".png");
            Image image = new Image("/graphics/islands/" + i % 3 + ".png");
            islandImage.setImage(image);

            // FIXME: cut island images so that they are standard (possibly in a square)

            int dimX = (int) (0.4 * image.getWidth()) * islandSize; // FIXME: choose a better scale factor
            int dimY = (int) (0.4 * image.getHeight()) * islandSize;
            islandImage = resizeImageView(islandImage, dimX, dimY);

            islandPane.getChildren().add(islandImage);

            // adding island's elements on top (creating a list of nodes of every component to be shown)
            // adding students, getting the colors of the students currently on the island
            HashMap<Color, Integer> presentStudents = new HashMap<>();
            islands.get(i).getStudents().forEach((color, quantity) -> {
                if (quantity > 0)
                    presentStudents.put(color, quantity);
            });

            if (presentStudents.keySet().size() > 0) {
                // creating a stackPane with image + quantity for each student color present
                for (Color studentColor : presentStudents.keySet()) {
                    int numStudents = presentStudents.get(studentColor);

                    StackPane studentStackPane = new StackPane();
                    ImageView studentImage = new ImageView("/graphics/pieces/" + studentColor.toString().toLowerCase() + "_student.png");
                    studentImage = resizeImageView(studentImage, 30, 30);
                    studentImage.getStyleClass().add(PIECE_CLASS);

                    Text studentNumber = new Text(String.valueOf(numStudents));
                    studentNumber.getStyleClass().add("borderedText");
                    // FIXME: resize text

                    studentStackPane.setPrefSize(studentImage.getFitWidth(), studentImage.getFitHeight());
                    studentStackPane.getChildren().add(studentImage);
                    studentStackPane.getChildren().add(studentNumber);

                    studentNodes.add(studentStackPane);
                }

                // adding towers
                int numTowers = islands.get(i).getNumTowers();
                if (numTowers > 0) {
                    StackPane towerStackPane = new StackPane();
                    String towerColor = String.valueOf(playerTowerColorMap.get(islands.get(i).getOwner())).toLowerCase();

                    ImageView towerImage = new ImageView("graphics/pieces/towers/tower_" + towerColor + ".png");
                    towerImage = resizeImageView(towerImage, 35, 35);
                    towerImage.getStyleClass().add(PIECE_CLASS);

                    Text towerNumber = new Text(String.valueOf(numTowers));
                    towerNumber.getStyleClass().add("borderedText");
                    // FIXME: resize text

                    towerStackPane.getChildren().add(towerImage);
                    towerStackPane.getChildren().add(towerNumber);

                    double x = islandPane.getBoundsInParent().getWidth() / 2 - towerStackPane.getBoundsInParent().getWidth() / 2;
                    double y = islandPane.getBoundsInParent().getHeight() / 2 - towerStackPane.getBoundsInParent().getHeight() / 2;
                    // if there's only one student color, the tower gets added on top of it, otherwise it is displayed in the center
                    if (presentStudents.size() == 1)
                        y -= 30; // TODO: check if this gets displayed correctly

                    towerStackPane.setLayoutX(x);
                    towerStackPane.setLayoutY(y);
                    islandPane.getChildren().add(towerStackPane);
                }

                // adding all the pieces on top of the islandPane
                double radius = islandPane.getBoundsInParent().getWidth() / 4;
                placeNodes(islandPane, studentNodes, radius);

                // adding the updated islandPane to the list
                islandNodes.add(islandPane);
                islandPanes.add(islandPane);
            }

        }

        // making a regular polygon with the provided nodes in the given container
        double radius = islandsPane.getBoundsInParent().getWidth() * 2 / 3 / 2;
        placeNodes(islandsPane, islandNodes, radius);
    }


    private void setupClouds() {
        Game model = view.getGameHandler().getModel();

        for (Students cloud : model.getClouds()) {
            Pane cloudPane = new Pane();
            ImageView cloudImage = new ImageView("graphics/islands/cloud.png");
            cloudImage = resizeImageView(cloudImage, 128, 128); // TODO: parametrize these

            cloudPane.getChildren().add(cloudImage);

            if (cloud.count() == 0)
                cloudImage.setEffect(new ColorAdjust(0.0, 0.0, 0.5, 0.0)); // TODO: add effect on utils or something
            else {
                List<Node> cloudTopping = new ArrayList<>();
                for (Color key : cloud.keySet()) {
                    if (cloud.get(key) > 0) {
                        StackPane cloudStackPane = new StackPane();

                        ImageView studentImage = new ImageView("graphics/pieces/" + key.toString().toLowerCase() + "_student.png");
                        studentImage = resizeImageView(studentImage, 30, 30);
                        studentImage.getStyleClass().add(PIECE_CLASS);

                        Text quantity = new Text(String.valueOf(cloud.get(key)));
                        quantity.getStyleClass().add("borderedText");

                        cloudStackPane.getChildren().add(studentImage);
                        cloudStackPane.getChildren().add(quantity);

                        cloudTopping.add(cloudStackPane);
                    }
                }

                double radius = cloudPane.getBoundsInParent().getWidth() / 4;
                placeNodes(cloudPane, cloudTopping, radius);

                cloudPanes.add(cloudPane);
            }
        }

        List<Node> cloudNodes = new ArrayList<>(cloudPanes);
        double radius = islandsPane.getBoundsInParent().getWidth() * 1 / 5 / 2;
        placeNodes(islandsPane, cloudNodes, radius);
    }

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

            // setting coin number
            Text coinText = (Text) ((StackPane) ((Pane) charactersGrid.getChildren().get(count)).getChildren().get(1)).getChildren().get(1);
            coinText.setText(String.valueOf(character.getCost()));

            VBox characterStuffSmall = (VBox) ((Pane) charactersGrid.getChildren().get(count)).getChildren().get(2);
            characterStuffSmallMap.put(id, characterStuffSmall);
            characterStuffSmall.setSpacing(3);

            // setting students (if any)
            if (character.getStudents() != null) {
                int totalStudents = character.getStudents().count();

                for (Color studentColor : character.getStudents().keySet()) {
                    int numOfStudents = character.getStudents().get(studentColor);

                    for (int i = 0; i < numOfStudents; i++) {
                        int size = 112 / totalStudents; // FIXME: this probably creates funny stuff with few students

                        ImageView studentImage = new ImageView("graphics/pieces/" + studentColor.toString().toLowerCase() + "_student.png");
                        studentImage = resizeImageView(studentImage, size, size);

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

    private void setupActions() {
        GameHandler gameHandler = view.getGameHandler();

        // enable and create objects depending on the current state
        String action_text = null;
        if (gameHandler.getCurrentState() == GameState.ENDING) {
            action_text = "The end";
            prepareEnding();
        } else if (gameHandler.getModel().getCurrentPlayer().getUsername().equals(GUI.getUsername())) { // if it is your turn

            if (!gameHandler.isActionCompleted()) {
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

    private void setupMotherNature() {
        int position = view.getGameHandler().getModel().findMotherNaturePosition();
        Pane island = islandPanes.get(position);
        System.out.println(position);

        motherNature.setLayoutX(island.getLayoutX() + island.getBoundsInParent().getWidth() / 2);
        motherNature.setLayoutY(island.getLayoutY() + island.getBoundsInParent().getHeight());
        island.getChildren().add(motherNature);
    }

    private void prepareEnding() {
        if (view.getGameHandler().getModel().winner().getUsername().equals(GUI.getUsername())) {
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
        Game game = view.getGameHandler().getModel();
        Character character = view.getGameHandler().getSelectedCharacter();
        if (character == null) {
            updateErrorMessage("Invalid character"); // it should be unreachable
            return;
        }
        // get the position of the selected character
        int position = 0;

        while (!game.getCharacters().get(position).equals(character)) position++;
        System.out.println(position);
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
                    islandPane.setEffect(new ColorAdjust(0.0, 0.0, 0.5, 0.0));
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
                    });
                }
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
                            selectedStudentCard = null;
                            selectedStudentCard.setEffect(null);
                            islandPanes.forEach(island -> island.setEffect(null));
                        } else {
                            if (selectedStudentCard != null) {
                                selectedStudentCard.setEffect(null);
                            }
                            selectedStudentCard = imageView;
                            selectedStudentCard.setEffect(new ColorAdjust(0.0, 0.0, 0.5, 0.0));
                            islandPanes.forEach(island -> island.setEffect(new ColorAdjust(0.0, 0.0, 0.5, 0.0)));
                        }
                    });
                });

                // 2) select an island
                for (int i = 0; i < islandPanes.size(); i++) {
                    int finalI = i;
                    Pane islandPane = islandPanes.get(i);
                    islandPane.setOnMouseClicked(mouseEvent -> {
                        if (selectedStudentCard != null) {
                            Color color = imageViewToColor(selectedEntrance);
                            PlayerChoicesSerializable playerChoicesSerializable = new PlayerChoicesSerializable();
                            playerChoicesSerializable.setIsland(finalI);
                            playerChoicesSerializable.setStudent(color);
                            GUI.getClientSocket().send(new ActivateCharacterMessage(playerChoicesSerializable));
                        } else {
                            updateErrorMessage("You must select a student from the character first");
                        }
                    });
                    islandPane.setEffect(new ColorAdjust(0.0, 0.0, 0.5, 0.0));
                }
            }

            case SWAP_CARD_ENTRANCE -> {
                // it wants up to 3 students from the entrance and up to 3 students from the entrance
                int finalPosition = position;
                onfillSchoolboard.add(player -> {
                    // setup global variables and objects
                    selectedOnEntrance.clear();
                    nextTurnButton.setEffect(new ColorAdjust(0.0, 0.0, -0.5, 0.0));

                    // student can be selected only if this is my schoolboard!
                    if (!player.equals(thisPlayer)) return;


                    // 1) select up to 3 students from this card
                    VBox characterStuffSmall = characterStuffSmallMap.get(finalPosition);
                    characterStuffSmall.getChildren().forEach(node -> {
                        ImageView imageView = (ImageView) node;
                        imageView.setOnMouseClicked(e -> {
                            if (selectedOnCard.contains(imageView)) { // deselect a student
                                selectedOnCard.remove(imageView);
                                imageView.setEffect(null);
                            } else {
                                selectedOnCard.add(imageView);
                                imageView.setEffect(new ColorAdjust(0.0, 0.0, 0.5, 0.0));

                                if (selectedOnCard.size() > 3) { // remove the first, if there are more than 3 cards
                                    selectedOnCard.get(0).setEffect(null);
                                    selectedOnCard.remove(0);
                                }
                            }

                            if (selectedOnCard.size() == selectedOnEntrance.size()) {
                                nextTurnButton.setEffect(null);
                            } else {
                                nextTurnButton.setEffect(new ColorAdjust(0.0, 0.0, -0.5, 0.0));
                            }
                        });
                    });

                    // 2) select up to 3 students from the entrance
                    entranceGrid.getChildren().forEach(node -> {
                        ImageView imageView = (ImageView) node;

                        imageView.setOnMouseClicked(e -> {
                            if (selectedOnEntrance.contains(imageView)) { // deselect a student
                                selectedOnEntrance.remove(imageView);
                                imageView.setEffect(null);
                            } else {
                                selectedOnEntrance.add(imageView);
                                imageView.setEffect(new ColorAdjust(0.0, 0.0, 0.5, 0.0));

                                if (selectedOnEntrance.size() > 3) { // remove the first, if there are more than 3 cards
                                    selectedOnEntrance.get(0).setEffect(null);
                                    selectedOnEntrance.remove(0);
                                }
                                if (selectedOnCard.size() == selectedOnEntrance.size()) {
                                    nextTurnButton.setEffect(null);
                                } else {
                                    nextTurnButton.setEffect(new ColorAdjust(0.0, 0.0, -0.5, 0.0));
                                }
                            }
                        });
                    });

                    // 3) confirm selection
                    nextTurnButton.setOnMouseClicked(mouseEvent -> {
                        if (selectedOnCard.size() == selectedOnEntrance.size()) {
                            PlayerChoicesSerializable playerChoicesSerializable = new PlayerChoicesSerializable();
                            for (int i = 0; i < selectedOnCard.size(); i++) {
                                playerChoicesSerializable.setStudent(imageViewToColor(selectedOnEntrance.get(i)));
                                playerChoicesSerializable.setStudent(imageViewToColor(selectedOnCard.get(i)));
                            }
                            GUI.getClientSocket().send(new ActivateCharacterMessage(playerChoicesSerializable));
                        } else {
                            updateErrorMessage("You must select the same number of students");
                        }
                    });
                });
            }

            case SWAP_DINING_ENTRANCE -> {

                // it wants up to 2 students from the entrance and up to 2 students from the entrance
                onfillSchoolboard.add(player -> {
                    // setup global variables
                    selectedOnEntrance.clear();
                    selectedOnDining.clear();
                    nextTurnButton.setEffect(new ColorAdjust(0.0, 0.0, -0.5, 0.0));

                    // student can be selected only if this is my schoolboard!
                    if (!player.equals(thisPlayer)) return;

                    // 1) select up to 2 students from this card
                    List.of(greenDining, magentaDining, cyanDining, redDining, yellowDining).forEach(gridPane -> {
                        gridPane.getChildren().forEach(node -> {
                            ImageView imageView = (ImageView) node;

                            imageView.setOnMouseClicked(e -> {
                                if (selectedOnDining.contains(imageView)) { // deselect a student
                                    selectedOnDining.remove(imageView);
                                    imageView.setEffect(null);
                                } else {
                                    selectedOnDining.add(imageView);
                                    imageView.setEffect(new ColorAdjust(0.0, 0.0, 0.5, 0.0));

                                    if (selectedOnDining.size() > 2) { // remove the first, if there are more than 3 cards
                                        selectedOnDining.get(0).setEffect(null);
                                        selectedOnDining.remove(0);
                                    }
                                }

                                if (selectedOnDining.size() == selectedOnEntrance.size()) {
                                    nextTurnButton.setEffect(null);
                                } else {
                                    nextTurnButton.setEffect(new ColorAdjust(0.0, 0.0, -0.5, 0.0));
                                }
                            });

                        });
                    });

                    // 2) select up to 3 students from the entrance
                    entranceGrid.getChildren().forEach(node -> {
                        ImageView imageView = (ImageView) node;

                        imageView.setOnMouseClicked(e -> {
                            if (selectedOnEntrance.contains(imageView)) { // deselect a student
                                selectedOnEntrance.remove(imageView);
                                imageView.setEffect(null);
                            } else {
                                selectedOnEntrance.add(imageView);
                                imageView.setEffect(new ColorAdjust(0.0, 0.0, 0.5, 0.0));

                                if (selectedOnEntrance.size() > 2) { // remove the first, if there are more than 3 cards
                                    selectedOnEntrance.get(0).setEffect(null);
                                    selectedOnEntrance.remove(0);
                                }
                            }

                            if (selectedOnDining.size() == selectedOnEntrance.size()) {
                                nextTurnButton.setEffect(null);
                            } else {
                                nextTurnButton.setEffect(new ColorAdjust(0.0, 0.0, -0.5, 0.0));
                            }
                        });
                    });

                    // 3) confirm selection
                    nextTurnButton.setOnMouseClicked(mouseEvent -> {
                        if (selectedOnDining.size() == selectedOnEntrance.size()) {
                            PlayerChoicesSerializable playerChoicesSerializable = new PlayerChoicesSerializable();
                            for (int i = 0; i < selectedOnDining.size(); i++) {
                                playerChoicesSerializable.setStudent(imageViewToColor(selectedOnEntrance.get(i)));
                                playerChoicesSerializable.setStudent(imageViewToColor(selectedOnDining.get(i)));
                            }
                            GUI.getClientSocket().send(new ActivateCharacterMessage(playerChoicesSerializable));
                        } else {
                            updateErrorMessage("You must select the same number of students");
                        }
                    });
                });
            }
        }
    }

    private void prepareMoveStudent() {
        Game game = view.getGameHandler().getModel();
        ArrayList<Island> islands = game.getIslands();
        System.out.println(islands.size() + " - " + islandPanes.size());
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

        onfillSchoolboard.add(player -> {
            // student can be selected only if this is my schoolboard!
            if (!player.equals(thisPlayer)) return;

            for (Node node : entranceGrid.getChildren()) {
                ImageView imageView = (ImageView) node;

                imageView.setOnMouseClicked(mouseEvent -> {
                    if (imageView != selectedEntrance) { // select a student
                        if (selectedEntrance != null) {
                            selectedEntrance.setEffect(null);
                        }
                        selectedEntrance = imageView;
                        imageView.setEffect(new ColorAdjust(0.0, 0.0, 0.5, 0.0));

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
                    .forEach(d -> {
                        d.setOnMouseClicked(mouseEvent -> {
                            if (selectedEntrance != null) {

                                Color color = imageViewToColor(selectedEntrance);
                                GUI.getClientSocket().send(new MoveStudentMessage(color));
                            } else {
                                updateErrorMessage("You must select a student first");
                            }
                        });
                    });
        });


    }


    private void prepareMotherNature() {
        Game game = view.getGameHandler().getModel();
        for (int i = 0; i < islandPanes.size(); i++) {
            Pane islandPane = islandPanes.get(i);
            System.out.println(i + ": " + checkMessage(new MoveMotherNatureMessage(i), thisPlayer));
            if (checkMessage(new MoveMotherNatureMessage(i), thisPlayer) == StatusCode.OK) {
                int finalI = i;

                islandPane.setOnMouseClicked(mouseEvent -> {
                    GUI.getClientSocket().send(new MoveMotherNatureMessage(finalI));
                });
                islandPane.setEffect(new ColorAdjust(0.0, 0.0, 0.5, 0.0));
            } else {
                islandPane.setEffect(new ColorAdjust(0.0, 0.0, -0.5, 0.0));
            }
        }
    }

    private void prepareChooseCloud() {
        for (int i = 0; i < cloudPanes.size(); i++) {
            Pane cloudPane = cloudPanes.get(i);
            ChooseCloudMessage cloudMessage = new ChooseCloudMessage(i);

            if (checkMessage(cloudMessage, view.getGameHandler().getModel().usernameToPlayer(GUI.getUsername())) == StatusCode.OK) {
                cloudPane.setOnMouseClicked(mouseEvent -> {
                    GUI.getClientSocket().send(cloudMessage);
                });
            } else {
                cloudPane.setOnMouseClicked(null);
                cloudPane.setEffect(new ColorAdjust(0.0, 0.0, -0.5, 0.0));
            }
        }
    }

    private void setupState() {
        String currentPlayerUsername = view.getGameHandler().getModel().getCurrentPlayer().getUsername();

        turnLabel.setText("Turn: " + currentPlayerUsername);
        usernameLabel.setText(thisPlayer.getUsername());
    }

    private void setupErrorMessage() {
        if (view.getErrorMessage() != null) {
            updateErrorMessage(view.getErrorMessage());
        } else {
            errorMsg.setVisible(false);
        }
    }

    private void setupNextTurnButton() {
        if (checkMessage(new NextStateMessage(), thisPlayer) == StatusCode.OK) {
            nextTurnButton.setOnMouseClicked(mouseEvent -> GUI.getClientSocket().send(new NextStateMessage()));
        } else {
            nextTurnButton.setEffect(new ColorAdjust(0.0, 0.0, -0.5, 0.0));
        }
    }

    private void setupAssistants() {
        for (Assistant assistant : Assistant.getAssistants(thisPlayer.getWizard())) {
            ImageView imageView = assistantCards.get(assistant.getPower() - 1);
            Label label = assistantLabelCards.get(assistant.getPower() - 1);
            Player player = view.getGameHandler().getModel().getPlayers()
                    .stream()
                    .filter(p -> assistant.equals(p.getCurrentAssistant()))
                    .findFirst().orElse(null);
            if(player != null){
                label.setText(player.getUsername());
            } else{
                label.setText("");
            }



            if (thisPlayer.getPlayerHand().contains(assistant)) {
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
        for (Assistant assistant : Assistant.getAssistants(thisPlayer.getWizard())) {
            ImageView imageView = assistantCards.get(assistant.getPower() - 1);
            if (checkMessage(new PlayAssistantMessage(thisPlayer.getPlayerHand().indexOf(assistant)), thisPlayer) != StatusCode.OK) {
                if (imageView.getEffect() == null) imageView.setEffect(new ColorAdjust(0.0, 0.0, -0.5, 0.0));
            }
        }
    }

    private void hideExpertModeComponents() {
        charactersPane.setVisible(false);
        coinsPane.setVisible(false);
        nextTurnButton.setVisible(false);
    }

    private void updateSchoolboard() {
        // shows this player's schoolboard by default
        fillSchoolboard(schoolboardPlayer);
        setSchoolBoardButtons(schoolboardPlayer);
    }

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
        Text coinNumber = new Text(String.valueOf(coins));
        coinNumber.getStyleClass().add("coinNumber");
        coinsPane.getChildren().add(coinNumber);

        // adding the listeners after building the schoolboard
        for (Consumer<Player> f : onfillSchoolboard) {
            f.accept(player);
        }
    }

    private void fillDining(Students diningStudents, Color studentColor, GridPane colorGrid) {
        System.out.println("Adding " + diningStudents.get(studentColor) + " " + studentColor);

        colorGrid.getChildren().clear();
        for (int i = 0; i < diningStudents.get(studentColor); i++) {
            ImageView studentImage = new ImageView("graphics/pieces/" + studentColor.toString().toLowerCase() + "_student.png");
            studentImage = resizeImageView(studentImage, 45, 45);
            studentImage.setCursor(Cursor.HAND);
            studentImage.getStyleClass().add(PIECE_CLASS);

            colorGrid.addColumn(i, studentImage);
        }
    }

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
            });

            if (player.equals(selectedPlayer))
                button.setDisable(true);
            schoolboardButtonsGrid.addColumn(count, button);

            count++;
        }
    }

    private void showCharacterInfo(int id) {
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
            });
        } else {
            activateCharacterButton.setDisable(true);
        }

        closeCharacterPaneButton.setOnMouseClicked(mouseEvent -> closeCharacterInfo());
    }

    private void closeCharacterInfo() {
        characterPane.setVisible(false);
        mainPane.setEffect(null);
        GUI.setSelectingCharacter(null);
    }

    private StatusCode checkMessage(ClientMessage clientMessage, Player player) {
        GameHandler gameHandler = (GameHandler) DeepCopy.copy(GUI.getView().getGameHandler());
        NetworkManager networkManager = NetworkManager.createNetworkManager(gameHandler);
        return clientMessage.handle(networkManager, player);
    }

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

    private void updateErrorMessage(String s) {
        errorMsg.setText(s);
        errorMsg.setVisible(true);
    }

    private ImageView resizeImageView(ImageView image, int width, int height) {
        return resizeImageView(image, width, height, 0);
    }

    private ImageView resizeImageView(ImageView image, int width, int height, int rotate) {
        ImageView newImage = new ImageView(image.getImage());
        newImage.setFitWidth(width);
        newImage.setFitHeight(height);
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

    public void addGlowEffect(MouseEvent event) {
        ((ImageView) event.getTarget()).setEffect(new Glow(1));
    }

    public void removeGlowEffect(MouseEvent event) {
        ((ImageView) event.getTarget()).setEffect(null);
    }

    public Color imageViewToColor(ImageView imageView) {
        // split the file name to get the color of the student
        String[] sname = imageView.getImage().getUrl().split("[^a-zA-Z]");
        return Color.parseColor(sname[sname.length - 3]);
    }

}
