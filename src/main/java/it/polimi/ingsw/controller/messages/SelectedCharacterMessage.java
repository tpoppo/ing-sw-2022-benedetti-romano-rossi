package it.polimi.ingsw.controller.messages;

import it.polimi.ingsw.controller.Game;
import it.polimi.ingsw.controller.GameHandler;
import it.polimi.ingsw.controller.GameState;
import it.polimi.ingsw.controller.NetworkManager;
import it.polimi.ingsw.controller.responses.ServerResponse;
import it.polimi.ingsw.controller.responses.StatusCode;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.characters.Character;
import it.polimi.ingsw.model.characters.PlayerChoices;
import it.polimi.ingsw.utils.exceptions.BadPlayerChoiceException;

public class SelectedCharacterMessage extends ClientMessage {
    int character_position;
    public SelectedCharacterMessage(int character_position) {
        this.character_position = character_position;
        super.message_type = MessageType.GAME;
    }

    @Override
    public ServerResponse handle(NetworkManager network_manager, Player player) {
        GameHandler gameHandler = network_manager.getGameHandler();
        Game game = gameHandler.getModel();

        // Invalid state. It ust be (current_state=ACTIVATE_CHARACTER, action_completed=False)
        if(gameHandler.getCurrentState() != GameState.ACTIVATE_CHARACTER || gameHandler.isActionCompleted()){
            return new ServerResponse(StatusCode.BAD_REQUEST, null); // TODO: viewContent missing
        }

        // Invalid player. Different players (from model and from socket)
        Player current_player = game.getCurrentPlayer();
        if(current_player == null || player != current_player) {
            return new ServerResponse(StatusCode.BAD_REQUEST, null); // TODO: viewContent missing
        }

        // The game must be in expert mode
        if(!game.getExpertMode()){
            return new ServerResponse(StatusCode.BAD_REQUEST, null); // TODO: viewContent missing
        }

        // no other characters active
        for(Character character : game.getCharacters()){
            if(character.isActivated()){
                return new ServerResponse(StatusCode.BAD_REQUEST, null); // TODO: viewContent missing
            }
        }

        // Invalid character_position value
        if(character_position < 0 || character_position >= game.getCharacters().size()){
            return new ServerResponse(StatusCode.BAD_REQUEST, null); // TODO: viewContent missing
        }
        Character character = game.getCharacters().get(character_position);

        // check if the player has enough money
        if(character.getCost() > player.getCoins()){
            return new ServerResponse(StatusCode.BAD_REQUEST, null); // TODO: viewContent missing
        }

        gameHandler.setSelectedCharacter(character);

        gameHandler.setActionCompleted(true);
        return new ServerResponse(StatusCode.OK, null); // TODO: viewContent missing
    }

}
