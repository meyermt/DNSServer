package com.meyermt.dns;

import java.util.List;

/**
 * Created by michaelmeyer on 4/23/17.
 */
public interface LocationDAO {

    void createDatabaseIfNeeded(String fileName);

    Peer getPeer(String name);
    List<Peer> getPeers();
    void insertPeer(String name, String ip, int port);
    void deletePeer(String name);
    void updatePeer(String name, String ip, int port, boolean isRoot);
    int getSuperCount();
    Peer getLocalSuperpeer();
    void insertNewPeer(String name, String ip, int port);
    void insertNewSuper(String name, String ip, int port);

    Superpeer getSuperpeer(String region);
    List<Superpeer> getSuperpeers();
    void insertSuperpeer(String region, String ip, int port);
    void deleteSuperpeer(String region);
    void updateSuperpeer(String region, String ip, int port);
    void insertNewSuperInSuper(String region, String ip, int port);
}
