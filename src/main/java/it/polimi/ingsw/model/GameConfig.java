package it.polimi.ingsw.model;

import java.io.Serial;
import java.io.Serializable;

/**
 * Class GameConfig represents the configuration of the game.
 */
public class GameConfig implements Serializable {
    @Serial
    private static final long serialVersionUID = 6501470373981175197L;
    final public int CLOUD_SPACE;
    final public int NUM_TOWERS;
    final public int NUM_CLOUDS;
    final public int NUM_ASSISTANTS;
    final public int NUM_ENTRANCE_STUDENTS;
    final public int NUM_STUDENTS_MOVES;
    final public int NUM_ISLANDS;
    final public int NUM_CHARACTERS;

    /**
     * Constructor, creates a GameConfiguration with the given parameters.
     *
     * @param cloud_space the number of students on top of the clouds.
     * @param num_towers the number of towers for each player.
     * @param num_clouds the number of clouds in game.
     * @param num_assistants the number of assistants in game.
     * @param num_entrance_students the number of entrance students for each schoolboard.
     * @param num_students_moves the number of students moves for the "move student" phase.
     * @param num_islands the number of islands in game.
     * @param num_characters the number of character in game.
     */
    public GameConfig(int cloud_space, int num_towers, int num_clouds, int num_assistants, int num_entrance_students, int num_students_moves, int num_islands, int num_characters) {
        CLOUD_SPACE = cloud_space;
        NUM_TOWERS = num_towers;
        NUM_CLOUDS = num_clouds;
        NUM_ASSISTANTS = num_assistants;
        NUM_ENTRANCE_STUDENTS = num_entrance_students;
        NUM_STUDENTS_MOVES = num_students_moves;
        NUM_ISLANDS = num_islands;
        NUM_CHARACTERS = num_characters;
    }
}
