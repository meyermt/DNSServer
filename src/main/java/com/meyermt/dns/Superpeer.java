package com.meyermt.dns;

/**
 * Created by michaelmeyer on 4/23/17.
 */
public class Superpeer {

    private int id;
    private String region;
    private String ip;
    private int port;

    public Superpeer() {}

    public Superpeer(int id, String region, String ip, int port) {
        this.id = id;
        this.region = region;
        this.ip = ip;
        this.port = port;
    }

    public int getId() {
        return id;
    }

    public String getRegion() {
        return region;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    @Override
    public String toString() {
        return "Superpeer{" +
                "id=" + id +
                ", region='" + region + '\'' +
                ", ip='" + ip + '\'' +
                ", port=" + port +
                '}';
    }
}
