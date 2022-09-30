package net.thallium.commands;

import net.thallium.patches.EntityPlayerMPFake;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CommandPlayer extends CommandThalliumBase {
	public String getName() {
		return "player";
	}

	public String getUsage(ICommandSender sender) {
		return "player <player_name> <spawn|kill> ";
	}

	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if (args.length < 2) {
			throw new WrongUsageException("player <x> <kill|spawn>");
		}
		String playerName = args[0];
		String action = args[1];
		EntityPlayerMP player = server.getPlayerList().getPlayerByUsername(playerName);
		if (sender instanceof EntityPlayer) {
			EntityPlayer sendingPlayer = getCommandSenderAsPlayer(sender);
			if (!(server.getPlayerList().canSendCommands(sendingPlayer.getGameProfile()))) {
				if (!(sendingPlayer == player || player == null || player instanceof EntityPlayerMPFake)) {
					throw new WrongUsageException("Non OP players can't control other players");
				}
			}
		}
		if (player == null && !action.equalsIgnoreCase("spawn")) {
			throw new WrongUsageException("player doesn't exist");
		}

		if ("spawn".equalsIgnoreCase(action)) {
			if (player != null) {
				throw new WrongUsageException("player " + playerName + " already exists");
			} else if (Arrays.asList(server.getPlayerList().getWhitelistedPlayerNames()).contains(playerName)) {
				throw new WrongUsageException("You can't spawn whitelisted players");
			}
			if (playerName.length() < 3 || playerName.length() > 16) {
				throw new WrongUsageException("player names can only be 3 to 16 chars long");
			}
			Vec3d vec3d = sender.getPositionVector();
			double d0 = vec3d.x;
			double d1 = vec3d.y;
			double d2 = vec3d.z;
			double yaw = 0.0D;
			double pitch = 0.0D;
			World world = sender.getEntityWorld();
			int dimension = world.provider.getDimensionType().getId();

			if (sender instanceof EntityPlayerMP) {
				EntityPlayerMP entity = getCommandSenderAsPlayer(sender);
				yaw = entity.rotationYaw;
				pitch = entity.rotationPitch;
			}
			if (args.length >= 5) {
				d0 = parseCoordinate(d0, args[2], true).getResult();
				d1 = parseCoordinate(d1, args[3], -4096, 4096, false).getResult();
				d2 = parseCoordinate(d2, args[4], true).getResult();
				yaw = parseCoordinate(yaw, args.length > 5 ? args[5] : "~", false).getResult();
				pitch = parseCoordinate(pitch, args.length > 6 ? args[6] : "~", false).getResult();
			}
			if (args.length >= 8) {
				String dimension_string = args[7];
				dimension = 0;
				if ("nether".equalsIgnoreCase(dimension_string)) {
					dimension = -1;
				}
				if ("end".equalsIgnoreCase(dimension_string)) {
					dimension = 1;
				}
			}
			EntityPlayerMPFake.createFake(playerName, server, d0, d1, d2, yaw, pitch, dimension, 3);
			return;
		}
		if ("kill".equalsIgnoreCase(action)) {
			if (!(player instanceof EntityPlayerMPFake)) {
				throw new WrongUsageException("use /kill or /kick on regular players");
			}
			player.onKillCommand();
			return;
		}
		throw new WrongUsageException("unknown action: " + action);
	}

	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
		if (args.length == 1) {
			Set<String> players = new HashSet<>(Arrays.asList(server.getOnlinePlayerNames()));
			players.add("Steve");
			players.add("Alex");
			return getListOfStringsMatchingLastWord(args, players.toArray(new String[0]));
		}
		if (args.length == 2) {
			//currently for all, needs to be restricted for Fake plaeyrs
			return getListOfStringsMatchingLastWord(args,
					"spawn", "kill");
		}
		if (args.length > 2 && (args[1].equalsIgnoreCase("spawn"))) {
			if (args.length <= 5) {
				return getTabCompletionCoordinate(args, 2, targetPos);
			} else if (args.length <= 7) {
				return getListOfStringsMatchingLastWord(args, "0.0");
			} else if (args.length == 8) {
				return getListOfStringsMatchingLastWord(args, "overworld", "end", "nether");
			}
		}
		return Collections.emptyList();
	}
}
