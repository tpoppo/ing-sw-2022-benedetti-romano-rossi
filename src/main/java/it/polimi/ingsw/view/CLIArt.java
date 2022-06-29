package it.polimi.ingsw.view;

import it.polimi.ingsw.client.ClientSocket;
import it.polimi.ingsw.controller.Game;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.board.Color;
import it.polimi.ingsw.model.board.Island;
import it.polimi.ingsw.model.board.Students;
import it.polimi.ingsw.utils.Constants;
import it.polimi.ingsw.utils.Pair;
import org.fusesource.jansi.Ansi;

import static org.fusesource.jansi.Ansi.ansi;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.*;

/**
 * This class is used to show an alternative version of the client cli)
 */
public class CLIArt extends CLI {
    /**
     * List of constants for the elements position.
     */
    private final Pair<Integer, Integer> STD_CURSOR_POSITION = new Pair<>(53, 1);
    private final Pair<Integer, Integer> STD_USERNAME_POSITION = new Pair<>(2, 80);
    private final Pair<Integer, Integer> STD_BAG_POSITION = new Pair<>(3, 80);
    private final Pair<Integer, Integer> STD_PLAYERS_POSITION = new Pair<>(4, 80);
    private final Pair<Integer, Integer> STD_BOARD_POSITION = new Pair<>(2, 177);
    private final Pair<Integer, Integer> STD_CHARACTER_POSITION = new Pair<>(43, 177);
    private final Pair<Integer, Integer> STD_STATUS_POSITION = new Pair<>(2, 110);
    private final Pair<Integer, Integer> STD_CLOUDS_POSITION = new Pair<>(10, 1);
    private final Pair<Integer, Integer> STD_ISLANDS_POSITION = new Pair<>(19, 1);
    private final Pair<Integer, Integer> STD_ASSISTANTS_POSITION = new Pair<>(7, 131);

    private final Pair<Integer, Integer> ISLAND_SHAPE = new Pair<>(8, 25);
    private final Pair<Integer, Integer> CLOUD_SHAPE = new Pair<>(8, 25);

    public CLIArt(ClientSocket client_socket, PrintStream out, InputStream read_stream) {
        super(client_socket, out, read_stream);
    }

    public CLIArt(ClientSocket client_socket) {
        this(client_socket, System.out, System.in);
    }


    /**
     * It shows the current view.
     * It assumes the player is in the state game
     */
    @Override
    protected void printGame(){
        print(ansi().a(Constants.ERIANTYS), 1, 1);

        Game model = view.getGameHandler().getModel();

        // Banner length is 63
        print(ansi().bg(Ansi.Color.WHITE).fg(Ansi.Color.BLACK).a(username).reset(), STD_USERNAME_POSITION);
        print(drawPlayers(), STD_PLAYERS_POSITION);
        print(drawState(), STD_STATUS_POSITION);
        print(drawBag(), STD_BAG_POSITION);

        printIslands();
        printClouds();

        print(drawBoard(), STD_BOARD_POSITION);

        print(drawAssistants(), STD_ASSISTANTS_POSITION);

        if(model.getExpertMode()) {
            print(drawCharacters(), STD_CHARACTER_POSITION);
        }
        print(ansi().eraseLine().a("> ").reset(), STD_CURSOR_POSITION);

        // print server errors
        if(errorMessage != null) printErrorRelative();
    }

    /**
     * It shows the clouds
     */
    private void printClouds(){
        Game model = view.getGameHandler().getModel();

        int cnt = 0;
        for(Students students : model.getClouds()){
            String cloudString = drawCloud(students);
            print("Cloud: "+cnt, STD_CLOUDS_POSITION.getFirst(), (1+CLOUD_SHAPE.getSecond())*cnt+STD_CLOUDS_POSITION.getSecond());
            print(cloudString, STD_CLOUDS_POSITION.getFirst()+1, (1+CLOUD_SHAPE.getSecond())*cnt+STD_CLOUDS_POSITION.getSecond());
            cnt++;
        }
    }

