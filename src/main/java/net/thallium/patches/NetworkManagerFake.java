package net.thallium.patches;

import net.minecraft.network.EnumPacketDirection;
import net.minecraft.network.NetworkManager;

public class NetworkManagerFake extends NetworkManager {
	NetworkManagerFake() {
		super(EnumPacketDirection.CLIENTBOUND);
	}

	public void disableAutoRead() {
	}
}