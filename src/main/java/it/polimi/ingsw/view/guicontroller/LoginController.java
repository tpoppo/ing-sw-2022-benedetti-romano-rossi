package it.polimi.ingsw.view.guicontroller;

import it.polimi.ingsw.client.ClientSocket;
import it.polimi.ingsw.view.GUI;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;

import java.io.IOException;

import static it.polimi.ingsw.view.GUI.switchScene;

public class LoginController {
    @FXML
    private TextField usernameField;
    @FXML
    private Label errorLabel;

    public void login(ActionEvent event){
        String username = usernameField.getText();

        System.out.println("Username: " + username);

        ClientSocket clientSocket = GUI.getClientSocket();

        if(username.isBlank()){
            errorLabel.setText("Invalid username");
            errorLabel.setVisible(true);
            return;
        }

        if(!clientSocket.login(username)){
            errorLabel.setText("Username already taken!");
            errorLabel.setVisible(true);
            return;
        }

        GUI.setUsername(username);
    }
}
