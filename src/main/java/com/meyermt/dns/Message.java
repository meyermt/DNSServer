package com.meyermt.dns;

/**
 * Created by michaelmeyer on 4/26/17.
 */
public class Message {

    private String message;
    private String region;
    private String fullName;
    private String nodeName;
    private String sourceIP;
    private int sourcePort;

    public Message() {}

    public Message(String message, String fullName, String sourceIP, int sourcePort) {
        this.message = message;
        String[] fullNameSplit = fullName.split("\\.");
        this.region = fullNameSplit[0];
        this.nodeName = fullNameSplit[1];
        this.fullName = fullName;
        this.sourceIP = sourceIP;
        this.sourcePort = sourcePort;
    }

    public String getMessage() {
        return message;
    }

    public String getRegion() {
        return region;
    }

    public String getFullName() {
        return fullName;
    }

    public String getNodeName() {
        return nodeName;
    }

    public String getSourceIP() {
        return sourceIP;
    }

    public int getSourcePort() {
        return sourcePort;
    }

    @Override
    public String toString() {
        return "Message{" +
                "message='" + message + '\'' +
                ", region='" + region + '\'' +
                ", fullName='" + fullName + '\'' +
                ", nodeName='" + nodeName + '\'' +
                ", sourceIP='" + sourceIP + '\'' +
                ", sourcePort=" + sourcePort +
                '}';
    }
}
