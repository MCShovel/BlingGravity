package com.sethbling.blinggravity;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CmdGravity implements CommandExecutor {

	private final BlingGravity plugin;
	public CmdGravity(BlingGravity plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
    	if (!sender.hasPermission("BlingGravity.admin")) {
    		sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4You do not have permissions for this command."));
    		return true;
    	}

    	if (args.length > 0)
			plugin.gravity = Double.parseDouble(args[0]);
		sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6Gravity is now at &f" + plugin.gravity));
		
		return true;
	}
}
