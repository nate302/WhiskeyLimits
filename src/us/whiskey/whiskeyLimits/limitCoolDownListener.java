package us.whiskey.whiskeyLimits;

import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.event.player.PlayerToggleSprintEvent;

import util.limitChat;

public class limitCoolDownListener<a> implements Listener {
	private final limit plugin;
	private boolean blocked = false;
	private static ConcurrentHashMap<Player, Location> playerloc = new ConcurrentHashMap<Player, Location>();
	private static ConcurrentHashMap<Player, String> playerworld = new ConcurrentHashMap<Player, String>();

	public limitCoolDownListener(limit instance) {
		plugin = instance;
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
		if (event.isCancelled()) {
			return;
		}
		String message = event.getMessage();
		message = message.trim().replaceAll(" +", " ");
		Player player = event.getPlayer();
		boolean on = true;
		on = isPluginOnForPlayer(player);

		if (on) {
			boolean used = false;
			String messageCommand = "";
			String preSub = "";
			String preSub2 = "";
			String preSub3 = "";
			String messageSub = "";
			String messageSub2 = "";
			String messageSub3 = "";
			int preSubCheck = -1;
			int preSubCheck2 = -1;
			int preSubCheck3 = -1;
			int price = 0;
			int limit = 0;
			int cd = 0;
			playerloc.put(player, player.getLocation());
			playerworld.put(player, player.getWorld().getName());
			String[] splitCommand;
			splitCommand = message.split(" ");
			String preCommand = splitCommand[0];
			if (splitCommand.length > 1) {
				for (int i = 1; i < splitCommand.length; i++) {
					messageCommand = messageCommand + " " + splitCommand[i];
				}
			}
			if (splitCommand.length > 1) {
				preSub = splitCommand[0] + " " + splitCommand[1];
				for (int i = 2; i < splitCommand.length; i++) {
					messageSub = messageSub + " " + splitCommand[i];
				}
			}
			if (splitCommand.length > 2) {
				preSub2 = splitCommand[0] + " " + splitCommand[1] + " "
						+ splitCommand[2];
				for (int i = 3; i < splitCommand.length; i++) {
					messageSub2 = messageSub2 + " " + splitCommand[i];
				}
			}
			if (splitCommand.length > 3) {
				preSub3 = splitCommand[0] + " " + splitCommand[1] + " "
						+ splitCommand[2] + " " + splitCommand[3];
				for (int i = 4; i < splitCommand.length; i++) {
					messageSub3 = messageSub3 + " " + splitCommand[i];
				}
			}
			if (preSub3.length() > 0) {
				if (preSub3 != null) {
					preSubCheck3 = preSubCheck(player, preSub3);
					if (preSubCheck3 < 0) {
						price = prePriceCheck(player, preSub3);
						cd = preCDCheck(player, preSub3);
						limit = preLimitCheck(player, preSub3);
						if (cd > 0) {
							preSubCheck3 = 0;
						} else if (price > 0) {
							preSubCheck3 = 0;
						} else if (limit > 0) {
							preSubCheck3 = 0;
						}
					}
				}
			}
			if (preSub2.length() > 0) {
				if (preSub2 != null && preSubCheck3 < 0) {
					preSubCheck2 = preSubCheck(player, preSub2);
					if (preSubCheck2 < 0) {
						price = prePriceCheck(player, preSub2);
						cd = preCDCheck(player, preSub2);
						limit = preLimitCheck(player, preSub2);
						if (cd > 0) {
							preSubCheck2 = 0;
						} else if (price > 0) {
							preSubCheck2 = 0;
						} else if (limit > 0) {
							preSubCheck2 = 0;
						}
					}
				}
			}
			if (preSub.length() > 0) {
				if (preSub.length() < 1 || preSub != null && preSubCheck2 < 0) {
					preSubCheck = preSubCheck(player, preSub);
					if (preSubCheck < 0) {
						price = prePriceCheck(player, preSub);
						cd = preCDCheck(player, preSub);
						limit = preLimitCheck(player, preSub);
						if (cd > 0) {
							preSubCheck = 0;
						} else if (price > 0) {
							preSubCheck = 0;
						} else if (limit > 0) {
							preSubCheck = 0;
						}
					}
				}
			}
			if (preSubCheck3 >= 0) {
				blocked = blocked(player, preSub3, messageSub3);
				this.checkCooldown(event, player, preSub3, messageSub3,
						preSubCheck3, price);
				used = true;
			} else if (preSubCheck2 >= 0) {
				blocked = blocked(player, preSub2, messageSub2);
				this.checkCooldown(event, player, preSub2, messageSub2,
						preSubCheck2, price);
				used = true;
			} else if (preSubCheck >= 0) {
				blocked = blocked(player, preSub, messageSub);
				this.checkCooldown(event, player, preSub, messageSub,
						preSubCheck, price);
				used = true;
			} else {
				blocked = blocked(player, preCommand, messageCommand);
				int preCmdCheck = preSubCheck(player, preCommand);
				price = prePriceCheck(player, preCommand);
				this.checkCooldown(event, player, preCommand, messageCommand,
						preCmdCheck, price);
				used = true;
			}

			if (!used) {
				blocked = blocked(player, preCommand, messageCommand);
				int preCmdCheck = preSubCheck(player, preCommand);
				price = prePriceCheck(player, preCommand);
				this.checkCooldown(event, player, preCommand, messageCommand,
						preCmdCheck, price);
				used = false;
			}
		}
	}

