package me.scill.snowballsplus.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import me.scill.snowballsplus.SnowballsPlus;

public class ReloadCommand implements CommandExecutor {
	
	private final SnowballsPlus plugin;
	
	public ReloadCommand(final SnowballsPlus plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("snowballsplus")) {
			if (sender.hasPermission("snowballs.reload")) {
				plugin.reloadConfig();
				sender.sendMessage(ChatColor.GREEN + "Snowballs+ has been reloaded!");
			}
			return true;
		}
		return false;
	}
}
