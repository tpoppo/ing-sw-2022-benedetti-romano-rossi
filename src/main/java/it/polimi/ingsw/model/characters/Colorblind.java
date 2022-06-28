package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.controller.Game;
import it.polimi.ingsw.model.board.Color;
import it.polimi.ingsw.utils.exceptions.BadPlayerChoiceException;

import java.io.Serial;
public class Colorblind extends Character{
    @Serial
    private static final long serialVersionUID = -3440315359225311324L;

    public Colorblind() {
        super(3);
        setDescription("""
                Choose a color of Student: during the influence calculation this turn, that color adds no influence.

                Requirements: <color of the student>""");
    }

    @Override
    public Requirements require() {
        return Requirements.STUDENT_COLOR;
    }

    @Override
    protected void onActivation(Game game, PlayerChoices playerChoices) throws BadPlayerChoiceException {
        Color chosen_color = playerChoices.getStudent().get(0);

        game.getGameModifiers().setInhibitColor(chosen_color);
    }

    @Override
    protected void onDeactivation(Game game){
        game.getGameModifiers().setInhibitColor(null);
    }

}
