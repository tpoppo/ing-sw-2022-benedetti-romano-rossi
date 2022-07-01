package it.polimi.ingsw.view;

import it.polimi.ingsw.client.ClientSocket;
import it.polimi.ingsw.view.guicontroller.GUIController;
import it.polimi.ingsw.view.viewcontent.ViewContent;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class is used to manage the GUI (via javafx)
 */
public class GUI extends Application {
    private static ViewContent view;
    private static ClientSocket client_socket;
    private static String username;
    private static Stage stage;
    private static boolean creatingLobby;
    private static Integer selectingCharacter;
    private static boolean showingError;
    private final Logger LOGGER = Logger.getLogger(getClass().getName());

    private final HashMap<String, Scene> sceneMap = new HashMap<>();
    private static final String LOGIN = "/fxml/login.fxml";
    private static final String MENU = "/fxml/menu.fxml";
    private static final String LOBBY = "/fxml/lobby.fxml";
    private static final String GAME = "/fxml/game.fxml";

    private final HashMap<Scene, GUIController> controllerMap = new HashMap<>();
    public static GUI me;

    public GUI(){
        me = this;
    }

    /**
     * It is used to start a new GUI
     * @param args command line arguments
     */
    public static void main(String[] args) {
   //     System.setProperty("prism.allowhidpi", "false");
        launch(args);
    }

    /**
     * It preloads the FXML scenes
     */
    public void setup() {
        List<String> fxmlList = new ArrayList<>(Arrays.asList(LOGIN, MENU, LOBBY, GAME));
        try {
            for (String path : fxmlList) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
                Scene scene = new Scene(loader.load());
                sceneMap.put(path, scene);
                GUIController controller = loader.getController();
                controllerMap.put(scene, controller);
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            e.printStackTrace();
        }
    }

    /**
     * It resizes the selected scenes
     * @param scenePaths list of scenes names
     * @param width target width
     * @param height target height
     */
    private void resizeScenes(List<String> scenePaths, int width, int height){
        for(String scenePath : scenePaths){
            Scene sceneToResize = sceneMap.get(scenePath);
            sceneMap.replace(scenePath, resizeScene(sceneToResize, width, height));
        }
    }

    /**
     * It initializes all the GUI components and variables
     * It is called when the GUI is created
     * @param stage JavaFX GUI
     */
    @Override
    public void start(Stage stage) {
        setup();

        resizeScenes(List.of(GAME), 1920, 1080);

        GUI.stage = stage;
        stage.setTitle("Eriantys");
        stage.getIcons().add(new Image("/graphics/other/coin.png"));
        stage.setFullScreenExitHint("");
        stage.setResizable(false);

        Scene scene = sceneMap.get(LOGIN);
        scene.getStylesheets().add("css/login.css");

        stage.setScene(scene);
        stage.show();

        startViewContentUpdates();
        stage.setOnCloseRequest(event -> {
            event.consume();
            logout();
        });

        // it manages the server shutdown
        new Thread(() -> {
            while(true) {
                synchronized (client_socket.mutex_closed){
                    try {
                        client_socket.mutex_closed.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    if (!client_socket.isOpened()) {
                        Platform.runLater(() -> {
                            Alert alert = new Alert(Alert.AlertType.WARNING);
                            alert.setTitle("Server shutdown");
                            alert.setHeaderText("The server stopped. You are about to exit the game");
                            alert.showAndWait();

                            stage.close();
                            System.exit(0);
                        });
                    }
                }
            }
        }).start();
    }

    /**
     * It starts a thread that updates the view whenever a {@link ViewContent} is received.
     */
    private void startViewContentUpdates(){
        new Thread(() -> {
            while (true) {
                synchronized (client_socket.mutex_view) {
                    while (client_socket.getView() == null) {
                        try {
                            client_socket.mutex_view.wait();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }

                GUI.view = client_socket.getView();

                if (view.getCurrentHandler() == null) { // show the menu
                    Platform.runLater(() -> {
                        switchScene(sceneMap.get(MENU), false);
                    });
                } else {
                    switch (view.getCurrentHandler()) {
                        case LOBBY -> // show the lobby
                                Platform.runLater(() -> switchScene(sceneMap.get(LOBBY), false));

                        case GAME -> // show the game
                                Platform.runLater(() -> {
                                    long startTime = System.currentTimeMillis();
                                    switchScene(sceneMap.get(GAME), true);

                                    long estimatedTime = System.currentTimeMillis() - startTime;
                                    System.out.println("estimatedTime: " + estimatedTime);
                                });
                    }
                }

                synchronized (client_socket.mutex_view){
                    client_socket.setView(null);
                    client_socket.mutex_view.notifyAll();
                }
            }
        }).start();
    }

    /**
     * It is called when a player tries to log out.
     */
    private static void logout(){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Exit");
        alert.setHeaderText("You are about to exit the game");
        alert.setContentText("Are you sure you want to exit?");

        if(alert.showAndWait().get() == ButtonType.OK){

            System.out.println("You are about to logout");
            stage.close();
            try {
                getClientSocket().closeConnection();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            System.exit(0);
        }
    }

    public void switchScene(Scene scene, boolean fullscreen) {
        stage.setScene(scene);
        controllerMap.get(scene).setup();

        stage.setFullScreen(fullscreen);
        stage.show();
    }

    /**
     * It resizes the given string
     * @param scene selected scene
     * @param width selected width
     * @param height selected height
     * @return resized scene
     */
    public Scene resizeScene(Scene scene, int width, int height) {
        Dimension resolution = Toolkit.getDefaultToolkit().getScreenSize();
        double currentWidth = resolution.getWidth();
        double currentHeight = resolution.getHeight();
        double w = currentWidth/width;
        double h = currentHeight/height;
        Scale scale = new Scale(w, h, 0, 0);
        scene.getRoot().getTransforms().add(scale);

        return scene;
    }

    public static String getUsername() {
        return username;
    }

    public static void setUsername(String username) {
        GUI.username = username;
    }

    public static ClientSocket getClientSocket() {
        return client_socket;
    }

    public static void setClientSocket(ClientSocket client_socket) {
        GUI.client_socket = client_socket;
    }

    public static ViewContent getView() {
        return view;
    }

    public static boolean isCreatingLobby() {
        return creatingLobby;
    }

    public static void setCreatingLobby(boolean creatingLobby) {
        GUI.creatingLobby = creatingLobby;
    }

    public static Integer getSelectingCharacter() {
        return selectingCharacter;
    }

    public static void setSelectingCharacter(Integer selectingCharacter) {
        GUI.selectingCharacter = selectingCharacter;
    }

    public static boolean isShowingError() {
        return showingError;
    }

    public static void setShowingError(boolean showingError) {
        GUI.showingError = showingError;
    }
}
