package it.polimi.ingsw.client;

/**
 * Defines in which situation the command can be used
 */
public enum CommandType {
    GAME, // It can be used during the game
    LOBBY, // It can be used while you are in the lobby
    MENU, // It can be used while you are in the menu
    GENERAL // It can be used in any moment
}
