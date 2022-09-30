package net.thallium;

import com.google.common.collect.Lists;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.NonNullList;
import net.minecraft.world.GameType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.thallium.logging.LoggerRegistry;
import net.thallium.utils.HUDController;
import net.thallium.utils.ScoreboardHandler;

import javax.security.auth.login.LoginException;

public class MCServer {
	public static final String THALLIUM_SERVER_VERSION = "@THALLIUMVERSION@";
	public static final Logger log = LogManager.getLogger("Thallium");
	public static MinecraftServer server;
	private static final Ingredient PAPER = Ingredient.fromItem(Items.PAPER);
	private static final Ingredient SULPHUR = Ingredient.fromItem(Items.GUNPOWDER);
	private static final IRecipe duration1 = new ShapelessRecipes("rocket", makeFirework(1), NonNullList.from(Ingredient.EMPTY, PAPER, SULPHUR));
	private static final IRecipe duration2 = new ShapelessRecipes("rocket", makeFirework(2), NonNullList.from(Ingredient.EMPTY, PAPER, SULPHUR, SULPHUR));
	private static final IRecipe duration3 = new ShapelessRecipes("rocket", makeFirework(3), NonNullList.from(Ingredient.EMPTY, PAPER, SULPHUR, SULPHUR, SULPHUR));

	static {
		CraftingManager.register("thallium:durationone", duration1);
		CraftingManager.register("thallium:durationtwo", duration2);
		CraftingManager.register("thallium:durationthree", duration3);
	}

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
	}

	public static void playerConnected(EntityPlayerMP player) {
		final GameType mode = player.interactionManager.getGameType();
		if (mode == GameType.CREATIVE) {
			player.setGameType(GameType.SPECTATOR);
		} else if (mode == GameType.ADVENTURE) {
			player.setGameType(GameType.SURVIVAL);
		}

		LoggerRegistry.playerConnected(player);
		unlockCustomRecipes(player);
	}

	public static void playerDisconnected(EntityPlayerMP player) {
		LoggerRegistry.playerDisconnected(player);
	}

	private static void unlockCustomRecipes(EntityPlayerMP player) {
		player.unlockRecipes(Lists.newArrayList(duration1, duration2, duration3));
	}
}
