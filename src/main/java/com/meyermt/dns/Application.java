package com.meyermt.dns;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.ModelAndView;
import spark.template.velocity.VelocityTemplateEngine;

import java.io.File;
import java.net.InetAddress;
import java.util.*;
import java.util.stream.Collectors;

import static spark.Spark.*;

/**
 * Created by michaelmeyer on 4/23/17.
 */
public class Application {

    private static Logger logger = LoggerFactory.getLogger(Application.class);
    private static String PORT_ARG = "--port";
    private static String MY_NAME = "";
    private static boolean IS_SUPER = false;
    private static String MY_IP = "";
    private static int MY_PORT = 0;
    private static String MY_SUPER_IP = "";
    private static int MY_SUPER_PORT = 0;

    public static void main(String[] args) {
        if (args.length == 2 && args[0].equals(PORT_ARG)) {
            port(Integer.parseInt(args[1]));
        } else {
            System.out.println("Must run with args --port <port>");
            System.exit(1);
        }
        LocationDAO dao = new LocationDAOSQLLite();
        DNSClient client = new DNSClient();
        staticFileLocation("/public");

        post("api/register", (request, response) -> {
            Map<String, String> inputs = parseFormInputs(request.body());
            String nodeGroup = inputs.get("nodegroup").trim();
            String fullNodeName = nodeGroup + "." + inputs.get("nodename").trim();
            MY_IP = InetAddress.getLocalHost().getHostAddress();
            logger.info("your ip is {}", MY_IP);
            MY_PORT = request.port();
            if (inputs.get("isroot") != null) {
                if (!inputs.get("superip").trim().equals("")) {
                    dao.insertNewSuperInSuper(nodeGroup, inputs.get("superip").trim(), Integer.parseInt(inputs.get("superport").trim()));
//                    logger.info("found super ip and it is {} so cool", inputs.get("superip").trim());
//                    String superFriend = inputs.get("superip").trim() + ":" + inputs.get("superport").trim();
//                    Superpeer mySuper = new Superpeer(0, nodeGroup, MY_IP, MY_PORT);
//                    List<Superpeer> superpeers = client.sendSuperRegistration(mySuper, superFriend);
//                    superpeers.stream().forEach(sp -> {
//                        dao.insertNewSuperInSuper(sp.getRegion(), sp.getIp(), sp.getPort());
//                    });
                } else {
                    MY_NAME = fullNodeName;
                    dao.createDatabaseIfNeeded("peers.db");
                    dao.createDatabaseIfNeeded("superpeers.db");
                    dao.insertNewSuper(fullNodeName, MY_IP, MY_PORT);
                    dao.insertNewSuperInSuper(nodeGroup, MY_IP, MY_PORT);
                }
                IS_SUPER = true;
            } else {
                MY_NAME = fullNodeName;
                MY_SUPER_IP = inputs.get("superip").trim();
                MY_SUPER_PORT = Integer.parseInt(inputs.get("superport").trim());
                String superIPPort = MY_SUPER_IP + ":" + MY_SUPER_PORT;
                client.sendRegistration(superIPPort, fullNodeName, MY_IP, MY_PORT);
            }
            logger.info("your my name is: {}", MY_NAME);
            response.redirect("/registernode.html");
            return "";
        });

        post("api/newnode", (request, response) -> {
            ObjectMapper mapper = new ObjectMapper();
            Peer peer = mapper.readValue(request.body(), Peer.class);
            dao.insertPeer(peer.getName(), peer.getIp(), peer.getPort());
            response.status(200);
            return "";
        });

//        post("api/newsuper", (request, response) -> {
//            ObjectMapper mapper = new ObjectMapper();
//            Superpeer sp = mapper.readValue(request.body(), Superpeer.class);
//            dao.insertNewSuperInSuper(sp.getRegion(), sp.getIp(), sp.getPort());
//            List<Superpeer> peers = dao.getSuperpeers();
//            client.disseminateSuperRegistration(peers);
//            SPGroup spGroup = new SPGroup(peers);
//            return mapper.writeValueAsString(spGroup);
//        });

//        post("api/regsuper", (request, response) -> {
//            ObjectMapper mapper = new ObjectMapper();
//            Superpeer sp = mapper.readValue(request.body(), Superpeer.class);
//            logger.info("registering new superpeer {}", sp.toString());
//            dao.insertNewSuperInSuper(sp.getRegion(), sp.getIp(), sp.getPort());
//            return "";
//        });

        post("api/sendthrusuper", (request, response) -> {
            ObjectMapper mapper = new ObjectMapper();
            Message message = mapper.readValue(request.body(), Message.class);
            if (dao.getPeer(message.getFullName()) != null) {
                Peer peer = dao.getPeer(message.getFullName());
                return client.sendMessage(message, peer);
            } else {
                Superpeer sp = dao.getSuperpeer(message.getRegion());
                return client.sendMessageThroughSuper(message, sp);
            }
        });

        post("api/send", (request, response) -> {
            Map<String, String> inputs = parseFormInputs(request.body());
            String strMessage = inputs.get("message");
            String peerFullName = inputs.get("peer").trim();
            logger.info("sending to {}", peerFullName);
            logger.info("your my name is: {}", MY_NAME);
            Message message = new Message(strMessage, peerFullName, MY_IP, MY_PORT);
            if (IS_SUPER) {
                if (dao.getPeer(peerFullName) != null) {
                    Peer peer = dao.getPeer(peerFullName);
                    return client.sendMessage(message, peer);
                } else {
                    Superpeer sp = dao.getSuperpeer(message.getRegion());
                    return client.sendMessageThroughSuper(message, sp);
                }
            } else {
                Superpeer sp = new Superpeer(0, message.getRegion(), MY_SUPER_IP, MY_SUPER_PORT);
                return client.sendMessageThroughSuper(message, sp);
            }
        });

        post("/messages", (request, response) -> {
            logger.info("Received this message from peer: " + request.body());
            return "Thanks for the message, " + MY_NAME + " got it!";
        });

        get("/index", (request, response) -> "Hello World!");

        get("/peers", (request, response) -> {
            List<Peer> peers = dao.getPeers();
            Map<String, Object> model = new HashMap<>();
            model.put("peers", peers);
            return new VelocityTemplateEngine().render(
                    new ModelAndView(model, "peers.vm")
            );
        });

        get("/superpeers", (request, response) -> {
            List<Superpeer> sps = dao.getSuperpeers();
            Map<String, Object> model = new HashMap<>();
            model.put("peers", sps);
            return new VelocityTemplateEngine().render(
                    new ModelAndView(model, "peers.vm")
            );
        });

    }

    private static Map<String, String> parseFormInputs(String body) {
        return Arrays.asList(body.split("\n")).stream()
                .map(entry -> {
                    String[] formInput = entry.split("=");
                    return new AbstractMap.SimpleEntry<>(formInput[0], formInput[1]);
                })
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
