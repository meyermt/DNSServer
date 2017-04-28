package com.meyermt.dns;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mashape.unirest.http.ObjectMapper;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by michaelmeyer on 4/23/17.
 */
public class DNSClient {

    private Logger logger = LoggerFactory.getLogger(DNSClient.class);

    public DNSClient() {
        setUpMapper();
    }

    public String sendMessage(Message message, Peer peer) {
        String url = "http://" + peer.getIp() + ":" + peer.getPort() + "/messages";
        try {
            return Unirest.post(url)
                    .body(message.getMessage())
                    .asString()
                    .getBody();
        } catch (UnirestException e) {
            e.printStackTrace();
            // we need to pass back something helpful in this scenario
            return "Sorry, it appears as though " + peer.getName() + " is not home right now.";
        }
    }

    public String sendMessageThroughSuper(Message message, Superpeer sp) {
        String url = "http://" + sp.getIp() + ":" + sp.getPort() + "/api/sendthrusuper";
        try {
            return Unirest.post(url)
                    .body(message)
                    .asString()
                    .getBody();
        } catch (UnirestException e) {
            throw new RuntimeException("Unable to send message", e);
        }
    }

    public void sendRegistration(String superIPPort, String fullNodeName, String ip, int port) {
        try {
            String url = "http://" + superIPPort + "/api/newnode";
            Peer peer = new Peer(0, fullNodeName, ip, port, false);
            Unirest.post(url)
                    .body(peer)
                    .asString();
        } catch (UnirestException e) {
            throw new RuntimeException("Unable to send message", e);
        }
    }

//    public void disseminateSuperRegistration(List<Superpeer> superpeers) {
//        superpeers.forEach(superpeer -> {
//            String url = "http://" + superpeer.getIp() + ":" + superpeer.getPort() + "/api/regsuper";
//            logger.info("sending along super reg to: {}", url);
//            try {
//                Unirest.post(url)
//                        .body(superpeer)
//                        .asString();
//            } catch (UnirestException e) {
//                throw new RuntimeException("Unable to send along superpeer registration", e);
//            }
//        });
//    }
//
//    public List<Superpeer> sendSuperRegistration(Superpeer mySuper, String superFriend) {
//        try {
//            logger.info("friend is " + superFriend);
//            logger.info("you are " + mySuper.toString());
//            String url = "http://" + superFriend + "/api/newsuper";
//            HttpResponse<SPGroup> peers = Unirest.post(url)
//                    .body(mySuper)
//                    .asObject(SPGroup.class);
//            return peers.getBody().getSps();
//        } catch (UnirestException e) {
//            throw new RuntimeException("Unable to send super registration", e);
//        }
//    }

    private void setUpMapper () {
        Unirest.setObjectMapper(new ObjectMapper() {
            private  com.fasterxml.jackson.databind.ObjectMapper jacksonObjectMapper
                    = new com.fasterxml.jackson.databind.ObjectMapper();

            public <T> T readValue(String value, Class<T> valueType) {
                try {
                    return jacksonObjectMapper.readValue(value, valueType);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            public String writeValue(Object value) {
                try {
                    return jacksonObjectMapper.writeValueAsString(value);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

}
