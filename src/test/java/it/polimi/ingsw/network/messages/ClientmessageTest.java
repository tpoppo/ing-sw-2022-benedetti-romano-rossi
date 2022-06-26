package it.polimi.ingsw.network.messages;

import it.polimi.ingsw.controller.LobbyPlayer;
import it.polimi.ingsw.network.NetworkManager;
import it.polimi.ingsw.model.board.Color;
import it.polimi.ingsw.utils.exceptions.FullLobbyException;
import it.polimi.ingsw.utils.exceptions.WizardNotAvailableException;
import org.junit.jupiter.api.RepeatedTest;

import java.util.ArrayList;
import java.util.Random;

public class ClientmessageTest {

    @RepeatedTest(5)
    public void RandomTest() throws FullLobbyException, WizardNotAvailableException {
        NetworkManager networkmanager = NetworkManager.createNetworkManager(2);
        ArrayList<LobbyPlayer> players = new ArrayList<>();
        for(int i=0; i<4; i++){
            players.add(new LobbyPlayer("Player "+i));
        }
        Random rng = new Random();
        networkmanager.getLobbyHandler().addPlayer(players.get(0));
        networkmanager.getLobbyHandler().addPlayer(players.get(2));

        networkmanager.getLobbyHandler().chooseWizard(1, players.get(0));
        networkmanager.getLobbyHandler().chooseWizard(2, players.get(1));


        //try 100000 random messages
        for(int i=0; i<100000; i++){
            int random_message = rng.nextInt(11);
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
                    activatecharactermessage.handle(networkmanager, players.get(rng.nextInt(players.size())));
                    activatecharactermessage.toString();
                    break;
                case 1:
                    //ChooseCloudMessage
                    //choose a random island position (also out of bound)
                    ChooseCloudMessage choosecloudmessage = new ChooseCloudMessage(rng.nextInt(15)-10);
                    choosecloudmessage.handle(networkmanager, players.get(rng.nextInt(players.size())));
                    choosecloudmessage.toString();
                    break;
                case 2:
                    //ChooseWizardMessage
                    //choose a random Wizard number (also out of bound)
                    ChooseWizardMessage choosewizardmessage = new ChooseWizardMessage(rng.nextInt(5)-2);
                    choosewizardmessage.handle(networkmanager, players.get(rng.nextInt(players.size())));
                    choosewizardmessage.toString();
                    break;
                case 3:
                    //MoveMotherNatureMessage
                    MoveMotherNatureMessage movemothernaturemessage = new MoveMotherNatureMessage(rng.nextInt()-15);
                    movemothernaturemessage.handle(networkmanager, players.get(rng.nextInt(players.size())));
                    movemothernaturemessage.toString();
                case 4:
                    //MoveStudentMessage
                    MoveStudentMessage movestudentmessage = new MoveStudentMessage(Color.getRandomColor(), rng.nextInt(15)-5);
                    movestudentmessage.handle(networkmanager, players.get(rng.nextInt(players.size())));
                    movestudentmessage.toString();
                case 5:
                    //NextStateMessage
                    NextStateMessage nextstatemessage = new NextStateMessage();
                    nextstatemessage.handle(networkmanager, players.get(rng.nextInt(players.size())));
                    nextstatemessage.toString();
                case 6:
                    PlayAssistantMessage playassistantmessage = new PlayAssistantMessage(rng.nextInt(15));
                    playassistantmessage.handle(networkmanager, players.get(rng.nextInt(players.size())));
                    playassistantmessage.toString();
                case 7:
                    SelectedCharacterMessage selectedcharactermessage = new SelectedCharacterMessage(rng.nextInt(15)-5);
                    selectedcharactermessage.handle(networkmanager, players.get(rng.nextInt(players.size())));
                    selectedcharactermessage.toString();
                case 8:
                    StartGameMessage startgamemessage = new StartGameMessage(rng.nextBoolean());
                    startgamemessage.handle(networkmanager, players.get(rng.nextInt(players.size())));
                    startgamemessage.toString();
                case 9:
                    EndingMessage endingMessage = new EndingMessage();
                    endingMessage.handle(networkmanager, players.get(rng.nextInt(players.size())));
                    endingMessage.toString();
            }
        }
    }
}
