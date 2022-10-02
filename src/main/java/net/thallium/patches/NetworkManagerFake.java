package net.thallium.patches;

import net.minecraft.network.EnumPacketDirection;
import net.minecraft.network.NetworkManager;

public class NetworkManagerFake extends NetworkManager {
	public NetworkManagerFake(EnumPacketDirection p)
	{
		super(p);
	}

	public void disableAutoRead()
	{
	}
	public void checkDisconnected()
	{
	}
}
