package me.HybridPlague.JobDisplay;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;

public class Main extends JavaPlugin {

	public Display display;
	public Chat chat;
	public Permission perm;
	
	@Override
	public void onEnable() {
		if (!setupChat()) {
			System.out.println(ChatColor.RED + "You must have Vault installed.");
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
		if (!setupPermission()) {
			System.out.println(ChatColor.RED + "You must have Vault installed.");
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
		this.getServer().getPluginManager().registerEvents(new Display(this), this);
		this.display = new Display(this);
		this.saveDefaultConfig();
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (label.equalsIgnoreCase("display") || label.equalsIgnoreCase("jobdisplay")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage("This command is only executable be a player.");
				return true;
			}
			Player p = (Player) sender;
			if (args.length == 1) {
				if (args[0].equalsIgnoreCase("reload") || args[0].equalsIgnoreCase("rl")) {
					if (!p.hasPermission("jobdisplay.reload")) {
						p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cInsufficient permission."));
						return true;
					}
					this.reloadConfig();
					p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&lDisplay &aConfig reloaded."));
					return true;
				}
				display.jobDisplay(p);
				return true;
			}
			display.jobDisplay(p);
			return true;
		}
		
		return false;
	}

	public boolean setupPermission() {
		RegisteredServiceProvider<Permission> permissions = getServer().
				getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
		if (permissions != null)
			perm = permissions.getProvider();
		return (perm != null);
	}

	private boolean setupChat() {
        RegisteredServiceProvider<Chat> rsp = getServer().getServicesManager().getRegistration(Chat.class);
        chat = rsp.getProvider();
        return chat != null;
    }
	
}
