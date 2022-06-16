package it.polimi.ingsw.network.messages;

/**
 * Code return by the {@link ClientMessage} handle method.
 */
public enum StatusCode {
    // everything fine
    OK,
    // handle method not implemented
    NOT_IMPLEMENTED,
    // generic invalid action
    INVALID_ACTION,
    // the message has been called in the wrong state (e.g. it is not your turn)
    WRONG_STATE,
    // the message has been called while it is not your turn
    WRONG_PLAYER,
    // the message has been sent to the wrong handler
    WRONG_HANDLER,
    // the message has not decided. Note: this should never be return by the handle function
    EMPTY,
}
