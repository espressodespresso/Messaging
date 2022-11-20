# Messaging App 
## Overview
Multithreaded client-server messaging app utilising the Publish/Subscribe protocol with JSON encoding.
The char ':' defines a command, if you are looking to publish it is not a defined command yet you just
enter the information you wish to send, without the use of ':'
#### Commands
* :subscribe (channelname)   - Joins a new channel
* :unsubscribe (channelname) - Leaves a channel
* :get                       - Retrieves all messages you have published
* :quit                      - Closes the program
* :help                      - Displays commands

## Requirements
* Java 15 or above

[^1]: Java 15 is required due to the use of text blocks.