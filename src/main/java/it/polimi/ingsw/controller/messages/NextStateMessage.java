package it.polimi.ingsw.controller.messages;

import it.polimi.ingsw.controller.*;

import java.util.Optional;

public class NextStateMessage extends ClientMessage {
    public NextStateMessage() {
        super.message_type = MessageType.GAME;
    }

    @Override
    public StatusCode handle(NetworkManager network_manager, LobbyPlayer lobby_player) {
        StatusCode status_code = preamble_game_check(network_manager, lobby_player, GameState.CHOOSE_CLOUD);
        if(status_code != StatusCode.EMPTY) return status_code;

        GameHandler gameHandler = network_manager.getGameHandler();
        Game game = gameHandler.getModel();

        gameHandler.setActionCompleted(false);

        switch(gameHandler.getCurrentState()){
            case ACTIVATE_CHARACTER:
                gameHandler.setCurrentState(gameHandler.getSavedState());
                gameHandler.setSavedState(null);
                break;

            case PLAY_ASSISTANT:
                if(game.getPlayers() == null){
                    game.endPlanning();
                    gameHandler.setCurrentState(GameState.MOVE_STUDENT);
                }
                break;

            case MOVE_STUDENT:
                if(gameHandler.getStudentMoves() == 0){
                    game.nextTurn();
                    gameHandler.setStudentMoves(3);
                    if(game.getPlayers() == null){
                        gameHandler.setCurrentState(GameState.MOVE_MOTHER_NATURE);
                    }
                }else{
                    gameHandler.setStudentMoves(gameHandler.getStudentMoves()-1);
                }
                break;

            case MOVE_MOTHER_NATURE:
                if(game.getPlayers() == null){
                    gameHandler.setCurrentState(GameState.CHOOSE_CLOUD);
                }
                break;

            case CHOOSE_CLOUD:
                if(game.getPlayers() == null){ // end of the turn
                    /* TODO:
                        if (game.checkEndGame()) ...
                     */
                    game.fillClouds();
                    game.beginPlanning();
                    gameHandler.setCurrentState(GameState.PLAY_ASSISTANT);
                }
                break;

        }

        gameHandler.setActionCompleted(true);
        return StatusCode.OK;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
