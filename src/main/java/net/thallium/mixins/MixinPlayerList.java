package net.thallium.mixins;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.management.PlayerList;
import net.thallium.MCServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerList.class)
public class MixinPlayerList {
    @Inject(method = "playerLoggedIn", at = @At(value = "RETURN"))
    private void onPlayerLoggedIn(EntityPlayerMP player, CallbackInfo ci) {
        MCServer.playerConnected(player);
    }

    @Inject(method = "playerLoggedOut", at = @At(value = "HEAD"))
    private void onPlayerLoggedOut(EntityPlayerMP player, CallbackInfo ci) {
        MCServer.playerDisconnected(player);
    }
}
