package it.polimi.ingsw.view.guicontroller;

import it.polimi.ingsw.view.GUI;
import it.polimi.ingsw.view.viewcontent.ViewContent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

public class GameController implements Initializable {

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ViewContent view = GUI.getView();
        System.out.println(view);
    }
}
