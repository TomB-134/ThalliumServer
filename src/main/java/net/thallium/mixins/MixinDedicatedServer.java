package net.thallium.mixins;

import net.minecraft.server.dedicated.DedicatedServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(DedicatedServer.class)
public abstract class MixinDedicatedServer {
	@Shadow
	private boolean guiIsEnabled;

	/**
	 * @author nessie
	 * @reason lazy whatever, screw the server gui
	 */
	@Overwrite
	public void setGuiEnabled() {
		this.guiIsEnabled = false;
	}
}
