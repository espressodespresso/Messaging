package me.harry;

import java.util.ArrayList;

public class Interface {

    public String WelcomeMessage() {
        return """
                ------------------------------------------------------------
                |                         Welcome                          |
                ------------------------------------------------------------
                | For more information, use the help command -> :help      |
                ------------------------------------------------------------     
                """;
    }

    public String HelpMenu() {
        return """
                ------------------------------------------------------------
                | To use any commands, use : before the command e.g. :help |
                ------------------------------------------------------------
                |                    Avaliable Commands                    |
                |                                                          |
                |  *  :subscribe (channelname)   - Joins a new channel     |
                |  *  :unsubscribe (channelname) - Leaves a channel        |
                |  *  :get                       - Retrieves all messages  |
                |                                  you have published      |
                |  *  :quit                      - Closes the program      |
                |  *  :help                      - You are here now        |
                |                                                          |
                ------------------------------------------------------------
                """;
    }
}
