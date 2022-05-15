package it.polimi.ingsw.network.messages;

import it.polimi.ingsw.controller.Game;
import it.polimi.ingsw.controller.GameHandler;
import it.polimi.ingsw.controller.GameState;
import it.polimi.ingsw.controller.LobbyPlayer;
import it.polimi.ingsw.network.*;

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

        gameHandler.setActionCompleted(false);

        switch (gameHandler.getCurrentState()) {
            case ACTIVATE_CHARACTER -> {
                gameHandler.setCurrentState(gameHandler.getSavedState());
                gameHandler.setSavedState(null);
            }
            case PLAY_ASSISTANT -> {
                game.nextTurn();
                if (game.getCurrentPlayer() == null) { // end of the subphase
                    game.endPlanning();
                    gameHandler.setStudentMoves(3);
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
                    gameHandler.setActionCompleted(true);
                }else {
                    gameHandler.setCurrentState(GameState.CHOOSE_CLOUD);
                }
            }
            case CHOOSE_CLOUD -> {
                game.nextTurn();
                gameHandler.setStudentMoves(3);
                if (game.getCurrentPlayer() == null) { // end of the round
                    if (game.checkEndGame()) {
                        gameHandler.setCurrentState(GameState.ENDING);
                        gameHandler.setActionCompleted(true);
                    }else {
                        game.fillClouds();
                        game.beginPlanning();
                        gameHandler.setCurrentState(GameState.PLAY_ASSISTANT);
                    }
                } else gameHandler.setCurrentState(GameState.MOVE_STUDENT);
            }
            case ENDING -> {
                MenuManager menuManager = MenuManager.getInstance();

                for (ConnectionCEO subscriber : network_manager.getSubscribers()) {
                    menuManager.subscribe(subscriber);
                    subscriber.clean();
                }
                network_manager.getSubscribers().clear();

                network_manager.destroy();
                Server.getInstance().deleteNetworkManager(network_manager);
            }
        }

        return StatusCode.OK;
    }

    @Override
    public String toString() {
        return "NextStateMessage{" +
                "message_type=" + message_type +
                '}';
    }
}
