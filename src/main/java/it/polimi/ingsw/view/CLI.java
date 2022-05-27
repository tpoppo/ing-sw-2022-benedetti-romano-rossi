package it.polimi.ingsw.view;

import it.polimi.ingsw.client.ClientSocket;
import it.polimi.ingsw.client.Command;
import it.polimi.ingsw.client.CommandHandler;
import it.polimi.ingsw.client.CommandType;
import it.polimi.ingsw.controller.*;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.board.*;
import it.polimi.ingsw.model.characters.Character;
import it.polimi.ingsw.network.messages.EndingMessage;
import it.polimi.ingsw.utils.Constants;
import it.polimi.ingsw.utils.Pair;
import it.polimi.ingsw.utils.ReducedLobby;
import it.polimi.ingsw.view.viewcontent.ViewContent;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static org.fusesource.jansi.Ansi.ansi;

public class CLI {
    final protected Logger LOGGER = Logger.getLogger(getClass().getName());
    final protected ClientSocket client_socket;
    final protected PrintStream out;
    final protected Scanner read_stream;
    protected ViewContent view;
    protected String username;
    protected String schoolBoardPlayerUsername;
    protected String errorMessage;
    private boolean playing;

    private final Pair<Integer, Integer> STD_CURSOR_POSITION = new Pair<>(53, 1);
    private final Pair<Integer, Integer> STD_USERNAME_POSITION = new Pair<>(6, 109);
    private final Pair<Integer, Integer> STD_PLAYERS_POSITION = new Pair<>(8, 109);
    private final Pair<Integer, Integer> STD_BOARD_POSITION = new Pair<>(35, 11);
    private final Pair<Integer, Integer> STD_CHARACTER_POSITION = new Pair<>(28, 60);
    private final Pair<Integer, Integer> STD_STATUS_POSITION = new Pair<>(15, 11);
    private final Pair<Integer, Integer> STD_CLOUDS_POSITION = new Pair<>(22, 60);
    private final Pair<Integer, Integer> STD_ISLANDS_POSITION = new Pair<>(20, 11);
    private final Pair<Integer, Integer> STD_ASSISTANTS_POSITION = new Pair<>(20, 109);
    private final Pair<Integer, Integer> STD_ENDING_POSITION = new Pair<>(25, 25);
    private final Pair<Integer, Integer> STD_BAG_POSITION = new Pair<>(20, 60);

    public CLI(ClientSocket client_socket, PrintStream out, InputStream read_stream) {
        this.client_socket = client_socket;
        this.out = out;
        this.read_stream = new Scanner(new BufferedInputStream(read_stream));
        CommandHandler.createCommands();
        playing = true;
    }
    public CLI(ClientSocket client_socket) {
        this(client_socket, System.out, System.in);
    }

    public void run(){
        AnsiConsole.systemInstall();

        String username;

        clearScreen();
        out.println(Constants.ERIANTYS);
        out.println(Constants.AUTHORS);
        out.println();

        out.println(ansi().fg(Ansi.Color.GREEN).a("LOGIN").reset());
        out.print("Username: ");

        username = read_stream.nextLine();
        while(!client_socket.login(username)) {
            // TODO: better error handling
            out.print(ansi().fgBrightRed().a("Username already taken").reset());
            out.print(ansi().cursorUpLine().eraseLine().a("Username: "));
            username = read_stream.nextLine();
        }
        out.println("Logged in");

        this.username = username;

        startRendering();

        while(client_socket.isOpened()){
            playing = true;
            this.schoolBoardPlayerUsername = username; // sets own username for later displaying of board

            while(playing && client_socket.isOpened()){
                getInput();
            }
            this.errorMessage = null;

            // last input
            client_socket.send(new EndingMessage());

            LOGGER.log(Level.INFO, "isOpened: {0}", new Object[]{client_socket.isOpened()});
        }
        // FIXME: this is the end.
        System.exit(0);
    }

    private void getInput(){
        read_stream.reset();
        String input = read_stream.nextLine();
        this.errorMessage = CommandHandler.sendInput(input, client_socket, this);

        if(errorMessage != null) printClientError(); // for clientside errors
    }

