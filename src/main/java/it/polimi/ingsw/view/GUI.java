package it.polimi.ingsw.view;

import it.polimi.ingsw.client.ClientSocket;
import it.polimi.ingsw.view.viewcontent.ViewContent;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class GUI extends Application {
    private static ViewContent view;
    private static ClientSocket client_socket;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        // Group root = FXMLLoader.load(getClass().getResource("/assets/fxml/login.fxml"));
        Parent root = FXMLLoader.load(getClass().getResource("/assets/fxml/lobby.fxml"));

        Scene scene = new Scene(root);

        stage.setFullScreen(true);
        stage.setScene(scene);
        stage.show();
        startViewContentUpdates(stage);
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
                view = client_socket.getView();

                switch(view.getCurrentHandler()){
                    case GAME -> {

                    }

                    case LOBBY -> {
                        Group root = null;
                        try {
                            root = FXMLLoader.load(getClass().getResource("/assets/fxml/lobby.fxml"));
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        Scene scene = new Scene(root);
                        stage.setScene(scene);
                    }
                }
            }
        });
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
}
