package it.polimi.ingsw.view;

import it.polimi.ingsw.client.ClientSocket;
import it.polimi.ingsw.model.Player;
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

public class GUI extends Application {
    private static ViewContent view;
    private static ClientSocket client_socket;
    private static String username;
    private static Stage stage;
    private static boolean creatingLobby;
    private static Integer selectingCharacter;
    public static Player schoolboardPlayer;
    private final Logger LOGGER = Logger.getLogger(getClass().getName());

    private final HashMap<String, Scene> sceneMap = new HashMap<>();
    private static final String LOGIN = "/fxml/login.fxml";
    private static final String MENU = "/fxml/menu.fxml";
    private static final String LOBBY = "/fxml/lobby.fxml";
    private static final String GAME = "/fxml/game.fxml";

    private final HashMap<Scene, GUIController> controllerMap = new HashMap<>();

    public static void main(String[] args) {
   //     System.setProperty("prism.allowhidpi", "false");
        launch(args);
    }

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

    private void resizeScenes(List<String> scenePaths, int width, int height){
        for(String scenePath : scenePaths){
            Scene sceneToResize = sceneMap.get(scenePath);
            sceneMap.replace(scenePath, resizeScene(sceneToResize, width, height));
        }
    }

    @Override
    public void start(Stage stage) throws IOException {
        setup();

        resizeScenes(List.of(GAME), 1920, 1080);

        GUI.stage = stage;
        stage.setTitle("Eriantys");
        stage.getIcons().add(new Image(GUI.class.getResourceAsStream("/graphics/other/coin.png")));
        stage.setFullScreenExitHint("");
        stage.setResizable(false);

        Scene scene = sceneMap.get(LOGIN);
        scene.getStylesheets().add("css/login.css");

        stage.setScene(scene);
        stage.show();

        startViewContentUpdates(stage);
        stage.setOnCloseRequest(event -> {
            event.consume();
            logout();
        });
    }

    private void startViewContentUpdates(Stage stage){
        new Thread(() -> {
            while (true) {
                synchronized (client_socket.mutex) {
                    while (client_socket.getView() == null) {
                        try {
                            client_socket.mutex.wait();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }

                GUI.view = client_socket.getView();

                if (view.getCurrentHandler() == null) { // show the menu
                    Platform.runLater(() -> {
                        try {
                            switchScene(sceneMap.get(MENU));
                            stage.getScene().getStylesheets().add("css/menu.css");
                            stage.setFullScreen(false);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
                } else {
                    switch (view.getCurrentHandler()) {
                        case LOBBY -> // show the lobby
                                Platform.runLater(() -> {
                                    try {
                                        switchScene(sceneMap.get(LOBBY));
                                        stage.getScene().getStylesheets().add("css/lobby.css");
                                        stage.setFullScreen(false);
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                });

                        case GAME -> // show the game
                                Platform.runLater(() -> {
                                    try {
                                        long startTime = System.currentTimeMillis();
                                        switchScene(sceneMap.get(GAME));
                                        stage.getScene().getStylesheets().add("css/game.css");
                                        stage.setFullScreen(true);
                                        long estimatedTime = System.currentTimeMillis() - startTime;
                                        System.out.println("estimatedTime: " + estimatedTime);
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                });
                    }
                }

                synchronized (client_socket.mutex){
                    client_socket.setView(null);
                    client_socket.mutex.notifyAll();
                }
            }
        }).start();
    }

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

    public void switchScene(Scene scene) throws IOException{
        stage.setScene(scene);
        controllerMap.get(scene).setup();

        stage.show();
    }

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
}
