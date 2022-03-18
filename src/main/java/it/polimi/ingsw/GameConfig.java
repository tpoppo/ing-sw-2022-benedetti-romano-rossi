package it.polimi.ingsw;

public class GameConfig {
    final public int CLOUD_SPACE;
    final public int NUM_TOWERS;
    final public int NUM_CLOUDS;
    final public int NUM_ASSISTANTS;
    final public int NUM_ENTRANCE_STUDENTS;
    final public int NUM_STUDENTS_MOVES;
    final public int NUM_ISLANDS;

    public GameConfig(int cloud_space, int num_towers, int num_clouds, int num_assistants, int num_entrance_students, int num_students_moves, int num_islands) {
        CLOUD_SPACE = cloud_space;
        NUM_TOWERS = num_towers;
        NUM_CLOUDS = num_clouds;
        NUM_ASSISTANTS = num_assistants;
        NUM_ENTRANCE_STUDENTS = num_entrance_students;
        NUM_STUDENTS_MOVES = num_students_moves;
        NUM_ISLANDS = num_islands;
    }
}
