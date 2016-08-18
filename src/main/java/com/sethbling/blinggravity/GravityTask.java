/*
 * Decompiled with CFR 0_115.
 *
 * Could not load the following classes:
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.scheduler.BukkitRunnable
 *  org.bukkit.scheduler.BukkitTask
 */
package com.sethbling.blinggravity;

import com.sethbling.blinggravity.BlingGravity;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class GravityTask
extends BukkitRunnable {
    private final BlingGravity plugin;

    public GravityTask(BlingGravity plugin) {
        this.plugin = plugin;
    }

    public void run() {
        this.plugin.UpdateVelocities();
        new GravityTask(this.plugin).runTaskLater((Plugin)this.plugin, 1);
    }
}
