package it.polimi.ingsw.controller.messages;

import it.polimi.ingsw.controller.Game;
import it.polimi.ingsw.controller.LobbyHandler;
import it.polimi.ingsw.controller.LobbyPlayer;
import it.polimi.ingsw.controller.NetworkManager;
import it.polimi.ingsw.model.board.Color;
import it.polimi.ingsw.model.board.Island;
import it.polimi.ingsw.model.board.Students;
import it.polimi.ingsw.utils.exceptions.EmptyBagException;
import it.polimi.ingsw.utils.exceptions.EmptyMovableException;
import it.polimi.ingsw.utils.exceptions.FullLobbyException;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Random;

public class ClientmessageTest {

    @RepeatedTest(10)
    public void RandomTest() throws FullLobbyException, EmptyMovableException, EmptyBagException {
        NetworkManager networkmanager = NetworkManager.createNetworkManager(2);
        LobbyPlayer player1 = new LobbyPlayer("Player 1");
        LobbyPlayer player2 = new LobbyPlayer("Player 2");
        Random rng = new Random();

        //try 100 random messages
        for(int i=0; i<1000; i++){
            int random_message = rng.nextInt(14);
            switch (random_message){
                case 0:
                    //ActivateCharacterMessage
                    PlayerChoicesSerializable playerchoiceserializable = new PlayerChoicesSerializable();
                    ArrayList<Color> students = new ArrayList<>();
                    //insert a random number of students
                    for(int j=0; j<rng.nextInt(4); j++){
                        students.add(Color.getRandomColor());
                    }
                    playerchoiceserializable.setStudent(students);
                    //choose randomly if insert an island
                    if(rng.nextInt(2) == 1){
                        playerchoiceserializable.setIsland(rng.nextInt(15));
                    }
                    ActivateCharacterMessage activatecharactermessage = new ActivateCharacterMessage(playerchoiceserializable);
                    activatecharactermessage.handle(networkmanager, player1);
                    activatecharactermessage.handle(networkmanager, player2);
                    activatecharactermessage.toString();
                case 1:
                    //ChooseCloudMessage
                    //choose a random island position (also out of bound)
                    ChooseCloudMessage choosecloudmessage = new ChooseCloudMessage(rng.nextInt(15)-10);
                    choosecloudmessage.handle(networkmanager, player2);
                    choosecloudmessage.handle(networkmanager, player1);
                    choosecloudmessage.toString();
                case 2:
                    //ChooseWizardMessage
                    //choose a random Wizard number (also out of bound)
                    ChooseWizardMessage choosewizardmessage = new ChooseWizardMessage(rng.nextInt(5)-2);
                    choosewizardmessage.handle(networkmanager, player1);
                    choosewizardmessage.handle(networkmanager, player1);
                    choosewizardmessage.toString();
            }
        }
        /*StartGameMessage startgamemesage = new StartGameMessage(true);
        startgamemesage.handle(networkmanager, player1);
        startgamemesage.handle(networkmanager, player2);*/
    }
}
