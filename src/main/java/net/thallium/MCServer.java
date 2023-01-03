package net.thallium;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.GameType;
import net.thallium.logging.LoggerRegistry;
import net.thallium.utils.HUDController;
import net.thallium.utils.PlayerActionHandler;
import net.thallium.utils.ScoreboardHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.security.auth.login.LoginException;
import java.util.ArrayList;

public class MCServer {
	public static final String THALLIUM_SERVER_VERSION = "@THALLIUMVERSION@";
	public static final Logger log = LogManager.getLogger("Thallium");
	public static MinecraftServer server;
	public static final ArrayList<PlayerActionHandler> actionHandlers = new ArrayList<>();

	private static ItemStack makeFirework(int duration) {
		final NBTTagCompound durationTag = new NBTTagCompound();
		final NBTTagCompound fireworksTag = new NBTTagCompound();
		durationTag.setByte("Flight", (byte) duration);
		fireworksTag.setTag("Fireworks", durationTag);
		final ItemStack firework = new ItemStack(Items.FIREWORKS, 3);
		firework.setTagCompound(fireworksTag);
		return firework;
	}

	public static void init(MinecraftServer server) {
		MCServer.server = server;
		ScoreboardHandler.genShorthand();
	}

	public static void onServerLoaded(MinecraftServer server) throws LoginException, InterruptedException {
		server.setMOTD("v" + THALLIUM_SERVER_VERSION + " \u2014 " + server.getMOTD());
		LoggerRegistry.initLoggers(server);
	}

	public static void tick(MinecraftServer server) {
		HUDController.update_hud(server);

		for (PlayerActionHandler actionHandler : actionHandlers) {
			actionHandler.onUpdate();
		}
	}

	public static void playerConnected(EntityPlayerMP player) {
		final GameType mode = player.interactionManager.getGameType();
		if (mode == GameType.CREATIVE) {
			player.setGameType(GameType.SPECTATOR);
		} else if (mode == GameType.ADVENTURE) {
			player.setGameType(GameType.SURVIVAL);
		}

		LoggerRegistry.playerConnected(player);
	}

	public static void playerDisconnected(EntityPlayerMP player) {
		LoggerRegistry.playerDisconnected(player);
	}
}