    /**
     * Clears the screen.
     */
    protected void clearScreen(){
        // clear the console
        out.println(ansi().cursor(0, 0).eraseScreen());
        out.flush();
    }

    private void startRendering(){
        new Thread(() -> {
            while(true){
                synchronized (client_socket.mutex){
                    while(client_socket.getView() == null) {
                        try {
                            client_socket.mutex.wait();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }

                this.view = client_socket.getView();
                // FIXME: synchronized for view usage?
                // if there's an error message, it overwrites the current one
                if(view.getErrorMessage() != null)
                    this.errorMessage = view.getErrorMessage();

                LOGGER.log(Level.FINE, "Rendered view: {0}", view);

                clearScreen();

                if(view.getCurrentHandler() == null){ // we are in the menu
                    printMenu();
                } else{
                    switch (view.getCurrentHandler()) {
                        case LOBBY -> // we are in the lobby
                                printLobby();
                        case GAME -> { // we are in the game
                            if(view.getGameHandler().getCurrentState().equals(GameState.ENDING)){
                                playing = false;
                                print(drawEndingScreen(), STD_ENDING_POSITION);
                            }
                            else printGame();
                        }
                    }
                }

                synchronized (client_socket.mutex){
                    client_socket.setView(null);
                    client_socket.mutex.notifyAll();
                }
            }
        }).start();
    }

    protected void printMenu() {
        out.println(Constants.MENU);
        ArrayList<ReducedLobby> reduced_lobbies = view.getLobbies();

        if(!reduced_lobbies.isEmpty())
            out.println(ansi().bold().a("Available lobbies:").reset());
        else out.println(ansi().fgRed().a("No lobbies available!").reset());

        for(ReducedLobby reduced_lobby : reduced_lobbies){
            out.printf("Lobby %d: Slots: %d/%d\n", reduced_lobby.getID(), reduced_lobby.getNumPlayer(), reduced_lobby.getMaxPlayers());
        }
        out.println();

        List<Command> menuCommands = CommandHandler.getCommands().stream().filter(command -> command.getCommandType().equals(CommandType.MENU)).toList();
        menuCommands.forEach(command -> out.print(command.getCommandInfo() + "\n"));

        out.print(ansi().cursorDown(2).a("> "));

        if(errorMessage != null) printErrorRelative();
    }

    protected void printLobby(){
        out.println(Constants.LOBBY);

        LobbyHandler lobby_handler = view.getLobbyHandler();
        ArrayList<LobbyPlayer> players = lobby_handler.getPlayers();

        // prints the lobby id
        out.println(ansi().bg(Ansi.Color.WHITE).fg(Ansi.Color.BLACK).a("Lobby ID: " + lobby_handler.ID).reset());
        out.println();
        out.println();

        // print the list of the players in the lobby
        out.printf("Players: %d/%d\n", players.size(), lobby_handler.getMaxPlayers());
        for(LobbyPlayer lobby_player : players){
            Integer wizard = lobby_player.getWizard();
            out.printf("%s (Wizard: %s)\n", lobby_player.getUsername().equals(username) ?
                    ansi().bg(Ansi.Color.WHITE).fg(Ansi.Color.BLACK).a(username).reset() : lobby_player.getUsername(),
                    wizard
            );
        }
        out.println();

        List<Command> lobbyCommands = CommandHandler.getCommands().stream().filter(command -> command.getCommandType().equals(CommandType.LOBBY)).toList();
        lobbyCommands.forEach(command -> out.print(command.getCommandInfo() + "\n"));

        out.print(ansi().cursorDownLine(2).a("> "));

        if(errorMessage != null) printErrorRelative();
    }

    protected void printGame(){
        print(ansi().a(Constants.ERIANTYS.replaceAll("\n", Constants.NEWLINE)), 5, 10);

        Game model = view.getGameHandler().getModel();

        print(ansi().bg(Ansi.Color.WHITE).fg(Ansi.Color.BLACK).a(username).reset(), STD_USERNAME_POSITION);
        print(drawBag(), STD_BAG_POSITION);
        print(drawPlayers(), STD_PLAYERS_POSITION);
        print(drawState(), STD_STATUS_POSITION);
        print(drawIslands(), STD_ISLANDS_POSITION);
        print(drawClouds(), STD_CLOUDS_POSITION);
        print(drawAssistants(), STD_ASSISTANTS_POSITION);
        printBoards();

        if(model.getExpertMode()) {
            print(drawCharacters(), STD_CHARACTER_POSITION);
        }

        print(ansi().eraseLine().a("> ").reset(), STD_CURSOR_POSITION);

        // print server errors
        if(errorMessage != null) printErrorRelative();
    }

    protected void printErrorRelative(){
        out.print(ansi().cursorDownLine(2).eraseLine());
        out.print(ansi().fgBrightRed().a("ERROR: " + errorMessage).reset());
        out.print(ansi().cursorUpLine(2).cursorRight(2).eraseLine());
    }

    private void printClientError(){
        out.print(ansi().cursorDownLine(1).eraseLine());
        out.print(ansi().fgBrightRed().a("ERROR: " + errorMessage).reset());
        out.print(ansi().cursorUpLine(2).cursorRight(2).eraseLine());
    }

    protected String drawBag(){
        StringBuilder bagStr = new StringBuilder();

        Game model = view.getGameHandler().getModel();

        bagStr.append(ansi().bold().a("BAG: ").reset());
        bagStr.append(model.getBag().capacity());

        return bagStr.toString();
    }

    protected String drawPlayers(){
        StringBuilder playersStr = new StringBuilder();

        Game model = view.getGameHandler().getModel();

        playersStr.append(ansi().bold().a("PLAYERS").reset()).append(Constants.NEWLINE);

        int count = 0;
        ArrayList<Player> players = model.getPlayers();
        for(Player player : players) {
            playersStr.append(count).append(": ").append(player.getUsername()).append(Constants.NEWLINE);

            count++;
        }

        return playersStr.toString();
    }

    protected String drawState(){
        StringBuilder stateText = new StringBuilder();

        GameHandler gameHandler = view.getGameHandler();
        Game model = gameHandler.getModel();

        if(model.getCurrentPlayer().getUsername().equals(username))
            stateText.append(ansi().bold().fg(Ansi.Color.GREEN).a("IT'S YOUR TURN!").reset());
        else
            stateText.append(ansi()
                    .bold()
                    .a("TURN: ")
                    .reset()
                    .a(model.getCurrentPlayer().getUsername())
            );
        stateText.append(Constants.NEWLINE);

        String instruction = "null";
        StringBuilder availableCommands = new StringBuilder();

        List<Command> gameCommands = CommandHandler.getCommands().stream().filter(command -> command.getCommandType().equals(CommandType.GAME)).toList();

        switch (gameHandler.getCurrentState()){
            case PLAY_ASSISTANT -> {
                instruction = "Play an assistant: ";
                gameCommands.stream()
                        .filter(command -> command.getAdmittedStates().contains(GameState.PLAY_ASSISTANT))
                        .map(command -> command.getSimpleInfo() + Constants.NEWLINE)
                        .forEach(availableCommands::append);
            }
            case CHOOSE_CLOUD -> {
                instruction = "Choose a cloud: ";
                gameCommands.stream()
                        .filter(command -> command.getAdmittedStates().contains(GameState.CHOOSE_CLOUD))
                        .map(command -> command.getSimpleInfo() + Constants.NEWLINE)
                        .forEach(availableCommands::append);
            }
            case MOVE_MOTHER_NATURE -> {
                instruction = "Move mother nature: ";
                gameCommands.stream()
                        .filter(command -> command.getAdmittedStates().contains(GameState.MOVE_MOTHER_NATURE))
                        .map(command -> command.getSimpleInfo() + Constants.NEWLINE)
                        .forEach(availableCommands::append);
            }
            case MOVE_STUDENT -> {
                instruction = "Move a student (" + gameHandler.getStudentMoves() + " left): ";
                gameCommands.stream()
                        .filter(command -> command.getAdmittedStates().contains(GameState.MOVE_STUDENT))
                        .map(command -> command.getSimpleInfo() + Constants.NEWLINE)
                        .forEach(availableCommands::append);
            }
            case ACTIVATE_CHARACTER -> {
                instruction = "Activate a character: ";
                gameCommands.stream()
                        .filter(command -> command.getAdmittedStates().contains(GameState.ACTIVATE_CHARACTER))
                        .map(command -> command.getSimpleInfo() + Constants.NEWLINE)
                        .forEach(availableCommands::append);
            }
            case ENDING -> {
                instruction = "The end: ";
                gameCommands.stream()
                        .filter(command -> command.getAdmittedStates().contains(GameState.ENDING))
                        .map(command -> command.getSimpleInfo() + Constants.NEWLINE)
                        .forEach(availableCommands::append);
            }
        }

        stateText.append(ansi().bold());
        stateText.append(instruction).append(ansi().reset());
        if(gameHandler.isActionCompleted())
            stateText.append(ansi().fgBrightGreen().a("DONE!").reset());
        else
            stateText.append(ansi().fgBrightYellow().a("IN PROGRESS...").reset());
        stateText.append(Constants.NEWLINE);
        stateText.append(availableCommands);

        return stateText.toString();
    }

    private String drawClouds(){
        StringBuilder cloudsText = new StringBuilder();

        Game model = view.getGameHandler().getModel();

        cloudsText.append(ansi().bold().a("CLOUDS").reset()).append(Constants.NEWLINE);

        int count = 0;
        for(Students cloud : model.getClouds()){
            if(cloud.count() == 0)
                cloudsText.append(ansi().bgBrightRed().a("Cloud " + count + ": -").reset());
            else {
                cloudsText.append("Cloud ").append(count).append(": ");

                for(Color key : cloud.keySet()){
                    if(cloud.get(key) > 0)
                        cloudsText.append(ansi().fgBright(Ansi.Color.valueOf(key.toString())).a(cloud.get(key) + " ").reset());
                }
            }

            cloudsText.append(Constants.NEWLINE);
            count++;
        }

        return cloudsText.toString();
    }

    private String drawIslands(){
        // TODO: add influence on island
        StringBuilder islandStr = new StringBuilder();

        Game model = view.getGameHandler().getModel();

        islandStr.append(ansi().bold().a("ISLANDS").reset()).append(Constants.NEWLINE);

        for(int i=0; i<model.getIslands().size(); i++){
            Island island = model.getIslands().get(i);
            Students islandStudents = island.getStudents();

            islandStr.append(i).append(" (").append(island.getNumIslands()).append("):\t");
            if(island.hasMotherNature())
                islandStr.append(ansi().bgBrightYellow().fg(Ansi.Color.BLACK).a("M").reset().a(" "));
            else islandStr.append("  ");
            if(island.getNoEntryTiles() > 0)
                islandStr.append(ansi().bg(Ansi.Color.RED).fg(Ansi.Color.BLACK).a(island.getNoEntryTiles()).reset());
            else islandStr.append(" ");
            islandStr.append("  ");

            for(Color studentColor : island.getStudents().keySet()){
                if(islandStudents.get(studentColor) > 0)
                    islandStr.append(ansi().fgBright(Ansi.Color.valueOf(studentColor.toString())).a(islandStudents.get(studentColor) + " ").reset());
            }
            islandStr.append(" ");
            islandStr.append(island.getOwner() == null ?
                    "" :
                    island.getOwner().getUsername() + " - Towers: " + island.getNumTowers()
            );
            islandStr.append(Constants.NEWLINE);
        }

        return islandStr.toString();
    }

    private String drawBoard(){
        StringBuilder boardStr = new StringBuilder();

        Game model = view.getGameHandler().getModel();

        Player player = model.usernameToPlayer(schoolBoardPlayerUsername);

        boardStr.append(ansi().bold().a("SCHOOLBOARD").reset());

        // displays the username of the owner if it's not the user's
        if(!schoolBoardPlayerUsername.equals(username))
            boardStr.append(" (").append(schoolBoardPlayerUsername).append(")");

        boardStr.append(Constants.NEWLINE);

        Students entranceStudents = player.getSchoolBoard().getEntranceStudents();
        Students diningStudents = player.getSchoolBoard().getDiningStudents();
        int numTowers = player.getSchoolBoard().getNumTowers();
        Professors professors = player.getProfessors();

        boardStr.append("Towers: ").append(numTowers);

        // [Expert mode] Coins
        if(model.getExpertMode())
            boardStr.append("\t\t\t").append(drawCoins(player));
        boardStr.append(Constants.NEWLINE);

        boardStr.append("Entrance: ");
        for(Color studentColor : entranceStudents.keySet()){
            if(entranceStudents.get(studentColor) > 0)
                boardStr.append(ansi().fgBright(Ansi.Color.valueOf(studentColor.toString())).a(entranceStudents.get(studentColor) + " ").reset());
        }
        boardStr.append(Constants.NEWLINE).append(Constants.NEWLINE);

        boardStr.append("Dining room:").append(Constants.NEWLINE).append(Constants.NEWLINE);
        for(Color studentColor : diningStudents.keySet()){
            boardStr.append(ansi().bg(Ansi.Color.valueOf(studentColor.toString())).fg(Ansi.Color.BLACK));

            int numOfStudents = diningStudents.get(studentColor);

            for(int i = 0; i< Game.MAX_DINING_STUDENTS; i++){
                if(numOfStudents > 0)
                    boardStr.append("S");
                else if(i > 0 && (i - 2) % 3 == 0) {
                    boardStr.append("C");
                }else boardStr.append("_");
                boardStr.append("   ");

                numOfStudents--;
            }

            if(professors.contains(studentColor))
                boardStr.append(" P");
            else boardStr.append("  ");

            boardStr.append(ansi().a(Constants.NEWLINE + Constants.NEWLINE).reset());
        }

        return boardStr.toString();
    }

    private void printBoards(){
        final int DISTANCE = 49;
        List<String> usernames = view.getGameHandler().getModel().getPlayers().stream()
                .map(LobbyPlayer::getUsername).toList();

        int count = 0;
        for(String username : usernames){
            this.schoolBoardPlayerUsername = username;

            print(drawBoard(), STD_BOARD_POSITION.getFirst(), STD_BOARD_POSITION.getSecond() + (count * DISTANCE));
            count++;
        }
    }

    protected String drawAssistants(){
        StringBuilder assistantStr = new StringBuilder();

        Game model = view.getGameHandler().getModel();

        ArrayList<Assistant> assistants = model.usernameToPlayer(username).getPlayerHand();
        Assistant currentAssistant = model.usernameToPlayer(username).getCurrentAssistant();
        Map<String, Assistant> playedAssistantsMap = model.getPlayers().stream()
                .filter(player -> player.getCurrentAssistant() != null)
                .collect(Collectors.toMap(LobbyPlayer::getUsername, Player::getCurrentAssistant));

        assistantStr.append(ansi().bold().a("ASSISTANTS").reset()).append(Constants.NEWLINE);
        int count = 0;
        // iterating on all assistants
        for(Assistant assistant : Assistant.getAssistants(1)){
            // current assistant is green
            if(assistant.equals(currentAssistant))
                assistantStr.append(ansi().bgBrightGreen().fgBlack());
                // assistants played by other players are yellow
            else if(playedAssistantsMap.containsValue(assistant))
                assistantStr.append(ansi().bgBrightYellow().fgBlack());
                // already played assistants are red
            else if(!assistants.contains(assistant) && !assistant.equals(currentAssistant))
                assistantStr.append(ansi().bgRed().fgBrightDefault());

            // if the player has this assistant, the index is displayed (otherwise -)
            if(assistants.contains(assistant)) {
                assistantStr.append(count).append(": ");
                count++;
            }else assistantStr.append("-: ");

            assistantStr.append("P ").append("%2d".formatted(assistant.getPower())).append(" - ").append("M ").append(assistant.getSteps());
            assistantStr.append(ansi().reset());
            if(playedAssistantsMap.containsValue(assistant)) {
                List<String> usernames = playedAssistantsMap.keySet().stream()
                        .filter(key -> playedAssistantsMap.get(key).equals(assistant))
                        .toList();
                assistantStr.append(" ").append(usernames);
            }
            assistantStr.append(Constants.NEWLINE);
        }

        return assistantStr.toString();
    }

    private String drawCoins(Player player){
        StringBuilder coinsStr = new StringBuilder();

        int coins = player.getCoins();

        coinsStr.append(ansi().bold().a("COINS: ").reset());
        coinsStr.append(ansi().fgBrightYellow().a(coins).reset());

        return coinsStr.toString();
    }

    protected String drawCharacters(){
        StringBuilder charStr = new StringBuilder();

        Game model = view.getGameHandler().getModel();

        ArrayList<Character> characters = model.getCharacters();
        charStr.append(ansi().bold().a("CHARACTERS").reset()).append(Constants.NEWLINE);

        int count = 0;
        for(Character character : characters){
            if(character.equals(view.getGameHandler().getSelectedCharacter()))
                charStr.append(ansi().bgBrightGreen().fgBlack());
            charStr.append(count).append(" - ").append(character.getClass().getSimpleName()).append(" - Cost: ").append(character.getCost()).append(" ");
            charStr.append(ansi().reset());

            // print card's specific students
            // FIXME: is null check useful?
            if(character.getStudents() != null && character.getStudents().count() > 0){
                charStr.append("[ ");
                for(Color studentColor : character.getStudents().keySet()) {
                    int numOfStudents = character.getStudents().get(studentColor);

                    if (numOfStudents > 0)
                        charStr.append(ansi().fg(Ansi.Color.valueOf(studentColor.toString())).a(numOfStudents).reset().a(" "));
                }
                charStr.append("]");
            }

            // print card's specific noEntryTiles
            if(character.getNoEntryTiles() > 0)
                charStr.append(ansi().bg(Ansi.Color.RED).fg(Ansi.Color.BLACK).a(character.getNoEntryTiles()).reset());

            charStr.append(Constants.NEWLINE);
            count++;
        }

        return charStr.toString();
    }

    public void printCharacterInfo(){
        clearScreen();

        Game model = view.getGameHandler().getModel();

        int position = 10;
        print(Constants.CHARACTERS.replaceAll("\n", Constants. NEWLINE), 8, 40);
        for(Character character : model.getCharacters()){
            StringBuilder characterCard = new StringBuilder();

            characterCard.append(ansi().bold().a("CHARACTER: ").reset());
            characterCard.append(character.getClass().getSimpleName()).append("\n\n");

            characterCard.append(ansi().fgYellow().a("COST: ").reset().a(character.getCost()));
            characterCard.append("\n\n");

            characterCard.append("EFFECT: ").append(character.getDescription());
            characterCard.append("\n\n");

            print(addRectangle(characterCard).replaceAll("\n", Constants.NEWLINE), 20, position);
            position += 50;
        }

        out.print(ansi().cursor(40, 1));
    }

    private String drawEndingScreen(){
        StringBuilder endString = new StringBuilder();

        Game model = view.getGameHandler().getModel();

        // if this player is the winner
        if(model.winner().getUsername().equals(username)) {
            endString.append(ansi().fgGreen().a(Constants.VICTORY).reset());
        }else {
            endString.append(ansi().fgBrightRed().a(Constants.DEFEAT).reset());
            endString.append(Constants.NEWLINE).append(Constants.NEWLINE);
            endString.append("\t\t\t\t\t\tWinner: ").append(model.winner().getUsername());
        }

        endString.append(Constants.NEWLINE).append(Constants.NEWLINE).append(Constants.NEWLINE);
        endString.append("\t\t\t\t\tPress Enter to continue......");

        return endString.toString();
    }

    public void printHelpScreen(){
        clearScreen();

        StringBuilder helpText = new StringBuilder();
        List<Command> commands = CommandHandler.getCommands();

        helpText.append(ansi().bold().a(Constants.HELP.replaceAll("\n", Constants.NEWLINE)).reset());
        helpText.append(Constants.NEWLINE).append(Constants.NEWLINE);

        for(CommandType commandType : CommandType.values()){
            helpText.append(ansi().bold().a(commandType.toString()).reset()).append(Constants.NEWLINE);
            commands.stream()
                    .filter(command -> command.getCommandType().equals(commandType))
                    .forEach(command -> {
                        helpText.append(command.getName()).append(" ");
                        command.getArguments().forEach(argument -> helpText.append("[" + argument + "] "));
                        helpText.append(ansi().cursorToColumn(52).a(CommandHandler.normalizeDescription(command)));
                        helpText.append(Constants.NEWLINE);
                    });
            helpText.append(Constants.NEWLINE);
            helpText.append(Constants.NEWLINE);
        }

        print(helpText.toString(), 5, 11);
    }

    public void cleanErrorMessage(){
        this.errorMessage = null;
        out.print(ansi().cursorDownLine(1).eraseLine());
        out.print(ansi().cursorUpLine(2).cursorRight(2).eraseLine());
    }

    public void printRequestedBoard(){
        int rowFrom = STD_BOARD_POSITION.getFirst();
        int columnFrom = STD_BOARD_POSITION.getSecond();
        int rowTo = rowFrom + 14;
        int columnTo = columnFrom + 35;

        eraseBox(rowFrom, columnFrom, rowTo, columnTo);
        print(drawBoard(), STD_BOARD_POSITION);
        print(ansi().eraseLine().a("> ").reset(), STD_CURSOR_POSITION);
    }

    public void refresh(){
        clearScreen();

        if(view.getCurrentHandler() != null){
            switch (view.getCurrentHandler()){
                case LOBBY -> printLobby();
                case GAME -> printGame();
            }
        }else printMenu();
    }

    public void setSchoolboardPlayerUsername(String schoolboardPlayerID) {
        this.schoolBoardPlayerUsername = schoolboardPlayerID;
    }

    public ViewContent getView() {
        return view;
    }

    // ---------------------------- PRINTING HELPER ---------------------------- //

    protected void print(Ansi s, int row, int column) {
        print(s.toString(), row, column);
    }

    protected void print(Ansi s, Pair<Integer, Integer> coordinates){
        print(s, coordinates.getFirst(), coordinates.getSecond());
    }

    protected void print(String s, int row, int column) {
        out.print(ansi().cursor(row, column).a(
                        s.replaceAll(
                                Constants.NEWLINE,
                                ansi().a(Constants.NEWLINE).cursorRight(column - 1).toString()
                        )
                )
        );
        out.flush();
    }

    protected void print(String s, Pair<Integer, Integer> coordinates){
        print(s, coordinates.getFirst(), coordinates.getSecond());
    }

    protected void drawBox(Ansi.Color color, int rowFrom, int columnFrom, int rowTo, int columnTo){
        out.print(ansi().bg(color));

        for(int i=rowFrom; i<rowTo; i++) {
            out.print(ansi().cursor(i, columnFrom));
            for (int k = columnFrom; k < columnTo; k++)
                out.print(ansi().a(" "));
        }

        out.print(ansi().reset());
    }

    protected void eraseBox(int rowFrom, int columnFrom, int rowTo, int columnTo){
        for(int i=rowFrom; i<rowTo; i++) {
            out.print(ansi().cursor(i, columnFrom));
            for (int k = columnFrom; k < columnTo; k++)
                out.print(ansi().reset().a(" "));
        }
    }

    private String addRectangle(StringBuilder text){
        int MAXLEN = 36;

        StringBuilder horizontalLine = new StringBuilder();
        horizontalLine.append("_".repeat(MAXLEN+4));

        StringBuilder replacement = new StringBuilder(text.substring(text.indexOf("EFFECT:")));

        for(int i=0; i<replacement.length(); i+=MAXLEN)
            replacement.insert(i, "\n");

        text.delete(text.indexOf("EFFECT:"), text.length()).append(replacement);
        text.insert(0, "   " + horizontalLine + "\n\n\n");
        text.insert(text.length(), "\n\n" + horizontalLine + "|");

        String result = text.toString().replaceAll("\n", "  |\n|  ");
        StringBuilder final_result = new StringBuilder();
        for(String line : result.split("\n")){
            int spaces = MAXLEN + 10 - line.replaceAll("\\e\\[[\\d;]*[^\\d;]","").length();
            String newline = line.substring(0, line.length()-1) + " ".repeat(spaces) + "|\n";
            final_result.append(newline);
        }

        return final_result.toString().replaceFirst("\\|", "");
    }
}
