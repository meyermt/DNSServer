package com.meyermt.dns;

import java.util.List;

/**
 * Created by michaelmeyer on 4/27/17.
 */
public class SPGroup {

    private List<Superpeer> sps;

    public SPGroup() {

    }

    public SPGroup(List<Superpeer> sps) {
        this.sps = sps;
    }

    public List<Superpeer> getSps() {
        return sps;
    }
}
