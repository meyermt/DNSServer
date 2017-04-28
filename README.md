# DNSServer
A really simple and brittle server with DNS-ish features. For answers to programming questions from assignment, please skip to the bottom section.

## General Information

This project gave me the opportunity to play around with features and frameworks like Spark for Java, Gradle's shadow plugin, Unirest REST client, and a little bit of bootstrap. Unfortunately, I didn't get enough time to implement an intended phase of superpeer linked login, where, if you wish to register to the superpeer network, you would only need to have information of one other superpeer. As is, the superpeer registration is cumbersome at best. In the future class project, I believe my group is planning to just use a central server to handle the registration (which is much more straightforward).

## How to Run this Project

If you don't want to go through the hassle of building and running, you can just run the "FAT", or uber, JAR that is located in the `bin` directory, like so:

`java -jar bin/DNSServer-all.jar --port [port]`

After doing this please follow directions on configuring superpeers and peers (the superpeer config is manual and has to be done first).

## How to Build this Project

From `programming/DNSServer` dir, run `./gradlew shadowJar`

Then, from the same dir, run with `java -jar build/libs/DNSServer-all.jar --port [port]`.

## How to configure the Project

Each node in this project needs to be run with a different port (hence the port arg). Peers and superpeers run the same program, they just register differently. Follow these directions for each:

1. Register all superpeers first.
2. In a browser, navigate to http://[IP]:[PORT]/ for your superpeer node. Then click on the register link.
3. First register your own node as a superpeer. This means you SHOULD fill out node group and node name (superpeers in this network have names, unlike the way the homework looks. More interchangeable this way). You should not need to fill out the superpeer ip and port. Check the superpeer checkbox and submit.
4. While still on this page for the superpeer, register the other superpeers that will be in the superpeer network. For this, you need to fill in all their information AND check the superpeer box.
5. Once you've done this for all the superpeers, register nodes. This requires filling in all information, which means filling in your node's name and group and their superpeer's ip and port. But DO NOT check the superpeer checkbox.
6. Go about this for as many peers as you like.
7. Send messages by navigating to link from the home page OR just going to http://[IP]:[PORT]/sendmessages.html . You MUST fill in the complete peer name [nodegroup].[nodename]

## Questions from the Assignment

1. The design of the system is explained in configuration and general information. Superpeers can talk to their peers and send messages to other superpeers by looking at which region to route things to (region = node group).
2. Adding a peer/node is covered in instruction 5.
3. There is no deleting of peers. If a peer cannot be reached, it is the same as if it does not exist message-wise.
4. Every insert into the peer database will first try to delete any entry with the same name. So any nodes can stomp on other nodes.