    /**
     * It shows the islands
     */
    private void printIslands(){
        Game model = view.getGameHandler().getModel();

        int divisor = model.getIslands().size();
        for(int i=2; i<=model.getIslands().size(); i++) {
            if(model.getIslands().size()%i == 0){
                divisor = model.getIslands().size()/i;
                break;
            }
        }
        divisor = Math.min(divisor, 7);

        int cnt = 0;
        for(Island island : model.getIslands()){
            String islandString = drawIsland(island);
            String owner = island.getOwner() == null ? "" : "- " + island.getOwner().getUsername();
            print("%d %s".formatted(cnt, owner),
                    STD_ISLANDS_POSITION.getFirst() + (1+ISLAND_SHAPE.getFirst())*(cnt/divisor),
                    STD_ISLANDS_POSITION.getSecond()+(1+ISLAND_SHAPE.getSecond())*(cnt%divisor));
            print(islandString,
                    STD_ISLANDS_POSITION.getFirst() + (1+ISLAND_SHAPE.getFirst())*(cnt/divisor) + 1,
                    STD_ISLANDS_POSITION.getSecond()+(1+ISLAND_SHAPE.getSecond())*(cnt%divisor));
            cnt++;
        }
    }

    /**
     * It returns a string that shows the schoolboard
     * @return the schoolboard ascii art
     */
    protected String drawBoard(){
        Game model = view.getGameHandler().getModel();
        Player player;

        if(schoolBoardPlayerUsername == null){
            player = model.usernameToPlayer(username);
        }else{
            player = model.usernameToPlayer(schoolBoardPlayerUsername);
        }
        Students entranceStudents = player.getSchoolBoard().getEntranceStudents();
        Students diningStudents = player.getSchoolBoard().getDiningStudents();
        StringBuilder boardStr = new StringBuilder();


        boardStr.append(ansi().bold().a("SCHOOLBOARD").reset());

        // displays the username of the owner if it's not the user's
        if(!username.equals(schoolBoardPlayerUsername))
            boardStr.append(" (").append(schoolBoardPlayerUsername).append(")");

        boardStr.append(Constants.NEWLINE);

        int numTowers = player.getSchoolBoard().getNumTowers();

        boardStr.append("Towers: ").append(numTowers).append(Constants.NEWLINE);

        // draw the entrance
        boardStr.append("Entrance: ").append(Constants.NEWLINE).append(Constants.NEWLINE).append(Constants.NEWLINE);
        int position = 0;
        for(Color studentColor : entranceStudents.keySet()){
            for(int i=0; i<entranceStudents.get(studentColor); i++){
                position++;
                if(position % 2 == 0){
                    boardStr.append(ansi().cursorMove(+1, +1).bg(Ansi.Color.valueOf(studentColor.toString())).a("  ").reset());
                }else{
                    boardStr.append(ansi().cursorMove(+1, -1).bg(Ansi.Color.valueOf(studentColor.toString())).a("  ").reset());
                }
            }
        }
        boardStr.append(Constants.NEWLINE).append(Constants.NEWLINE).append(Constants.NEWLINE);

        // draw the dining room
        boardStr.append("Dining room:").append(Constants.NEWLINE).append(Constants.NEWLINE).append(ansi().cursorRight(-3));

        for(int i=0; i<Game.MAX_DINING_STUDENTS; i++) {
            for(int r=0; r<3; r++){
                if(r == 0){
                    boardStr.append("%2d: ".formatted(i+1));
                } else {
                    boardStr.append("    ");
                }
                for(Color color : Color.values()){
                    String value;
                    if(r == 0) {
                        if (diningStudents.get(color) > i)
                            value = "  S  ";
                        else if (i > 0 && (i - 2) % 3 == 0) {
                            value = "  C  ";
                        } else {
                            value = "     ";
                        }
                    }else {
                        value = "     ";
                    }
                    boardStr.append(ansi().fg(Ansi.Color.BLACK).bg(Ansi.Color.valueOf(color.toString())).a(value).reset());
                }
                boardStr.append(Constants.NEWLINE).append(ansi().cursorRight(-3));
            }
        }

        return boardStr.toString();
    }

