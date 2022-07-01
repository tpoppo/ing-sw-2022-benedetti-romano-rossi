package it.polimi.ingsw.network.messages;

import it.polimi.ingsw.controller.Game;
import it.polimi.ingsw.controller.GameHandler;
import it.polimi.ingsw.controller.GameState;
import it.polimi.ingsw.controller.LobbyPlayer;
import it.polimi.ingsw.network.*;
/**
 * This message is used to confirm an action and to go to the next state
 * It can be used while you are in game.
 */
public class NextStateMessage extends ClientMessage {
    public NextStateMessage() {
        super.message_type = MessageType.GAME;
    }

    @Override
    public StatusCode handle(NetworkManager network_manager, LobbyPlayer lobby_player) {
        StatusCode status_code = preambleGameCheck(network_manager, lobby_player, null, true);
        if(status_code != StatusCode.EMPTY) return status_code;

        GameHandler gameHandler = network_manager.getGameHandler();
        Game game = gameHandler.getModel();
        boolean next_action_completed = false;

        switch (gameHandler.getCurrentState()) {
            case ACTIVATE_CHARACTER -> {
                gameHandler.setCurrentState(gameHandler.getSavedState());
                gameHandler.setSavedState(null);
                next_action_completed = gameHandler.isSavedActionCompleted();
            }
            case PLAY_ASSISTANT -> {
                game.nextTurn();
                if (game.getCurrentPlayer() == null) { // end of the subphase
                    game.endPlanning();
                    gameHandler.setStudentMoves(game.getGameConfig().NUM_STUDENTS_MOVES);
                    gameHandler.setCurrentState(GameState.MOVE_STUDENT);
                }
            }
            case MOVE_STUDENT -> {
                gameHandler.setStudentMoves(gameHandler.getStudentMoves() - 1);
                if (gameHandler.getStudentMoves() == 0)
                    gameHandler.setCurrentState(GameState.MOVE_MOTHER_NATURE);
            }
            case MOVE_MOTHER_NATURE -> {
                if (game.checkVictory()) {
                    gameHandler.setCurrentState(GameState.ENDING);
                }else {
                    gameHandler.setCurrentState(GameState.CHOOSE_CLOUD);
                }
            }
            case CHOOSE_CLOUD -> {
                game.nextTurn();

                game.getActiveCharacter().ifPresent(character -> character.deactivate(game));
                gameHandler.setSelectedCharacter(null);

                gameHandler.setStudentMoves(game.getGameConfig().NUM_STUDENTS_MOVES);

                if (game.getCurrentPlayer() == null) { // end of the round
                    if (game.checkEndGame()) {
                        gameHandler.setCurrentState(GameState.ENDING);
                    }else {
                        game.fillClouds();
                        game.beginPlanning();
                        gameHandler.setCurrentState(GameState.PLAY_ASSISTANT);
                    }
                } else gameHandler.setCurrentState(GameState.MOVE_STUDENT);
            }
            default -> {
                network_manager.addErrorMessage(lobby_player, "You cannot pass in this situation.");
                return StatusCode.INVALID_ACTION;
            }
        }

        gameHandler.setActionCompleted(next_action_completed);
        return StatusCode.OK;
    }

    @Override
    public String toString() {
        return "NextStateMessage{" +
                "message_type=" + message_type +
                '}';
    }
}