	private int preSubCheck(Player player, String preSub) {

			if (player.hasPermission("limitcooldowns.warmup2")) {
				return limitConfigManager.getWarmUp2(preSub);
			} else if (player.hasPermission("limitcooldowns.warmup3")) {
				return limitConfigManager.getWarmUp3(preSub);
			} else if (player.hasPermission(
					"limitcooldowns.warmup4")) {
				return limitConfigManager.getWarmUp4(preSub);
			} else if (player.hasPermission(
					"limitcooldowns.warmup5")) {
				return limitConfigManager.getWarmUp5(preSub);
			} else {
				return limitConfigManager.getWarmUp(preSub);
			}

	}

	private int preLimitCheck(Player player, String preSub) {
			if (player.hasPermission(
					"limitcooldowns.limit6")) {
				return limitConfigManager.getLimit6(preSub);
			} else if (player.hasPermission(
					"limitcooldowns.limit5")) {
				return limitConfigManager.getLimit5(preSub);
			} else if (player.hasPermission(
					"limitcooldowns.limit4")) {
				return limitConfigManager.getLimit4(preSub);
			} else if (player.hasPermission(
					"limitcooldowns.limit3")) {
				return limitConfigManager.getLimit3(preSub);
			} else if (player.hasPermission(
					"limitcooldowns.limit2")) {
				return limitConfigManager.getLimit2(preSub);
			} else {
				return limitConfigManager.getLimit(preSub);
			}
	}

	private int preCDCheck(Player player, String preSub) {
			if (player.hasPermission(
					"limitcooldowns.cooldown2")) {
				return limitConfigManager.getCoolDown2(preSub);
			} else if (player.hasPermission(
					"limitcooldowns.cooldown3")) {
				return limitConfigManager.getCoolDown3(preSub);
			} else if (player.hasPermission(
					"limitcooldowns.cooldown4")) {
				return limitConfigManager.getCoolDown4(preSub);
			} else if (player.hasPermission(
					"limitcooldowns.cooldown5")) {
				return limitConfigManager.getCoolDown5(preSub);
			} else {
				return limitConfigManager.getCoolDown(preSub);
			}
	}

