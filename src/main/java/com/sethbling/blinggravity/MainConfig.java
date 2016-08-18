package com.sethbling.blinggravity;

public class MainConfig extends com.sethbling.blinggravity.BaseYamlSettingsFile {
	public MainConfig(BlingGravity plugin) {
		super(plugin, "config.yml");
	}

	public double getGravity() {
		return getDouble("gravity", 0.25);
	}
	
	public boolean appliesToPlayers() {
		return getBoolean("apply.players", true);
	}

	public boolean appliesToEntities() {
		return getBoolean("apply.entities", false);
	}

	public int getTickUpdate() {
		return getInt("ticks", 1);
	}
}
