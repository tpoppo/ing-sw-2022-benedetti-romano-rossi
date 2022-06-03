package it.polimi.ingsw.view;

import it.polimi.ingsw.client.ClientSocket;
import it.polimi.ingsw.view.viewcontent.ViewContent;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.CacheHint;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;

public class GUI extends Application {
    private static ViewContent view;
    private static ClientSocket client_socket;
    private static String username;
    private static Stage stage;
    private static boolean creatingLobby;
    private static Integer selectingCharacter;

    public static void main(String[] args) {
   //     System.setProperty("prism.allowhidpi", "false");
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        GUI.stage = stage;
        stage.setTitle("Eriantys");
        stage.getIcons().add(new Image(GUI.class.getResourceAsStream("/graphics/other/coin.png")));
        stage.setFullScreenExitHint("");
        stage.setResizable(false);

        switchScene("/fxml/login.fxml");
        stage.getScene().getStylesheets().add("css/login.css");

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
                            switchScene("/fxml/menu.fxml");
                            stage.getScene().getStylesheets().add("css/menu.css");
                            stage.setFullScreen(false);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
                } else {
                    switch (view.getCurrentHandler()) {
                        case GAME -> // show the game
                                Platform.runLater(() -> {
                                    try {
                                        switchSceneResize("/fxml/game.fxml", 1920, 1080);
                                        stage.getScene().getStylesheets().add("css/game.css");
                                        stage.setFullScreen(true);
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                });

                        case LOBBY -> // show the lobby
                                Platform.runLater(() -> {
                                    try {
                                        switchScene("/fxml/lobby.fxml");
                                        stage.getScene().getStylesheets().add("css/lobby.css");
                                        stage.setFullScreen(false);
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

    public static void switchScene(String scenePath) throws IOException{
        Parent root = FXMLLoader.load(GUI.class.getResource(scenePath));
        Scene scene = new Scene(root);

        scene.getRoot().setCache(true);
        scene.getRoot().setCacheHint(CacheHint.SPEED); // Maybe CacheHint.SPEED
        stage.setScene(scene);
        stage.show();
    }

    public static void switchSceneResize(String scenePath, int width, int heigth) throws IOException {
        Parent root = FXMLLoader.load(GUI.class.getResource(scenePath));
        Scene scene = new Scene(root);

        Dimension resolution = Toolkit.getDefaultToolkit().getScreenSize();
        double currentWidth = resolution.getWidth();
        double currentHeight = resolution.getHeight();
        double w = currentWidth/width;
        double h = currentHeight/heigth;
        Scale scale = new Scale(w, h, 0, 0);
        root.getTransforms().add(scale);

        scene.getRoot().setCache(true);
        scene.getRoot().setCacheHint(CacheHint.SPEED); // Maybe CacheHint.SPEED
        stage.setScene(scene);
        stage.show();
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

    public static Stage getStage() {return stage;}

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
