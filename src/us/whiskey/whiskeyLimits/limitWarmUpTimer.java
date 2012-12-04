package us.whiskey.whiskeyLimits;

import java.util.Timer;
import java.util.TimerTask;
import org.bukkit.entity.Player;

public class limitWarmUpTimer extends TimerTask {
	
	private limit bCoolDown;
	private Player player;
	private String pre;
	private String message;

	public limitWarmUpTimer(limit bCoolDown, Timer timer, Player player,
			String pre, String message) {
		this.bCoolDown = bCoolDown;
		this.player = player;
		this.pre = pre;
		this.message = message;
	}

	public limitWarmUpTimer() {
	}

	@Override
	public void run() {
		bCoolDown.getServer().getScheduler().scheduleSyncDelayedTask(bCoolDown, new limitWarmUpRunnable());
	}
	
	public class limitWarmUpRunnable implements Runnable {
		public void run() {
			if (player.isOnline() && !player.isDead() && limitWarmUpManager.hasWarmUps(player)) {
				limitCoolDownManager.setWarmUpOK(player, pre, message);
				limitWarmUpManager.removeWarmUpProcess(player.getName() + "@"
						+ pre);
				limitCoolDownListener.clearLocWorld(player);
				player.chat(pre + message);
			} else if (player.isOnline() && player.isDead() && limitWarmUpManager.hasWarmUps(player)){
				limitCoolDownManager.removeWarmUp(player, pre, message);
				limitWarmUpManager.removeWarmUpProcess(player.getName() + "@"
						+ pre);
				limitCoolDownListener.clearLocWorld(player);
			} else if (!player.isOnline() && limitWarmUpManager.hasWarmUps(player)){
				limitCoolDownManager.removeWarmUp(player, pre, message);
				limitWarmUpManager.removeWarmUpProcess(player.getName() + "@"
						+ pre);
				limitCoolDownListener.clearLocWorld(player);
			}
		}
	}
}