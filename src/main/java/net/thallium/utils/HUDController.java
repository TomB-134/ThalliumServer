package net.thallium.utils;

import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketPlayerListHeaderFooter;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.thallium.logging.LoggerRegistry;
import net.thallium.mixins.ISPacketPlayerListHeaderFooter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class HUDController {
    private static final Map<EntityPlayer, List<ITextComponent>> player_huds = new HashMap<>();

    public static void addMessage(EntityPlayer player, ITextComponent hudMessage) {
        if (!player_huds.containsKey(player)) {
            player_huds.put(player, new ArrayList<>());
        } else {
            player_huds.get(player).add(new TextComponentString("\n"));
        }
        player_huds.get(player).add(hudMessage);
    }

    public static void clear_player(EntityPlayer player) {
        SPacketPlayerListHeaderFooter packet = new SPacketPlayerListHeaderFooter();
        ((ISPacketPlayerListHeaderFooter) packet).setHeader(new TextComponentString(""));
        ((ISPacketPlayerListHeaderFooter) packet).setFooter(new TextComponentString(""));
        ((EntityPlayerMP) player).connection.sendPacket(packet);
    }


    public static void update_hud(MinecraftServer server) {
        if (server.getTickCounter() % 20 != 0)
            return;

        player_huds.clear();

        if (LoggerRegistry.__tps)
            log_tps(server);

        if (LoggerRegistry.__mobcaps)
            log_mobcaps();

        for (EntityPlayer player : player_huds.keySet()) {
            SPacketPlayerListHeaderFooter packet = new SPacketPlayerListHeaderFooter();
            ((ISPacketPlayerListHeaderFooter) packet).setHeader(new TextComponentString(""));
            ((ISPacketPlayerListHeaderFooter) packet).setFooter(Messenger.m(null, player_huds.get(player).toArray(new Object[0])));
            ((EntityPlayerMP) player).connection.sendPacket(packet);
        }
    }

    private static void log_tps(MinecraftServer server) {
        double MSPT = MathHelper.average(server.tickTimeArray) * 1.0E-6D;
        double TPS = 1000.0D / Math.max(50, MSPT);
        String color = Messenger.heatmap_color(MSPT, 50);
        ITextComponent[] message = new ITextComponent[]{Messenger.m(null, "g TPS: ", String.format(Locale.US, "%s %.1f", color, TPS), "g  MSPT: ", String.format(Locale.US, "%s %.1f", color, MSPT))};
        LoggerRegistry.getLogger("tps").log(() -> message, "MSPT", MSPT, "TPS", TPS);
    }

    private static void log_mobcaps() {
        List<Object> commandParams = new ArrayList<>();
        for (int dim = -1; dim <= 1; dim++) {
            for (EnumCreatureType type : EnumCreatureType.values()) {
                Tuple<Integer, Integer> counts = SpawnReporter.mobcaps.get(dim).getOrDefault(type, new Tuple<>(0, 0));
                int actual = counts.getFirst(), limit = counts.getSecond();
                Collections.addAll(commandParams, type.name() + "_ACTUAL_DIM_" + dim, actual, type.name() + "_ACTUAL_LIMIT_" + dim, limit);
            }
        }
        LoggerRegistry.getLogger("mobcaps").log((option, player) -> {
            int dim = player.dimension;
            switch (option) {
                case "overworld":
                    dim = 0;
                    break;
                case "nether":
                    dim = -1;
                    break;
                case "end":
                    dim = 1;
                    break;
            }
            return send_mobcap_display(dim);
        }, commandParams.toArray());
    }

    private static ITextComponent[] send_mobcap_display(int dim) {
        List<ITextComponent> components = new ArrayList<>();
        for (EnumCreatureType type : EnumCreatureType.values()) {
            Tuple<Integer, Integer> counts = SpawnReporter.mobcaps.get(dim).getOrDefault(type, new Tuple<>(0, 0));
            int actual = counts.getFirst();
            int limit = counts.getSecond();
            components.add(Messenger.m(null, (actual + limit == 0) ? "g -" : Messenger.heatmap_color(actual, limit) + " " + actual, Messenger.creatureTypeColor(type) + " /" + ((actual + limit == 0) ? "-" : limit)));
            components.add(Messenger.m(null, "w  "));
        }
        components.remove(components.size() - 1);
        return new ITextComponent[]{Messenger.m(null, components.toArray(new Object[0]))};
    }
}