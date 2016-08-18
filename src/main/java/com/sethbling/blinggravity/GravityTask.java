package com.sethbling.blinggravity;

import com.sethbling.blinggravity.BlingGravity;

public class GravityTask
implements Runnable {
    private final BlingGravity plugin;

    public GravityTask(BlingGravity plugin) {
        this.plugin = plugin;
    }

    public void run() {
        this.plugin.UpdateVelocities();
    }
}
