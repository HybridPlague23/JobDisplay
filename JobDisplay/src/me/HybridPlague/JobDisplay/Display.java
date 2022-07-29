package me.HybridPlague.JobDisplay;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.clip.placeholderapi.PlaceholderAPI;

public class Display implements Listener {

	private List<Inventory> displays = new ArrayList<Inventory>();
	Map<String, Long> cooldowns = new HashMap<String, Long>();
	private Inventory jobDisplay;
	
	private Main plugin;
	public Display(Main plugin) {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		this.plugin = plugin;
	}
	
	public void jobDisplay(Player p) {

		String playerGroups = "%vault_ranks%";
		playerGroups = PlaceholderAPI.setPlaceholders(p, " " + playerGroups);
		
		jobDisplay = Bukkit.createInventory(null, 54, ChatColor.translateAlternateColorCodes('&', "&c&lJob Display"));
		displays.add(jobDisplay);
		p.openInventory(jobDisplay);
		
		List<String> lore = new ArrayList<String>();
		ItemStack item = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
		ItemMeta meta = item.getItemMeta();
		
		meta.setDisplayName(" ");
		item.setItemMeta(meta);
		for (int i = 0; i < 54 ; i++) {
			jobDisplay.setItem(i, item);
		}
		int i = 0;
		
		for (String job : plugin.getConfig().getStringList("Jobs")) {
			
			if (playerGroups.contains(" " + job)) {
				item.setType(Material.OAK_SIGN);
				meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', plugin.chat.getGroupPrefix(p.getName(), job)));
				lore.add("");
				lore.add(ChatColor.translateAlternateColorCodes('&', "&7Click to set your job display."));
				meta.setLore(lore);
				item.setItemMeta(meta);
				jobDisplay.setItem(i, item);
				lore.clear();
				i++;
			}
			
		}
		
		item.setType(Material.BARRIER);
		meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&4Close"));
		item.setItemMeta(meta);
		jobDisplay.setItem(48, item);
		
		item.setType(Material.STONE_BUTTON);
		meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&4Reset"));
		lore.add("");
		lore.add(ChatColor.translateAlternateColorCodes('&', "&7Click to reset your job display."));
		meta.setLore(lore);
		item.setItemMeta(meta);
		jobDisplay.setItem(50, item);
		
	}

	
	@EventHandler
	public void onClick(InventoryClickEvent e) {
		if (displays.contains(e.getInventory())) {
			e.setCancelled(true);
			
			Player p = (Player) e.getWhoClicked();
			try {
				if (e.getCurrentItem().getType().equals(Material.OAK_SIGN)
						&& e.getCurrentItem().hasItemMeta()) {
					if (cooldowns.containsKey(p.getName())) {
						if (cooldowns.get(p.getName()) > System.currentTimeMillis()) {
							long timeleft = (cooldowns.get(p.getName()) - System.currentTimeMillis()) / 1000;
							p.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "» " + ChatColor.WHITE + "This command has a cooldown. Please wait " + ChatColor.YELLOW + "" + timeleft + ChatColor.WHITE + " more seconds.");
							return;
						}
					}
					cooldowns.put(p.getName(), System.currentTimeMillis() + (30 * 1000));
					
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lp user " + p.getName() + " meta setprefix 65 \"" + e.getCurrentItem().getItemMeta().getDisplayName() + "\"");
					p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&lDisplay &fYour displayed job has been set to " + e.getCurrentItem().getItemMeta().getDisplayName()));
				}
				
				if (e.getCurrentItem().getType().equals(Material.STONE_BUTTON)
						&& e.getCurrentItem().getItemMeta().getDisplayName().contains("Reset")) {
					if (cooldowns.containsKey(p.getName())) {
						if (cooldowns.get(p.getName()) > System.currentTimeMillis()) {
							long timeleft = (cooldowns.get(p.getName()) - System.currentTimeMillis()) / 1000;
							p.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "» " + ChatColor.WHITE + "This command has a cooldown. Please wait " + ChatColor.YELLOW + "" + timeleft + ChatColor.WHITE + " more seconds.");
							return;
						}
					}
					cooldowns.put(p.getName(), System.currentTimeMillis() + (plugin.getConfig().getInt("Cooldown") * 1000));
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lp user " + p.getName() + " meta removeprefix 65");
					p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&lDisplay &fYour displayed job has been reset to default."));
				}
				
				if (e.getCurrentItem().getType().equals(Material.BARRIER)
						&& e.getCurrentItem().getItemMeta().getDisplayName().contains("Close")) {
					p.closeInventory();
				}
			} catch (Exception ex) {
				
			}
			
		}
		
		
		return;
	}
	
}
