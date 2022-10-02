package net.thallium.commands;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.thallium.MCServer;
import net.thallium.utils.PlayerActionHandler;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class CommandMacro extends CommandThalliumBase{
    public String getName() {
        return "macro";
    }

    public String getUsage(ICommandSender sender) {
        return "Usage: macro <attack|use> <once|continuous|interval";
    }

    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (!(sender instanceof EntityPlayer)) {return;}

        EntityPlayerMP playerMP = getCommandSenderAsPlayer(sender);
        PlayerActionHandler playersActionHandler = null;

        for (PlayerActionHandler actionHandler : MCServer.actionHandlers) {
            if (actionHandler.player == playerMP) {
                playersActionHandler = actionHandler;
            }
        } if (playersActionHandler == null) { return; }

        String action = args[0];

        if ("use".equalsIgnoreCase(action) || "attack".equalsIgnoreCase(action)) {
            String option = "once";
            int interval = 0;
            if (args.length > 1) {
                option = args[1];
                if (args.length > 2 && option.equalsIgnoreCase("interval")) {
                    interval = parseInt(args[2],2,72000);
                }
            }

            if (action.equalsIgnoreCase("use")) {
                if (option.equalsIgnoreCase("once"))
                    playersActionHandler.useOnce();
                if (option.equalsIgnoreCase("continuous"))
                    playersActionHandler.setUseForever();
                if (option.equalsIgnoreCase("interval") && interval > 1)
                    playersActionHandler.setUse(interval, 0);
            }
            if (action.equalsIgnoreCase("attack")) {
                if (option.equalsIgnoreCase("once"))
                    playersActionHandler.attackOnce();
                if (option.equalsIgnoreCase("continuous"))
                    playersActionHandler.setAttackForever();
                if (option.equalsIgnoreCase("interval") && interval > 1)
                    playersActionHandler.setAttack(interval, 0);
            }
        }
        if ("stop".equalsIgnoreCase(action)) {
            playersActionHandler.stop();
        }
    }

    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos) {
        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(args, "use", "attack");
        }
        if (args.length == 2) {
            return getListOfStringsMatchingLastWord(args, "once", "continuous", "interval");
        }

        return Collections.emptyList();
    }

}
