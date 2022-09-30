package net.thallium.mixins;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EntityLivingBase.class)
public abstract class MixinEntityLivingBase extends Entity {
	public MixinEntityLivingBase(World world) {
		super(world);
	}

	@Redirect(method = "renderBrokenItemStack", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityLivingBase;playSound(Lnet/minecraft/util/SoundEvent;FF)V"))
	private void fixToolBreakEffect1(EntityLivingBase entityLivingBase, SoundEvent soundIn, float volume, float pitch) {
		this.world.playSound(null, this.posX, this.posY, this.posZ, soundIn, this.getSoundCategory(), volume, pitch);
	}

	@Redirect(method = "renderBrokenItemStack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;spawnParticle(Lnet/minecraft/util/EnumParticleTypes;DDDDDD[I)V"))
	private void fixToolBreakEffect2(World world, EnumParticleTypes particleType, double xCoord, double yCoord, double zCoord, double xSpeed, double ySpeed, double zSpeed, int... parameters) {
		((WorldServer) world).spawnParticle(particleType, xCoord, yCoord, zCoord, 0, xCoord, yCoord + 0.05D, zCoord, 0.0D, parameters[0], new ItemStack(Item.getItemById(parameters[0])).getMetadata());
	}
}
