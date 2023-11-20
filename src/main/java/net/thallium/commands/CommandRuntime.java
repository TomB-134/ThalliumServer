package net.thallium.commands;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

import java.math.BigDecimal;
import java.math.RoundingMode;

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

        iCommandSender.sendMessage(new TextComponentString("--Sever Runtime Data--"));
        iCommandSender.sendMessage(new TextComponentString("Runtime in hours: " + roundedTimeInHours));
        iCommandSender.sendMessage(new TextComponentString("Runtime in days: " + roundedTimeInDays));
    }
}
