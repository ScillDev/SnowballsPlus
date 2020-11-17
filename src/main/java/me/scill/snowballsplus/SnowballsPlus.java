package me.scill.snowballsplus;

import org.bukkit.plugin.java.JavaPlugin;

import me.scill.snowballsplus.commands.ReloadCommand;
import me.scill.snowballsplus.listeners.SnowballsListener;

public class SnowballsPlus extends JavaPlugin {

	public void onEnable() {
		saveDefaultConfig();

		// Listeners
		getServer().getPluginManager().registerEvents(new SnowballsListener(this), this);

		// Commands
		getCommand("snowballsplus").setExecutor(new ReloadCommand(this));

		getServer().getConsoleSender().sendMessage("Snowballs+ has been enabled!");
	}

	public void onDisable() {
		getServer().getConsoleSender().sendMessage("Snowballs+ has been disabled!!");
	}
}