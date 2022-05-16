package it.polimi.ingsw.utils;

import java.util.Set;

public class Constants {
    // Server info
    public static final String SERVER_ADDR = "127.0.0.1";

    public static final int SERVER_PORT = 42069; // nice

    // Path for saves
    public static final String PATH_SAVES = "src/main/SavedGames";

    // match constants
    public static final int MIN_PLAYERS = 2;
    public static final int MAX_PLAYERS = 3;
    public static final String ANSI_RESET = "\033[0m";

    // Style
    public static final String ANSI_BOLD = "\033[1m";
    public static final String ANSI_ITALIC = "\033[3m";
    public static final String ANSI_UNDERLINE = "\033[4m";

    // Color
    public static final String ANSI_BLACK = "\033[0;30m";   // BLACK
    public static final String ANSI_RED = "\033[0;31m";     // RED
    public static final String ANSI_GREEN = "\033[0;32m";   // GREEN
    public static final String ANSI_YELLOW = "\033[0;33m";  // YELLOW
    public static final String ANSI_BLUE = "\033[0;34m";    // BLUE
    public static final String ANSI_PURPLE = "\033[0;35m";  // PURPLE
    public static final String ANSI_CYAN = "\033[0;36m";    // CYAN
    public static final String ANSI_WHITE = "\033[0;37m";   // WHITE

    // Background
    public static final String ANSI_BLACK_BACKGROUND = "\033[40m";  // BLACK
    public static final String ANSI_RED_BACKGROUND = "\033[41m";    // RED
    public static final String ANSI_GREEN_BACKGROUND = "\033[42m";  // GREEN
    public static final String ANSI_YELLOW_BACKGROUND = "\033[43m"; // YELLOW
    public static final String ANSI_BLUE_BACKGROUND = "\033[44m";   // BLUE
    public static final String ANSI_PURPLE_BACKGROUND = "\033[45m"; // PURPLE
    public static final String ANSI_CYAN_BACKGROUND = "\033[46m";   // CYAN
    public static final String ANSI_WHITE_BACKGROUND = "\033[47m";  // WHITE

    public static final String NEWLINE = String.format("%n");

    public static final Set<String> TRUE_STRING = Set.of("yes", "true", "1");  // immutable set of true
    public static final Set<String> FALSE_STRING = Set.of("no", "false", "0"); // immutable set of false

    public static String ERIANTYS = """

            ███████╗██████╗ ██╗ █████╗ ███╗   ██╗████████╗██╗   ██╗███████╗
            ██╔════╝██╔══██╗██║██╔══██╗████╗  ██║╚══██╔══╝╚██╗ ██╔╝██╔════╝
            █████╗  ██████╔╝██║███████║██╔██╗ ██║   ██║    ╚████╔╝ ███████╗
            ██╔══╝  ██╔══██╗██║██╔══██║██║╚██╗██║   ██║     ╚██╔╝  ╚════██║
            ███████╗██║  ██║██║██║  ██║██║ ╚████║   ██║      ██║   ███████║
            ╚══════╝╚═╝  ╚═╝╚═╝╚═╝  ╚═╝╚═╝  ╚═══╝   ╚═╝      ╚═╝   ╚══════╝
                                                                          \s
            """;

    public static String MENU = """

            ███╗   ███╗███████╗███╗   ██╗██╗   ██╗
            ████╗ ████║██╔════╝████╗  ██║██║   ██║
            ██╔████╔██║█████╗  ██╔██╗ ██║██║   ██║
            ██║╚██╔╝██║██╔══╝  ██║╚██╗██║██║   ██║
            ██║ ╚═╝ ██║███████╗██║ ╚████║╚██████╔╝
            ╚═╝     ╚═╝╚══════╝╚═╝  ╚═══╝ ╚═════╝\s
                                                 \s
            """;

    public static String LOBBY = """

            ██╗      ██████╗ ██████╗ ██████╗ ██╗   ██╗
            ██║     ██╔═══██╗██╔══██╗██╔══██╗╚██╗ ██╔╝
            ██║     ██║   ██║██████╔╝██████╔╝ ╚████╔╝\s
            ██║     ██║   ██║██╔══██╗██╔══██╗  ╚██╔╝ \s
            ███████╗╚██████╔╝██████╔╝██████╔╝   ██║  \s
            ╚══════╝ ╚═════╝ ╚═════╝ ╚═════╝    ╚═╝  \s
                                                     \s
            """;

