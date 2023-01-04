package me.harry;

import java.util.ArrayList;

public class Interface {

    public String WelcomeMessage() {
        return """
                ------------------------------------------------------------
                |                         Welcome                          |
                ------------------------------------------------------------
                | For more information, use the help command -> :menu      |
                ------------------------------------------------------------     
                """;
    }

    public String MenuCommand() {
        return """
                ------------------------------------------------------------
                | To use any commands, use : before the command e.g. :help |
                ------------------------------------------------------------
                |                    Avaliable Commands                    |
                |                                                          |
                |  *  :channels                  - Shows the available     |
                |                                  channels                |
                |  *  :subscribe (channelname)   - Joins a new channel     |
                |  *  :unsubscribe (channelname) - Leaves a channel        |
                |  *  :subscribed                - Shows the channels you  |
                |                                  are subscribed to       |
                |  *  :current                   - Gets the current /      |
                |                                  active channel          |
                |  *  :select (channelname)      - Selects the current /   |
                |                                  active channel ( if     |
                |                                  subscribed to )         |
                |  *  :get (servertime)          - Retrieves all messages  |
                |                                  from the channel        |
                |  *  :search (keywords)         - Searches subscribed     |
                |                                  channels for messages   | 
                |                                  containing the keywords |
                |  *  :quit                      - Closes the program      |
                |  *  :menu                      - You are here now        |
                |                                                          |
                |  * Chat Format *                                         |
                |  Type -> ServerTime | (Channel) Username : Message       |
                |                                                          |
                ------------------------------------------------------------
                """;
    }
}