	public int prePriceCheck(Player player, String preSub) {
			if (player.hasPermission(
					"limitcooldowns.cooldown2")) {
				return limitConfigManager.getPrice2(preSub);
			} else if (player.hasPermission(
					"limitcooldowns.cooldown3")) {
				return limitConfigManager.getPrice3(preSub);
			} else if (player.hasPermission(
					"limitcooldowns.cooldown4")) {
				return limitConfigManager.getPrice4(preSub);
			} else if (player.hasPermission(
					"limitcooldowns.cooldown5")) {
				return limitConfigManager.getPrice5(preSub);
			} else {
				return limitConfigManager.getPrice(preSub);
			}
	}

	private boolean blocked(Player player, String pre, String msg) {
		int limit = -1;
		int uses = limitCoolDownManager.getUses(player, pre, msg);
			if (player.hasPermission(
					"limitcooldowns.nolimit")
					|| player.hasPermission(
							"limitcooldowns.nolimit." + pre)) {
			} else {
				if (player.hasPermission(
						"limitcooldowns.limit6")) {
					limit = limitConfigManager.getLimit6(pre);
					if (limit == -1) {
						return false;
					} else if (limit <= uses) {
						return true;
					}
				} else if (player.hasPermission(
						"limitcooldowns.limit5")) {
					limit = limitConfigManager.getLimit5(pre);
					if (limit == -1) {
						return false;
					} else if (limit <= uses) {
						return true;
					}
				} else if (player.hasPermission(
						"limitcooldowns.limit4")) {
					limit = limitConfigManager.getLimit4(pre);
					if (limit == -1) {
						return false;
					} else if (limit <= uses) {
						return true;
					}
				} else if (player.hasPermission(
						"limitcooldowns.limit3")) {
					limit = limitConfigManager.getLimit3(pre);
					if (limit == -1) {
						return false;
					} else if (limit <= uses) {
						return true;
					}
				} else if (player.hasPermission(
						"limitcooldowns.limit2")) {
					limit = limitConfigManager.getLimit2(pre);
					if (limit == -1) {
						return false;
					} else if (limit <= uses) {
						return true;
					}
				} else {
					limit = limitConfigManager.getLimit(pre);
					if (limit == -1) {
						return false;
					} else if (limit <= uses) {
						return true;
					}
				}
			}
		return false;
	}

	private boolean isPluginOnForPlayer(Player player) {
		boolean on;
		if (player.isOp()) {
			on = false;
		}
		if (player.hasPermission(
						"limitcooldowns.exception")) {
			on = false;
		} else if (player.isOp()) {
			on = false;
		} else {
			on = true;
		}
		return on;
	}

	// Returns true if the command is on cooldown, false otherwise
	private void checkCooldown(PlayerCommandPreprocessEvent event,
			Player player, String pre, String message, int warmUpSeconds,
			int price) {
		if (!blocked) {
				if (warmUpSeconds > 0) {
					if (!player.hasPermission(
							"limitcooldowns.nowarmup")
							&& !player.hasPermission(
									"limitcooldowns.nowarmup." + pre)) {
						start(event, player, pre, message, warmUpSeconds);
					}
				} else {
					if (limitCoolDownManager.coolDown(player, pre)) {
						event.setCancelled(true);
					}
				}
			if (!event.isCancelled()) {
				payForCommand(event, player, pre, price);
			}
		} else {
			event.setCancelled(true);
			String msg = String.format(limitConfigManager
					.getCommandBlockedMessage());
			limitChat.sendMessageToPlayer(player, msg);
		}
		if (!event.isCancelled()) {
			limitCoolDownManager.setUses(player, pre, message);
			if (limitConfigManager.getCommandLogging()) {
				limit.commandLogger(player.getName(), pre + message);
			}
		}
	}

	public void payForCommand(PlayerCommandPreprocessEvent event,
			Player player, String pre, int price) {
		String name = player.getName();
		if (price > 0) {
			if (!player.hasPermission(
					"limitcooldowns.noprice")
					&& !player.hasPermission(
							"limitcooldowns.noprice." + pre)) {
				if (limitPriceManager.payForCommand(player, pre, price, name)) {
					return;
				} else {
					limitCoolDownManager.cancelCooldown(player, pre);
					event.setCancelled(true);
					return;
				}
			}
		}
	}
	
