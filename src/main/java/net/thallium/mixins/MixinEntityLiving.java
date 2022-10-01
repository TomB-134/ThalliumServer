package net.thallium.mixins;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityLiving.class)
public abstract class MixinEntityLiving extends EntityLivingBase {
    public MixinEntityLiving(World worldIn) {super(worldIn);}

    @Inject(method = "playLivingSound", at = @At("HEAD"), cancellable = true)
    public void playLivingSound(CallbackInfo info) {
        String customName = this.getCustomNameTag();
        if (customName.equalsIgnoreCase("he sweep") ||
                customName.equalsIgnoreCase("he attack") ||
                customName.equalsIgnoreCase("stfu")) {
            info.cancel();
        }
    }
}
