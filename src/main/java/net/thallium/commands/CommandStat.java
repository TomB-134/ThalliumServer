package net.thallium.commands;

import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSenderWrapper;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.thallium.utils.ScoreboardHandler;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class CommandStat extends CommandThalliumBase {
    public String getName() {
        return "stat";
    }

    public String getUsage(ICommandSender sender) {
        return "Usage: stat <break|craft|drop|killedby|killed|mined|pickedup|used|custom> [item]";
    }

    public int indexOf(String element, String[] array) {
        for (int i = 0; i < array.length; i++) {
            if (array[i].equalsIgnoreCase(element)) {
                return i;
            }
        }
        return -1;
    }

    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        String command = "/scoreboard objectives setdisplay";
        String displaySlot;
        if ("sidebar".equalsIgnoreCase(args[0]) || "list".equalsIgnoreCase(args[0]) || "belowname".equalsIgnoreCase(args[0])) {
            displaySlot = args[0];
            command += " " + displaySlot;
        } else {
            throw new WrongUsageException("Check usage", new Object[0]);
        }

        if ("break".equalsIgnoreCase(args[1])) {
            int i = indexOf(args[2], ScoreboardHandler.scoreboardBreakShorthand);
            String criteria = ScoreboardHandler.scoreboardBreak[i];
            int x = indexOf(criteria, ScoreboardHandler.scoreboardAll);
            command += " " + x;
        }
        if ("craft".equalsIgnoreCase(args[1])) {
            int i = indexOf(args[2], ScoreboardHandler.scoreboardCraftShorthand);
            String criteria = ScoreboardHandler.scoreboardCraft[i];
            int x = indexOf(criteria, ScoreboardHandler.scoreboardAll);
            command += " " + x;
        }
        if ("drop".equalsIgnoreCase(args[1])) {
            int i = indexOf(args[2], ScoreboardHandler.scoreboardDropShorthand);
            String criteria = ScoreboardHandler.scoreboardDrop[i];
            int x = indexOf(criteria, ScoreboardHandler.scoreboardAll);
            command += " " + x;
        }
        if ("killedby".equalsIgnoreCase(args[1])) {
            int i = indexOf(args[2], ScoreboardHandler.scoreboardKilledbyShorthand);
            String criteria = ScoreboardHandler.scoreboardKilledby[i];
            int x = indexOf(criteria, ScoreboardHandler.scoreboardAll);
            command += " " + x;
        }
        if ("killed".equalsIgnoreCase(args[1])) {
            int i = indexOf(args[2], ScoreboardHandler.scoreboardKilledShorthand);
            String criteria = ScoreboardHandler.scoreboardKilled[i];
            int x = indexOf(criteria, ScoreboardHandler.scoreboardAll);
            command += " " + x;
        }
        if ("mined".equalsIgnoreCase(args[1])) {
            int i = indexOf(args[2], ScoreboardHandler.scoreboardMineShorthand);
            String criteria = ScoreboardHandler.scoreboardMine[i];
            int x = indexOf(criteria, ScoreboardHandler.scoreboardAll);
            command += " " + x;
        }
        if ("pickedup".equalsIgnoreCase(args[1])) {
            int i = indexOf(args[2], ScoreboardHandler.scoreboardPickupShorthand);
            String criteria = ScoreboardHandler.scoreboardPickup[i];
            int x = indexOf(criteria, ScoreboardHandler.scoreboardAll);
            command += " " + x;
        }
        if ("used".equalsIgnoreCase(args[1])) {
            int i = indexOf(args[2], ScoreboardHandler.scoreboardUsedShorthand);
            String criteria = ScoreboardHandler.scoreboardUsed[i];
            int x = indexOf(criteria, ScoreboardHandler.scoreboardAll);
            command += " " + x;
        }
        if ("custom".equalsIgnoreCase(args[1])) {
            int i = indexOf(args[2], ScoreboardHandler.scoreboardCustomShorthand);
            String criteria = ScoreboardHandler.scoreboardCustom[i];
            int x = indexOf(criteria, ScoreboardHandler.scoreboardAll);
            command += " " + x;
        }

        CommandSenderWrapper csw = CommandSenderWrapper.create(sender).withSendCommandFeedback(false);
        server.commandManager.executeCommand(csw, command);
    }

    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos) {
        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(args, "sidebar", "list", "belowname");
        }
        if (args.length == 2) {
            return getListOfStringsMatchingLastWord(args, "break", "craft", "drop", "killedby", "killed", "mined", "pickedup", "used", "custom", "clear");
        }
        if (args.length == 3) {
            if ("break".equalsIgnoreCase(args[1])) {
                return getListOfStringsMatchingLastWord(args, ScoreboardHandler.scoreboardBreakShorthand);
            }
            if ("craft".equalsIgnoreCase(args[1])) {
                return getListOfStringsMatchingLastWord(args, ScoreboardHandler.scoreboardCraftShorthand);
            }
            if ("drop".equalsIgnoreCase(args[1])) {
                return getListOfStringsMatchingLastWord(args, ScoreboardHandler.scoreboardDropShorthand);
            }
            if ("killedby".equalsIgnoreCase(args[1])) {
                return getListOfStringsMatchingLastWord(args, ScoreboardHandler.scoreboardKilledbyShorthand);
            }
            if ("killed".equalsIgnoreCase(args[1])) {
                return getListOfStringsMatchingLastWord(args, ScoreboardHandler.scoreboardKilledShorthand);
            }
            if ("mined".equalsIgnoreCase(args[1])) {
                return getListOfStringsMatchingLastWord(args, ScoreboardHandler.scoreboardMineShorthand);
            }
            if ("pickedup".equalsIgnoreCase(args[1])) {
                return getListOfStringsMatchingLastWord(args, ScoreboardHandler.scoreboardPickupShorthand);
            }
            if ("used".equalsIgnoreCase(args[1])) {
                return getListOfStringsMatchingLastWord(args, ScoreboardHandler.scoreboardUsedShorthand);
            }
            if ("custom".equalsIgnoreCase(args[1])) {
                return getListOfStringsMatchingLastWord(args, ScoreboardHandler.scoreboardCustomShorthand);
            }
        }

        return Collections.emptyList();
    }
}
