package us.whiskey.whiskeyLimits;

import java.util.Iterator;
import java.util.Timer;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.entity.Player;

import util.limitChat;

public class limitWarmUpManager {

	private static ConcurrentHashMap<String, limitWarmUpTimer> playercommands = new ConcurrentHashMap<String, limitWarmUpTimer>();

	static Timer scheduler;

	public static void startWarmUp(limit bCoolDown, Player player,
			String pre, String message, int warmUpSeconds) {
		pre = pre.toLowerCase();
		long warmUpMinutes = Math.round(warmUpSeconds / 60);
		long warmUpHours = Math.round(warmUpMinutes / 60);
		if (!isWarmUpProcess(player, pre, message)) {
			limitCoolDownManager.removeWarmUpOK(player, pre, message);
			String msg = limitConfigManager.getWarmUpMessage();
			msg = msg.replaceAll("&command&", pre);
			if (warmUpSeconds >= 60 && 3600 >= warmUpSeconds) {
				msg = msg.replaceAll("&seconds&", Long.toString(warmUpMinutes));
				msg = msg.replaceAll("&unit&",
						limitConfigManager.getUnitMinutesMessage());
			} else if (warmUpMinutes >= 60) {
				msg = msg.replaceAll("&seconds&", Long.toString(warmUpHours));
				msg = msg.replaceAll("&unit&",
						limitConfigManager.getUnitHoursMessage());
			} else {
				msg = msg.replaceAll("&seconds&", Long.toString(warmUpSeconds));
				msg = msg.replaceAll("&unit&",
						limitConfigManager.getUnitSecondsMessage());
			}
			limitChat.sendMessageToPlayer(player, msg);

			scheduler = new Timer();
			limitWarmUpTimer scheduleMe = new limitWarmUpTimer(bCoolDown,
					scheduler, player, pre, message);
			playercommands.put(player.getName() + "@" + pre, scheduleMe);
			scheduler.schedule(scheduleMe, warmUpSeconds * 1000);
		} else {
			String msg = limitConfigManager.getWarmUpAlreadyStartedMessage();
			msg = msg.replaceAll("&command&", pre);
			limitChat.sendMessageToPlayer(player, msg);
		}
	}

	public static boolean isWarmUpProcess(Player player, String pre,
			String message) {
		pre = pre.toLowerCase();
		if (playercommands.containsKey(player.getName() + "@" + pre)) {
			return true;
		}
		return false;
	}

	public static void removeWarmUpProcess(String tag) {
		limitWarmUpManager.playercommands.remove(tag);
	}

//	public static void cancelWarmUps(Player player) {
//		for (String key : playercommands.keySet()) {
//			if (key.startsWith(player.getName() + "@")) {
//				removeWarmUpProcess(key);
//			}
//		}
//	}

	public static void cancelWarmUps(Player player) {
		Iterator<String> iter = playercommands.keySet().iterator();
		while (iter.hasNext()) {
			if (iter.next().startsWith(player.getName() + "@")) {
				killTimer(player);
				iter.remove();
			}
		}
	}
	public static void killTimer(Player player) {
	for (String key : playercommands.keySet()) {
		if (key.startsWith(player.getName() + "@")) {
			playercommands.get(key).cancel();
		}
	}
	}
	
	public static boolean hasWarmUps(Player player) {
		for (String key : playercommands.keySet()) {
			if (key.startsWith(player.getName() + "@")) {
				return true;
			}
		}
		return false;
	}
}