    public static String HELP = """

            ██╗  ██╗███████╗██╗     ██████╗\s
            ██║  ██║██╔════╝██║     ██╔══██╗
            ███████║█████╗  ██║     ██████╔╝
            ██╔══██║██╔══╝  ██║     ██╔═══╝\s
            ██║  ██║███████╗███████╗██║    \s
            ╚═╝  ╚═╝╚══════╝╚══════╝╚═╝    \s
                                           \s
            """;

    public static String VICTORY = "" + NEWLINE +
            "                                                                                                                " + NEWLINE +
            "`8.`888b           ,8'  8 8888     ,o888888o.8888888 8888888888 ,o888888o.     8 888888888o. `8.`8888.      ,8' " + NEWLINE +
            " `8.`888b         ,8'   8 8888    8888     `88.    8 8888    . 8888     `88.   8 8888    `88. `8.`8888.    ,8'  " + NEWLINE +
            "  `8.`888b       ,8'    8 8888 ,8 8888       `8.   8 8888   ,8 8888       `8b  8 8888     `88  `8.`8888.  ,8'   " + NEWLINE +
            "   `8.`888b     ,8'     8 8888 88 8888             8 8888   88 8888        `8b 8 8888     ,88   `8.`8888.,8'    " + NEWLINE +
            "    `8.`888b   ,8'      8 8888 88 8888             8 8888   88 8888         88 8 8888.   ,88'    `8.`88888'     " + NEWLINE +
            "     `8.`888b ,8'       8 8888 88 8888             8 8888   88 8888         88 8 888888888P'      `8. 8888      " + NEWLINE +
            "      `8.`888b8'        8 8888 88 8888             8 8888   88 8888        ,8P 8 8888`8b           `8 8888      " + NEWLINE +
            "       `8.`888'         8 8888 `8 8888       .8'   8 8888   `8 8888       ,8P  8 8888 `8b.          8 8888      " + NEWLINE +
            "        `8.`8'          8 8888    8888     ,88'    8 8888    ` 8888     ,88'   8 8888   `8b.        8 8888      " + NEWLINE +
            "         `8.`           8 8888     `8888888P'      8 8888       `8888888P'     8 8888     `88.      8 8888      " + NEWLINE;

    public static String DEFEAT = "" + NEWLINE +
            "                                                                                                   " + NEWLINE +
            "8 888888888o.      8 8888888888   8 8888888888   8 8888888888            .8.    8888888 8888888888 " + NEWLINE +
            "8 8888    `^888.   8 8888         8 8888         8 8888                 .888.         8 8888       " + NEWLINE +
            "8 8888        `88. 8 8888         8 8888         8 8888                :88888.        8 8888       " + NEWLINE +
            "8 8888         `88 8 8888         8 8888         8 8888               . `88888.       8 8888       " + NEWLINE +
            "8 8888          88 8 888888888888 8 888888888888 8 888888888888      .8. `88888.      8 8888       " + NEWLINE +
            "8 8888          88 8 8888         8 8888         8 8888             .8`8. `88888.     8 8888       " + NEWLINE +
            "8 8888         ,88 8 8888         8 8888         8 8888            .8' `8. `88888.    8 8888       " + NEWLINE +
            "8 8888        ,88' 8 8888         8 8888         8 8888           .8'   `8. `88888.   8 8888       " + NEWLINE +
            "8 8888    ,o88P'   8 8888         8 8888         8 8888          .888888888. `88888.  8 8888       " + NEWLINE +
            "8 8888    ,o88P'   8 8888         8 8888         8 8888          .888888888. `88888.  8 8888       " + NEWLINE +
            "8 888888888P'      8 888888888888 8 8888         8 888888888888 .8'       `8. `88888. 8 8888       " + NEWLINE;

    public static final String AUTHORS =
            "\nby "
                    + ANSI_PURPLE
                    + "Arturo Benedetti"
                    + ANSI_RESET
                    + ", "
                    + ANSI_RED
                    + "Lorenzo Rossi"
                    + ANSI_RESET
                    + ", "
                    + ANSI_YELLOW
                    + "Luca Romanò"
                    + ANSI_RESET;
}
