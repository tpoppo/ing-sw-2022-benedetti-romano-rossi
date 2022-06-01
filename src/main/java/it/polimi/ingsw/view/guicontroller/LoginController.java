package it.polimi.ingsw.view.guicontroller;

import it.polimi.ingsw.client.ClientSocket;
import it.polimi.ingsw.view.GUI;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class LoginController {
    @FXML
    private TextField usernameField;
    @FXML
    private Label errorLabel;

    public void login(){
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

    public void checkKeyPressed(KeyEvent keyEvent){
        if(keyEvent.getCode().equals(KeyCode.ENTER))
            login();
    }
}
