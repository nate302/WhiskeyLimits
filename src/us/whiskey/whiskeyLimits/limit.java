package cz.limitik.limit;

import java.io.IOException;
import java.util.logging.Logger;

import net.milkbowl.vault.Vault;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import util.limitChat;

public class limit extends JavaPlugin {
	public static final Logger log = Logger.getLogger("Minecraft");
	public static PluginDescriptionFile pdfFile;
	private static Economy economy = null;
	private static boolean usingVault = false;
	private PluginManager pm;

	public void onEnable() {
		pdfFile = this.getDescription();
		PluginDescriptionFile pdfFile = this.getDescription();
		log.info("[" + pdfFile.getName() + "]" + " version "
				+ pdfFile.getVersion() + " by " + pdfFile.getAuthors()
				+ " is enabled!");

		new limitConfigManager(this);
		limitConfigManager.load();
		new limitCoolDownManager(this);
		limitCoolDownManager.load();
		pm = getServer().getPluginManager();
		pm.registerEvents(new limitCoolDownListener<Object>(this), this);
		initializeVault();
	}

	public void onDisable() {
		if (limitConfigManager.getClearOnRestart() == true) {
			limitCoolDownManager.clear();
			log.info("[" + pdfFile.getName() + "]" + " cooldowns cleared!");
		} else {
			limitCoolDownManager.save();
			log.info("[" + pdfFile.getName() + "]" + " cooldowns saved!");
		}
		log.info("[" + pdfFile.getName() + "]" + " version "
				+ pdfFile.getVersion() + " disabled!");
	}

