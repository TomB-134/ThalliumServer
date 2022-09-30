package net.thallium.patches;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;

public class NetHandlerPlayServerFake extends NetHandlerPlayServer {
	public NetHandlerPlayServerFake(MinecraftServer server, NetworkManager nm, EntityPlayerMP playerIn) {
		super(server, nm, playerIn);
	}

	public void sendPacket(final Packet<?> packetIn) {
	}

	public void disconnect(final ITextComponent textComponent) {
		this.player.onKillCommand();
	}
}



