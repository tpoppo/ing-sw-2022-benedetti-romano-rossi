package it.polimi.ingsw.utils;

import java.util.Set;

/**
 * This class contains all the most important game constant.
 */
public class Constants {
    // Server info
    public static final String SERVER_ADDR = "127.0.0.1";

    public static final int SERVER_PORT = 42069; // nice

    // Path for saves
    public static final String PATH_SAVES = "SavedGames";

    // match constants
    public static final int MIN_PLAYERS = 2;
    public static final int MAX_PLAYERS = 3;
    public static final String ANSI_RESET = "\033[0m";

    // Color
    public static final String ANSI_RED = "\033[0;31m";     // RED
    public static final String ANSI_YELLOW = "\033[0;33m";  // YELLOW
    public static final String ANSI_PURPLE = "\033[0;35m";  // PURPLE

    public static final String NEWLINE = String.format("%n");

    public static final Set<String> TRUE_STRING = Set.of("yes", "true", "1");  // immutable set of true
    public static final Set<String> FALSE_STRING = Set.of("no", "false", "0"); // immutable set of false

    public static final String ERIANTYS = """

            ███████╗██████╗ ██╗ █████╗ ███╗   ██╗████████╗██╗   ██╗███████╗
            ██╔════╝██╔══██╗██║██╔══██╗████╗  ██║╚══██╔══╝╚██╗ ██╔╝██╔════╝
            █████╗  ██████╔╝██║███████║██╔██╗ ██║   ██║    ╚████╔╝ ███████╗
            ██╔══╝  ██╔══██╗██║██╔══██║██║╚██╗██║   ██║     ╚██╔╝  ╚════██║
            ███████╗██║  ██║██║██║  ██║██║ ╚████║   ██║      ██║   ███████║
            ╚══════╝╚═╝  ╚═╝╚═╝╚═╝  ╚═╝╚═╝  ╚═══╝   ╚═╝      ╚═╝   ╚══════╝
                                                                          \s
            """;

    public static final String MENU = """

            ███╗   ███╗███████╗███╗   ██╗██╗   ██╗
            ████╗ ████║██╔════╝████╗  ██║██║   ██║
            ██╔████╔██║█████╗  ██╔██╗ ██║██║   ██║
            ██║╚██╔╝██║██╔══╝  ██║╚██╗██║██║   ██║
            ██║ ╚═╝ ██║███████╗██║ ╚████║╚██████╔╝
            ╚═╝     ╚═╝╚══════╝╚═╝  ╚═══╝ ╚═════╝\s
                                                 \s
            """;

    public static final String LOBBY = """

            ██╗      ██████╗ ██████╗ ██████╗ ██╗   ██╗
            ██║     ██╔═══██╗██╔══██╗██╔══██╗╚██╗ ██╔╝
            ██║     ██║   ██║██████╔╝██████╔╝ ╚████╔╝\s
            ██║     ██║   ██║██╔══██╗██╔══██╗  ╚██╔╝ \s
            ███████╗╚██████╔╝██████╔╝██████╔╝   ██║  \s
            ╚══════╝ ╚═════╝ ╚═════╝ ╚═════╝    ╚═╝  \s
                                                     \s
            """;

    public static final String HELP = """

            ██╗  ██╗███████╗██╗     ██████╗\s
            ██║  ██║██╔════╝██║     ██╔══██╗
            ███████║█████╗  ██║     ██████╔╝
            ██╔══██║██╔══╝  ██║     ██╔═══╝\s
            ██║  ██║███████╗███████╗██║    \s
            ╚═╝  ╚═╝╚══════╝╚══════╝╚═╝    \s
                                           \s
            """;

    public static final String CHARACTERS = """

             ██████╗██╗  ██╗ █████╗ ██████╗  █████╗  ██████╗████████╗███████╗██████╗ ███████╗
            ██╔════╝██║  ██║██╔══██╗██╔══██╗██╔══██╗██╔════╝╚══██╔══╝██╔════╝██╔══██╗██╔════╝
            ██║     ███████║███████║██████╔╝███████║██║        ██║   █████╗  ██████╔╝███████╗
            ██║     ██╔══██║██╔══██║██╔══██╗██╔══██║██║        ██║   ██╔══╝  ██╔══██╗╚════██║
            ╚██████╗██║  ██║██║  ██║██║  ██║██║  ██║╚██████╗   ██║   ███████╗██║  ██║███████║
             ╚═════╝╚═╝  ╚═╝╚═╝  ╚═╝╚═╝  ╚═╝╚═╝  ╚═╝ ╚═════╝   ╚═╝   ╚══════╝╚═╝  ╚═╝╚══════╝
                                                                                            \s
            """;

    public static final String VICTORY = "" + NEWLINE +
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

    public static final String DEFEAT = "" + NEWLINE +
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
