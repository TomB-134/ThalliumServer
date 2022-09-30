package net.thallium.mixins;

import net.minecraft.block.BlockShulkerBox;
import net.minecraft.item.EnumDyeColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BlockShulkerBox.class)
public interface IBlockShulkerBox {
	@Accessor
	EnumDyeColor getColor();
}
