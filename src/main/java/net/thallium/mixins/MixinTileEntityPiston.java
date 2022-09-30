package net.thallium.mixins;

import net.minecraft.block.BlockPistonExtension;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityPiston;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TileEntityPiston.class)
public abstract class MixinTileEntityPiston extends TileEntity {
	@Inject(method = "update", at = @At(value = "INVOKE", shift = At.Shift.BEFORE, target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;I)Z"))
	private void onUpdate(CallbackInfo ci) {
		final IBlockState state = this.world.getBlockState(this.pos);
		this.world.notifyBlockUpdate(pos.offset(state.getValue(BlockPistonExtension.FACING).getOpposite()), state, state, 0);
	}
}
