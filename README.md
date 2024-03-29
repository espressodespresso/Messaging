# Messaging App 
## Overview
Multithreaded client-server messaging app utilising the Publish/Subscribe protocol with JSON encoding.
The char ':' defines a command, if you are looking to publish it is not a defined command yet you just
enter the information you wish to send, without the use of ':'
#### Commands
* :channels                  - Shows the available channels
* :subscribe (channelname)   - Joins a new channel
* :unsubscribe (channelname) - Leaves a channel
* :subscribed                - Shows the channels you are currently subscribed to
* :current                   - Gets the active / current channel
* :select (channelname)      - Selects the active / current channel (if subscribed to)
* :get                       - Retrieves all messages you have published
* :search (keywords)         - Searches subscribed channel for messages containing the keywords entered
* :quit                      - Closes the program
* :menu                      - Displays commands

## Requirements
* Java 15 or above (JDK 19 Recommended)

[^1]: Java 15 is required due to the use of text blocks.
