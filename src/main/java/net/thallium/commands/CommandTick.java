package net.thallium.commands;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.thallium.utils.Profiler;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class CommandTick extends CommandThalliumBase {
	/**
	 * Gets the name of the command
	 */
	public String getName() {
		return "tick";
	}

	/**
	 * Gets the usage string for the command.
	 */
	public String getUsage(ICommandSender sender) {
		return "Usage: tick <health|entities> [duration]";
	}

	/**
	 * Callback for when the command is executed
	 */
	public void execute(final MinecraftServer server, final ICommandSender sender, String[] args) throws CommandException {
		if (args.length == 0) {
			throw new WrongUsageException(getUsage(sender));
		}

		if ("health".equalsIgnoreCase(args[0])) {
			int step = 100;
			if (args.length > 1) {
				step = parseInt(args[1], 20, 72000);
			}

			Profiler.prepare_tick_report(step);
			return;
		} else if ("entity".equalsIgnoreCase(args[0]) || "entities".equalsIgnoreCase(args[0])) {
			int step = 100;
			if (args.length > 1) {
				step = parseInt(args[1], 20, 72000);
			}

			Profiler.prepare_entity_report(step);
			return;
		}

		throw new WrongUsageException(getUsage(sender));
	}

	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos) {
		if (args.length == 1) {
			return getListOfStringsMatchingLastWord(args, "health", "entities");
		}

		if (args.length == 2) {
			if ("health".equalsIgnoreCase(args[0])) {
				return getListOfStringsMatchingLastWord(args, "100", "200", "1000");
			}

			if ("entities".equalsIgnoreCase(args[0])) {
				return getListOfStringsMatchingLastWord(args, "100", "200", "1000");
			}
		}
		return Collections.emptyList();
	}
}
