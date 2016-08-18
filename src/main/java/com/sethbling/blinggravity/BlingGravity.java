/*
 * Decompiled with CFR 0_115.
 *
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.Server
 *  org.bukkit.World
 *  org.bukkit.block.Block
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.entity.EntityDamageEvent
 *  org.bukkit.event.entity.EntityDamageEvent$DamageCause
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.PluginManager
 *  org.bukkit.plugin.java.JavaPlugin
 *  org.bukkit.scheduler.BukkitTask
 *  org.bukkit.util.Vector
 */
package com.sethbling.blinggravity;

import com.sethbling.blinggravity.GravityTask;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

public class BlingGravity
extends JavaPlugin
implements Listener {
    protected HashMap<UUID, Vector> velocities;
    protected HashMap<UUID, Location> positions;
    protected HashMap<UUID, Boolean> onGround;

    public void onEnable() {
        this.getLogger().info("BlingGravity has been loaded.");
        new GravityTask(this).runTaskLater((Plugin)this, 1);
        this.velocities = new HashMap();
        this.onGround = new HashMap();
        this.positions = new HashMap();
        this.getServer().getPluginManager().registerEvents((Listener)this, (Plugin)this);
    }

    public void onDisable() {
        this.getLogger().info("BlingGravity has been unloaded.");
    }

    public void UpdateVelocities() {
        for (World world : this.getServer().getWorlds()) {
            for (Entity e : world.getEntities()) {
                Vector newv = e.getVelocity().clone();
                UUID uuid = e.getUniqueId();
                if (this.velocities.containsKey(uuid) && this.onGround.containsKey(uuid) && !e.isOnGround() && !e.isInsideVehicle()) {
                    Vector oldv = this.velocities.get(uuid);
                    if (!this.onGround.get(uuid).booleanValue()) {
                        Vector d = oldv.clone();
                        d.subtract(newv);
                        double dy = d.getY();
                        if (dy > 0.0 && (newv.getY() < -0.01 || newv.getY() > 0.01)) {
                            boolean oldzchanged;
                            boolean oldxchanged;
                            Location loc = e.getLocation().clone();
                            double gravity = 1.0;
                            while (loc.getBlockY() >= 0) {
                                Block block = loc.getBlock();
                                if (block.getType() == Material.WOOL) {
                                    if (block.getData() == 5) {
                                        gravity = 0.2;
                                    } else if (block.getData() == 14) {
                                        gravity = 5.0;
                                    } else if (block.getData() == 9) {
                                        gravity = -0.2;
                                    } else if (block.getData() == 2) {
                                        gravity = -5.0;
                                    }
                                }
                                if (block.getType() != Material.AIR) break;
                                loc.setY(loc.getY() - 1.0);
                            }
                            newv.setY(oldv.getY() - dy * gravity);
                            boolean newxchanged = newv.getX() < -0.001 || newv.getX() > 0.001;
                            boolean bl = oldxchanged = oldv.getX() < -0.001 || oldv.getX() > 0.001;
                            if (newxchanged && oldxchanged) {
                                newv.setX(oldv.getX());
                            }
                            boolean newzchanged = newv.getZ() < -0.001 || newv.getZ() > 0.001;
                            boolean bl2 = oldzchanged = oldv.getZ() < -0.001 || oldv.getZ() > 0.001;
                            if (newzchanged && oldzchanged) {
                                newv.setZ(oldv.getZ());
                            }
                            e.setVelocity(newv.clone());
                        }
                    } else if (e instanceof Player && this.positions.containsKey(uuid)) {
                        Vector pos = e.getLocation().toVector();
                        Vector oldpos = this.positions.get(uuid).toVector();
                        Vector velocity = pos.subtract(oldpos);
                        newv.setX(velocity.getX());
                        newv.setZ(velocity.getZ());
                    }
                    e.setVelocity(newv.clone());
                }
                this.velocities.put(uuid, newv.clone());
                this.onGround.put(uuid, e.isOnGround());
                this.positions.put(uuid, e.getLocation());
            }
        }
    }

    @EventHandler(priority=EventPriority.HIGHEST)
    public void onEntityDamageEvent(EntityDamageEvent e) {
        if (e.getCause() == EntityDamageEvent.DamageCause.FALL) {
            e.setCancelled(true);
        }
    }
}
