package cz.limitik.limit;

import org.bukkit.entity.Player;

import util.limitChat;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;

public class limitPriceManager {
	private static Economy economy = limit.getEconomy();
	public static boolean payForCommand(Player player, String pre, int price, String name) {
		if (economy == null){
			return true;}
		EconomyResponse r = economy.withdrawPlayer(name, price);
		if (r.transactionSuccess()) {
			String msg = String.format(
					limitConfigManager.getPaidForCommandMessage(),
					economy.format(r.amount), economy.format(r.balance));
			msg = msg.replaceAll("&command&", pre);
			limitChat.sendMessageToPlayer(player, msg);
			return true;
		} else {
			String msg = String.format(limitConfigManager.getPaidErrorMessage(),
					r.errorMessage);
			limitChat.sendMessageToPlayer(player, msg);
			return false;
		}
	}
}