    /**
     * It generates a boolean mask used to generate clouds and islands
     * @param row number of rows
     * @param column number of columns
     * @param dim size of the mask
     * @return the mask
     */
    private boolean[][] generateMaskTile(int row, int column, int dim){
        boolean[][] mat = new boolean[row][column];

        for(int i=-row/2; i<(1+row)/2; i++){
            for(int j=-column/2; j<(1+column)/2; j++){
                mat[row/2+i][column/2+j] = 2*i*i+j*j/2 <= dim;
            }
        }
        return mat;
    }

    /**
     * It returns the cloud ascii art
     * @param cloud the cloud to show
     * @return the ascii art of the cloud
     */
    private String drawCloud(Students cloud){
        return drawTile(cloud, 0,0,  false, 20,
                Ansi.Color.WHITE, CLOUD_SHAPE.getFirst(), CLOUD_SHAPE.getSecond());
    }

    /**
     * It returns the island ascii art
     * @param island the island to show
     * @return the ascii art of the island
     */
    private String drawIsland(Island island){
        // dim must be >= 36 (in the worst case)
        return drawTile(island.getStudents(), island.getNumTowers(), island.getNoEntryTiles(),
                        island.hasMotherNature(), 12 + 5*island.getNumIslands(),
                Ansi.Color.GREEN, ISLAND_SHAPE.getFirst(), ISLAND_SHAPE.getSecond());
    }

    /**
     * It returns an ascii art of a tile given the items to show.
     * @param students students to show
     * @param num_towers number of towers
     * @param no_entry_tiles number of no entry tiles
     * @param has_mother_nature whether it has mother nature
     * @param dim size of the tile
     * @param bg_color background color
     * @param row number of rows
     * @param column number of columns
     * @return the ascii art
     */
    private String drawTile(Students students, int num_towers, int no_entry_tiles, boolean has_mother_nature, int dim, Ansi.Color bg_color, int row, int column){
        boolean[][] mask = generateMaskTile(row, column, dim);
        String[][] canvas = new String[mask.length][mask[0].length];
        int position = 0;

        // add students
        if(students != null){
            for(Map.Entry<Color, Integer> entry : students.entrySet()) {
                Color key = entry.getKey();
                int value = entry.getValue();
                while(value > 0){
                    // first available position
                    String s;
                    while(!mask[position/column][position%column]) position++;
                    if(value >= 5) {
                        value -= 5;
                        s = ansi().fg(Ansi.Color.valueOf(key.toString())).a(5).reset().toString();
                    } else {
                        value--;
                        s = ansi().fg(Ansi.Color.valueOf(key.toString())).a(1).reset().toString();
                    }
                    canvas[position/column][position%column] = s;
                    position++;
                }
            }
        }

        // add the towers
        for(int i=0; i<num_towers; i++){
            // first available position
            while(!mask[position/column][position%column]) position++;
            canvas[position/column][position%column] = ansi().fg(Ansi.Color.BLACK).a("T").reset().toString();
            position++;
        }

        // ad no entry tiles
        for(int i=0; i<no_entry_tiles; i++){
            // first available position
            while(!mask[position/column][position%column]) position++;
            canvas[position/column][position%column] = ansi().fg(Ansi.Color.BLACK).a("X").reset().toString();
            position++;
        }

        // add mother nature
        if(has_mother_nature){
            // first available position
            while(!mask[position/column][position%column]) position++;
            canvas[position/column][position%column] = ansi().fg(Ansi.Color.RED).a("M").reset().toString();
            position++;
        }

        StringBuilder stringBuilder = new StringBuilder();
        for(int i=0; i<canvas.length; i++){
            for(int j=0; j<canvas[i].length; j++){
                if(mask[i][j]){ // is it inside the tile mask?
                    if(canvas[i][j] == null) {
                        stringBuilder.append(ansi().bgBright(bg_color).a(" ").reset());
                    } else {
                        stringBuilder.append(ansi().bgBright(bg_color).a(canvas[i][j]).reset());
                    }
                } else {
                    stringBuilder.append(" ");
                }
            }
            stringBuilder.append(Constants.NEWLINE);
        }
        return stringBuilder.toString();
    }

}
