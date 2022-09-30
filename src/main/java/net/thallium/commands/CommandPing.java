package net.thallium.commands;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class CommandPing extends CommandThalliumBase {
	@Override
	public String getName() {
		return "ping";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "ping [player]";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if (!(sender instanceof EntityPlayerMP)) {
			return;
		}

		EntityPlayerMP player = getCommandSenderAsPlayer(sender);
		if (args.length > 0) {
			final EntityPlayerMP name = server.getPlayerList().getPlayerByUsername(args[0]);
			if (name != null) {
				player = name;
			}
		}

		sender.sendMessage(new TextComponentString("Ping of " + player.getName() + " is " + player.ping + "ms."));
	}

	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
		return args.length == 1 ? getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames()) : Collections.emptyList();
	}
}
