package com.meyermt.dns;

/**
 * Created by michaelmeyer on 4/23/17.
 */
public class Peer {

    private int id;
    private String name;
    private String ip;
    private int port;
    private boolean root;

    public Peer() {
    }

    public Peer(int id, String name, String ip, int port, boolean root) {
        this.id = id;
        this.name = name;
        this.ip = ip;
        this.port = port;
        this.root = root;
    }

    @Override
    public String toString() {
        return "Peer{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", ip='" + ip + '\'' +
                ", port=" + port +
                ", isRoot=" + root +
                '}';
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public boolean isRoot() {
        return root;
    }
}