	@Override
	public boolean onCommand(CommandSender sender, Command c,
			String commandLabel, String[] args) {
		String command = c.getName().toLowerCase();
			if (command.equalsIgnoreCase("limitcooldowns")) {
				if (args.length == 1) {
					if (sender.hasPermission("limitcooldowns.reload")
							&& args[0].equalsIgnoreCase("reload")) {
						limitConfigManager.reload();
						limitChat.sendMessageToCommandSender(sender, "&6["
								+ pdfFile.getName() + "]&e"
								+ " config reloaded");
						return true;
					}
					if (sender.hasPermission("limitcooldowns.list.limits")
							&& args[0].equalsIgnoreCase("limits")) {
						try {
							Player send = (Player) sender;
							limitCoolDownManager.getLimits(send);
						} catch (ClassCastException e) {
							log.warning("You cannot use this command from console!");
						}
						return true;
					}
				}
				if (args.length == 2) {
					String jmeno = args[1];
					if (sender.hasPermission("limitcooldowns.clearcooldowns")
							&& args[0].equalsIgnoreCase("clearcooldowns")) {
						String co = "cooldown";
						limitCoolDownManager.clearSomething(co, jmeno);
						limitChat.sendMessageToCommandSender(sender, "&6["
								+ pdfFile.getName() + "]&e"
								+ " cooldowns of player " + jmeno + " cleared");
						return true;
					} else if (sender.hasPermission("limitcooldowns.clearuses")
							&& command.equalsIgnoreCase("limitcooldowns")
							&& args[0].equalsIgnoreCase("clearuses")) {
						String co = "uses";
						limitCoolDownManager.clearSomething(co, jmeno);
						limitChat.sendMessageToCommandSender(sender, "&6["
								+ pdfFile.getName() + "]&e"
								+ " uses of player " + jmeno + " cleared");
						return true;
					} else if (sender.hasPermission("limitcooldowns.clearwarmups")
							&& command.equalsIgnoreCase("limitcooldowns")
							&& args[0].equalsIgnoreCase("clearwarmups")) {
						String co = "warmup";
						limitCoolDownManager.clearSomething(co, jmeno);
						limitChat.sendMessageToCommandSender(sender, "&6["
								+ pdfFile.getName() + "]&e"
								+ " warmups of player " + jmeno + " cleared");
						return true;
					}
				}
				if (args.length == 3) {
					String jmeno = args[1];
					String command2 = args[2].trim();
					if (sender.hasPermission("limitcooldowns.clearcooldowns")
							&& args[0].equalsIgnoreCase("clearcooldowns")) {
						String co = "cooldown";
						limitCoolDownManager.clearSomething(co, jmeno, command2);
						limitChat.sendMessageToCommandSender(sender, "&6["
								+ pdfFile.getName() + "]&e"
								+ " cooldown for command " + command2
								+ " of player " + jmeno + " cleared");
						return true;
					} else if (sender.hasPermission("limitcooldowns.clearuses")
							&& args[0].equalsIgnoreCase("clearuses")) {
						String co = "uses";
						limitCoolDownManager.clearSomething(co, jmeno, command2);
						limitChat.sendMessageToCommandSender(sender, "&6["
								+ pdfFile.getName() + "]&e"
								+ " uses for command " + command2
								+ " of player " + jmeno + " cleared");
						return true;
					} else if (sender.hasPermission("limitcooldowns.clearwarmups")
							&& args[0].equalsIgnoreCase("clearwarmups")) {
						String co = "warmup";
						limitCoolDownManager.clearSomething(co, jmeno, command2);
						limitChat.sendMessageToCommandSender(sender, "&6["
								+ pdfFile.getName() + "]&e"
								+ " warmups for command " + command2
								+ " of player " + jmeno + " cleared");
						return true;
					}
				}
				if (args.length == 4) {
					if (sender.hasPermission("limitcooldowns.set")
							&& args[0].equalsIgnoreCase("set")) {
						String coSetnout = args[1];
						String co = args[2];
						int hodnota = 0;
						try {
							hodnota = Integer.valueOf(args[3]);
						} catch (Exception e) {
							limitChat.sendMessageToCommandSender(sender,
									"Added value must be number!");
							return true;
						}
						if (co.startsWith("/")) {
							if (coSetnout.equals("cooldown")
									|| coSetnout.equals("cooldown2")
									|| coSetnout.equals("cooldown3")
									|| coSetnout.equals("cooldown4")
									|| coSetnout.equals("cooldown5")
									|| coSetnout.equals("warmup")
									|| coSetnout.equals("warmup2")
									|| coSetnout.equals("warmup3")
									|| coSetnout.equals("warmup4")
									|| coSetnout.equals("warmup5")
									|| coSetnout.equals("limit")
									|| coSetnout.equals("limit2")
									|| coSetnout.equals("limit3")
									|| coSetnout.equals("limit4")
									|| coSetnout.equals("limit5")
									|| coSetnout.equals("limit6")                      
									|| coSetnout.equals("price")
									|| coSetnout.equals("price2")
									|| coSetnout.equals("price3")
									|| coSetnout.equals("price4")
									|| coSetnout.equals("price5")) {
								limitConfigManager.setAddToConfigFile(coSetnout,
										co, hodnota);
								limitChat.sendMessageToCommandSender(sender,
										"&6[" + pdfFile.getName() + "]&e" + " "
												+ coSetnout + " for command "
												+ co + " is now set to "
												+ hodnota);
								return true;
							} else {
								limitChat.sendMessageToCommandSender(
										sender,
										"&6["
												+ pdfFile.getName()
												+ "]&e"
												+ " You can only set cooldown, cooldown2, cooldown3, cooldown4, cooldown5, warmup, warmup2, warmup3, warmup4, warmup5, limit, limit2, limit3, limit4, limit5, limit6, price, price2, price3, price4, price5.");
								return true;
							}
						} else {
							limitChat.sendMessageToCommandSender(
									sender,
									"&6["
											+ pdfFile.getName()
											+ "]&e"
											+ " Added command have to start with \"/\".");
							return true;
						}
					}
				}
			
		} else {
			limitChat.sendMessageToCommandSender(
					sender,
					"&6["
							+ pdfFile.getName()
							+ "]&e"
							+ " access denied, you lack required permission to do this!");
		}
		return false;
	}

	public static Economy getEconomy() {
		return economy;
	}

	public static boolean isUsingVault() {
		return usingVault;
	}

	public static void commandLogger(String player, String command) {
		log.info("[" + "limitLogger" + "] " + player + " used command "
				+ command);
	}

	private boolean setupEconomy() {
		if (usingVault) {
			RegisteredServiceProvider<Economy> economyProvider = getServer()
					.getServicesManager().getRegistration(
							net.milkbowl.vault.economy.Economy.class);
			if (economyProvider != null) {
				economy = economyProvider.getProvider();
			}
			return (economy != null);
		}
		return false;
	}

	private void initializeVault() {
		Plugin x = this.getServer().getPluginManager().getPlugin("Vault");
		if (x != null & x instanceof Vault) {
			log.info("[" + pdfFile.getName() + "]"
					+ " found [Vault] searching for economy plugin.");
			usingVault = true;
			if (setupEconomy()) {
				log.info("[" + pdfFile.getName() + "]" + " found ["
						+ economy.getName()
						+ "] plugin, enabling prices support.");
			} else {
				log.info("["
						+ pdfFile.getName()
						+ "]"
						+ " economy plugin not found, disabling prices support.");
			}
		} else {
			log.info("["
					+ pdfFile.getName()
					+ "]"
					+ " [Vault] not found disabling economy support.");
			usingVault = false;
		}
	}
}
