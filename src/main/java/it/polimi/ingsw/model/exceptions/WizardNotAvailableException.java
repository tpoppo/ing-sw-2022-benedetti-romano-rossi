package it.polimi.ingsw.model.exceptions;

public class WizardNotAvailableException extends GameException{
    public WizardNotAvailableException() {
        super("WizardNotAvailableException: Wizard is not in the available_wizards list");
    }
}
