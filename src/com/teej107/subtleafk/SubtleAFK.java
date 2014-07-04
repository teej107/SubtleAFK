package com.teej107.subtleafk;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class SubtleAFK extends JavaPlugin implements Listener, Runnable
{
	private Map<String, String> afkPlayers;
	private Map<String, Location> afkLocation;

	@Override
	public void onEnable()
	{
		afkPlayers = new HashMap<String, String>();
		afkLocation = new HashMap<String, Location>();
		Bukkit.getPluginManager().registerEvents(this, this);
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, this, 0, 20 * 60);
	}

	@Override
	public void onDisable()
	{
		for (Player player : Bukkit.getOnlinePlayers())
		{
			removeAFK(player);
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label,
			String[] args)
	{
		if (sender instanceof Player)
		{
			Player p = (Player) sender;
			setAFK(p);
			return true;
		}
		return false;
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event)
	{
		removeAFK(event.getPlayer());
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event)
	{
		removeAFK(event.getPlayer());
	}

	private void setAFK(Player p)
	{
		afkPlayers.put(p.getName(), p.getPlayerListName());
		p.setPlayerListName(ChatColor.DARK_RED
				+ ChatColor.stripColor(p.getPlayerListName()));
		p.sendMessage(ChatColor.RED + "You went AFK.");
	}

	private void removeAFK(Player player)
	{
		String original = afkPlayers.get(player.getName());
		if (original != null)
		{
			player.setPlayerListName(original);
			afkPlayers.remove(player.getName());
			player.sendMessage(ChatColor.YELLOW + "You are no longer AFK.");
		}
	}

	@Override
	public void run()
	{
		for (Player player : Bukkit.getOnlinePlayers())
		{
			if (!afkPlayers.containsKey(player.getName()))
			{
				Location loc = afkLocation.get(player.getName());
				if (loc == null)
				{
					loc = player.getLocation();
				}
				else if (loc.equals(player.getLocation()))
				{
					Bukkit.dispatchCommand(player, "afk");
				}
				afkLocation.put(player.getName(), player.getLocation());
			}
		}
	}
}
