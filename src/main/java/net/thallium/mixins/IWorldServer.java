package net.thallium.mixins;

import net.minecraft.world.WorldServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(WorldServer.class)
public interface IWorldServer {
	@Invoker
	boolean callIsChunkLoaded(int x, int z, boolean allowEmpty);
}
