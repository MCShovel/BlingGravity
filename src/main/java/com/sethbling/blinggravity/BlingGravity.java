package com.sethbling.blinggravity;

import com.sethbling.blinggravity.GravityTask;
import java.util.HashMap;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class BlingGravity
extends JavaPlugin
implements Listener {
    protected HashMap<UUID, Vector> velocities;
    protected HashMap<UUID, Location> positions;
    protected HashMap<UUID, Boolean> onGround;
	private int tickUpdate;
	double gravity;
	private boolean applyPlayers;
	private boolean applyEntities;

    public void onEnable() {
        this.getLogger().info("BlingGravity has been loaded.");
        this.velocities = new HashMap<UUID, Vector>();
        this.onGround = new HashMap<UUID, Boolean>();
        this.positions = new HashMap<UUID, Location>();
        
        MainConfig cfg = new MainConfig(this);
        cfg.load();
        
        this.gravity = cfg.getGravity();
        this.tickUpdate = cfg.getTickUpdate();
        this.applyPlayers = cfg.appliesToPlayers();
        this.applyEntities = cfg.appliesToEntities();
        		
        this.getCommand("gravity").setExecutor(new CmdGravity(this));
        this.getServer().getPluginManager().registerEvents((Listener)this, (Plugin)this);
        this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new GravityTask(this), tickUpdate, tickUpdate);
    }

    public void onDisable() {
        this.getLogger().info("BlingGravity has been unloaded.");
    }

    private void clearPlayer(UUID uuid) {
    	if (this.positions.containsKey(uuid)) {
	        this.velocities.remove(uuid);
	        this.positions.remove(uuid);
	        this.onGround.remove(uuid);
		}
	}

    public void UpdateVelocities() {
        for (World world : this.getServer().getWorlds()) {
        	if (applyEntities) {
        		for (Entity e : world.getEntities()) {
        			if (!applyPlayers && e instanceof Player) { 
        				continue; 
    				}
        			UpdateVelocitiesFor(world, e);
        		}
        	} else if (applyPlayers) {
				for (Entity e : world.getPlayers()) {
					UpdateVelocitiesFor(world, e);
				}
			}
        }
    }    	
        	
    public void UpdateVelocitiesFor(World world, Entity e) {
    	
    	Vector newv = e.getVelocity().clone();
        UUID uuid = e.getUniqueId();

    	Player player = null;
    	if (e instanceof Player) {
    		player = (Player)e;
    		if (player.isDead() || player.isFlying() || player.isGliding() || player.isInsideVehicle() || player.isSneaking()) {
    			clearPlayer(uuid);
    			return;
    		}
    		Material t = world.getBlockAt(e.getLocation()).getType();
    		if (t == Material.LADDER 
    				|| t == Material.WATER || t == Material.STATIONARY_WATER
    				|| t == Material.LAVA || t == Material.STATIONARY_LAVA) {
    			clearPlayer(uuid);
    			return;
    		}
    		if (player.hasPotionEffect(PotionEffectType.LEVITATION)) {
    			clearPlayer(uuid);
    			return;
    		}
    	}

        if (this.velocities.containsKey(uuid) && this.onGround.containsKey(uuid) && !e.isOnGround()) {
            Vector oldv = this.velocities.get(uuid);
            if (!this.onGround.get(uuid).booleanValue()) {
                Vector d = oldv.clone();
                d.subtract(newv);
                double dy = d.getY();
                if (dy > 0.0 && (newv.getY() < -0.01 || newv.getY() > 0.01)) {
                    newv.setY(oldv.getY() - dy * gravity);
/*
                    boolean oldzchanged;
                    boolean oldxchanged;

                    boolean newxchanged = newv.getX() < -0.001 || newv.getX() > 0.001;
                    oldxchanged = oldv.getX() < -0.001 || oldv.getX() > 0.001;
                    if (newxchanged && oldxchanged) {
                        newv.setX((oldv.getX() + newv.getX()) / 2);
                    }
                    
                    boolean newzchanged = newv.getZ() < -0.001 || newv.getZ() > 0.001;
                    oldzchanged = oldv.getZ() < -0.001 || oldv.getZ() > 0.001;
                    if (newzchanged && oldzchanged) {
                        newv.setZ((oldv.getZ() + newv.getZ()) / 2);
                    }
*/
                    e.setVelocity(newv.clone());
                }
            }/* else if (player != null && this.positions.containsKey(uuid)) {
                Vector pos = e.getLocation().toVector();
                Vector oldpos = this.positions.get(uuid).toVector();
                Vector velocity = pos.subtract(oldpos);
                newv.setX(velocity.getX());
                newv.setZ(velocity.getZ());
            }*/
            e.setVelocity(newv.clone());
        }
        this.velocities.put(uuid, newv.clone());
        this.onGround.put(uuid, e.isOnGround());
        this.positions.put(uuid, e.getLocation());
    }

	@EventHandler
    public void onJoin(PlayerJoinEvent e) {
    	clearPlayer(e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
    	clearPlayer(e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onGameModeChange(PlayerGameModeChangeEvent e) {
    	clearPlayer(e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
    	clearPlayer(e.getEntity().getUniqueId());
    }

    @EventHandler
    public void onChangedWorld(PlayerChangedWorldEvent e) {
    	clearPlayer(e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent e) {
    	clearPlayer(e.getPlayer().getUniqueId());
    }
    
    @EventHandler(priority=EventPriority.HIGHEST)
    public void onEntityDamageEvent(EntityDamageEvent e) {
     	if (e.getCause() == EntityDamageEvent.DamageCause.FALL) {
        	e.setDamage(e.getFinalDamage() * this.gravity);
        }
    }
}