	public void payForCommand2(AsyncPlayerChatEvent event,
			Player player, String pre, int price) {
		String name = player.getName();
		if (price > 0) {
			if (!player.hasPermission(
					"limitcooldowns.noprice")
					&& !player.hasPermission(
							"limitcooldowns.noprice." + pre)) {
				if (limitPriceManager.payForCommand(player, pre, price, name)) {
					return;
				} else {
					limitCoolDownManager.cancelCooldown(player, pre);
					event.setCancelled(true);
					return;
				}
			}
		}
	}

	private void start(PlayerCommandPreprocessEvent event, Player player,
			String pre, String message, int warmUpSeconds) {
		if (!limitCoolDownManager.checkWarmUpOK(player, pre, message)) {
			if (limitCoolDownManager.checkCoolDownOK(player, pre, message)) {
				limitWarmUpManager.startWarmUp(this.plugin, player, pre,
						message, warmUpSeconds);
				event.setCancelled(true);
				return;
			} else {
				event.setCancelled(true);
				return;
			}
		} else {
			if (limitCoolDownManager.coolDown(player, pre)) {
				event.setCancelled(true);
				return;
			} else {
				limitCoolDownManager.removeWarmUpOK(player, pre, message);
				return;
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerMove(PlayerMoveEvent event) {
		if (!limitConfigManager.getCancelWarmupOnMove())
			return;

		if (event.isCancelled())
			return;

		Player player = event.getPlayer();
			if (player != null
					&& !player.hasPermission(
							"limitcooldowns.nocancel.move")) {
				if (limitWarmUpManager.hasWarmUps(player) && hasMoved(player)) {
					clearLocWorld(player);
					limitChat.sendMessageToPlayer(player,
							limitConfigManager.getWarmUpCancelledByMoveMessage());
					limitWarmUpManager.cancelWarmUps(player);
				}

			}
	}

	public static boolean hasMoved(Player player) {
		String curworld = player.getWorld().getName();
		String cmdworld = playerworld.get(player);
		Location curloc = player.getLocation();
		Location cmdloc = playerloc.get(player);
		if (!curworld.equals(cmdworld)) {
			return true;
		} else if (cmdloc.distanceSquared(curloc) > 2) {
			return true;
		}

		return false;
	}

	public static void clearLocWorld(Player player) {
		playerloc.remove(player);
		playerworld.remove(player);
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerToggleSneak(PlayerToggleSneakEvent event) {
		if (!limitConfigManager.getCancelWarmupOnSneak())
			return;

		if (event.isCancelled())
			return;

		Player player = event.getPlayer();
			if (player != null
					&& !player.hasPermission(
							"limitcooldowns.nocancel.sneak")) {
				if (limitWarmUpManager.hasWarmUps(player)) {
					limitChat.sendMessageToPlayer(player,
							limitConfigManager.getCancelWarmupOnSneakMessage());
					limitWarmUpManager.cancelWarmUps(player);
				}

			}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerToggleSprint(PlayerToggleSprintEvent event) {
		if (!limitConfigManager.getCancelWarmupOnSprint())
			return;

		if (event.isCancelled())
			return;

		Player player = event.getPlayer();
			if (player != null
					&& !player.hasPermission(
							"limitcooldowns.nocancel.sprint")) {
				if (limitWarmUpManager.hasWarmUps(player)) {
					limitChat.sendMessageToPlayer(player,
							limitConfigManager.getCancelWarmupOnSprintMessage());
					limitWarmUpManager.cancelWarmUps(player);
				}

			}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onEntityDamage(EntityDamageEvent event) {
		if (!limitConfigManager.getCancelWarmUpOnDamage())
			return;

		if (event.isCancelled())
			return;

		Entity entity = event.getEntity();
		if (entity != null && entity instanceof Player) {
			Player player = (Player) entity;
				if (player != null
						&& !player.hasPermission(
								"limitcooldowns.nocancel.damage")) {
					if (limitWarmUpManager.hasWarmUps(player)) {
						limitChat.sendMessageToPlayer(player, limitConfigManager
								.getWarmUpCancelledByDamageMessage());
						limitWarmUpManager.cancelWarmUps(player);
					}

				}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (!limitConfigManager.getBlockInteractDuringWarmup())
			return;

		if (event.isCancelled())
			return;

		Entity entity = event.getPlayer();
		if (entity != null && entity instanceof Player) {
			Player player = (Player) entity;
				if (player != null
						&& !player.hasPermission(
								"limitcooldowns.dontblock.interact")) {
					if (limitWarmUpManager.hasWarmUps(player)) {
						if (event.getClickedBlock().getType().name()
								.equals("CHEST")
								|| event.getClickedBlock().getType().name()
										.equals("FURNACE")
								|| event.getClickedBlock().getType().name()
										.equals("BURNING_FURNACE")
								|| event.getClickedBlock().getType().name()
										.equals("WORKBENCH")
								|| event.getClickedBlock().getType().name()
										.equals("DISPENSER")
								|| event.getClickedBlock().getType().name()
										.equals("JUKEBOX")
								|| event.getClickedBlock().getType().name()
										.equals("LOCKED_CHEST")
								|| event.getClickedBlock().getType().name()
										.equals("ENCHANTMENT_TABLE")
								|| event.getClickedBlock().getType().name()
										.equals("BREWING_STAND")
								|| event.getClickedBlock().getType().name()
										.equals("CAULDRON")
								|| event.getClickedBlock().getType().name()
										.equals("STORAGE_MINECART")) {
							event.setCancelled(true);
							limitChat.sendMessageToPlayer(player,
									limitConfigManager
											.getInteractBlockedMessage());
						}
					}

				}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerGameModeChange(PlayerGameModeChangeEvent event) {
		if (!limitConfigManager.getCancelWarmUpOnGameModeChange())
			return;

		if (event.isCancelled())
			return;

		Entity entity = event.getPlayer();
		if (entity != null && entity instanceof Player) {
			Player player = (Player) entity;
				if (player != null
						&& !player.hasPermission(
								"limitcooldowns.nocancel.gamemodechange")) {
					if (limitWarmUpManager.hasWarmUps(player)) {
						limitChat.sendMessageToPlayer(player, limitConfigManager
								.getCancelWarmupByGameModeChangeMessage());
						limitWarmUpManager.cancelWarmUps(player);
					}

				}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerDeath(PlayerDeathEvent event) {
		if (!limitConfigManager.getCleanCooldownsOnDeath()
				&& !limitConfigManager.getCleanUsesOnDeath())
			return;
		Entity entity = event.getEntity();
		if (entity != null && entity instanceof Player) {
			Player player = (Player) entity;
				if (player != null
						&& player.hasPermission(
								"limitcooldowns.clear.cooldowns.death")) {
					if (limitConfigManager.getCleanCooldownsOnDeath()) {
						limitCoolDownManager.clearSomething("cooldown", player
								.getName().toLowerCase());
					}
				}
				if (player != null
						&& player.hasPermission(
								"limitcooldowns.clear.uses.death")) {
					if (limitConfigManager.getCleanUsesOnDeath()) {
						limitCoolDownManager.clearSomething("uses", player
								.getName().toLowerCase());
					}
				}
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerChat(AsyncPlayerChatEvent event) {
		String chatMessage = event.getMessage();
		String temp = "globalchat";
		int price = 0;
		Player player = event.getPlayer();
		if (chatMessage.startsWith("!")) {
			if (!limitCoolDownManager.checkCoolDownOK(player, temp,
					chatMessage)) {
				event.setCancelled(true);
				return;
			} else {
				if (limitCoolDownManager.coolDown(player, temp)) {
					event.setCancelled(true);
					return;
				}
			}
				price = prePriceCheck(player, temp);
				payForCommand2(event, player, temp, price);
			}
		}
	}