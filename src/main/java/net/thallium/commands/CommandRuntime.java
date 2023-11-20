package net.thallium.commands;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.stats.StatBase;
import net.minecraft.util.text.TextComponentString;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static net.minecraft.stats.StatList.BASIC_STATS;

public class CommandRuntime extends CommandThalliumBase{
    @Override
    public String getName() {
        return "runtime";
    }

    @Override
    public String getUsage(ICommandSender iCommandSender) {
        return "runtime";
    }

    @Override
    public void execute(MinecraftServer minecraftServer, ICommandSender iCommandSender, String[] strings) throws CommandException {
        float timeInHours = minecraftServer.worlds[0].getTotalWorldTime() / 20f / 60f / 60f;
        BigDecimal a = new BigDecimal(timeInHours);
        BigDecimal roundedTimeInHours = a.setScale(2, RoundingMode.HALF_EVEN);

        float timeInDays = timeInHours / 24f;
        BigDecimal b = new BigDecimal(timeInDays);
        BigDecimal roundedTimeInDays = b.setScale(2, RoundingMode.HALF_EVEN);

        EntityPlayerMP epm = getCommandSenderAsPlayer(iCommandSender);
        float playerPlayTimeInHours = epm.getStatFile().readStat(BASIC_STATS.get(1)) / 20f / 60f / 60f;
        BigDecimal c = new BigDecimal(playerPlayTimeInHours);
        BigDecimal roundedPlayerPlayTimeInHours = c.setScale(2, RoundingMode.HALF_EVEN);
        float playerPlayTimeInDays = playerPlayTimeInHours / 24f;
        BigDecimal d = new BigDecimal(playerPlayTimeInDays);
        BigDecimal roundedPlayerPlayTimeInDays = d.setScale(2, RoundingMode.HALF_EVEN);


        iCommandSender.sendMessage(new TextComponentString("--Sever Runtime Data Printout--"));
        iCommandSender.sendMessage(new TextComponentString("Server Runtime in hours: " + roundedTimeInHours));
        iCommandSender.sendMessage(new TextComponentString("Server Runtime in days: " + roundedTimeInDays));

        iCommandSender.sendMessage(new TextComponentString(epm.getName() + " playtime in hours: " + roundedPlayerPlayTimeInHours));
        iCommandSender.sendMessage(new TextComponentString(epm.getName() + " playtime in days: " + roundedPlayerPlayTimeInDays));
    }
}
