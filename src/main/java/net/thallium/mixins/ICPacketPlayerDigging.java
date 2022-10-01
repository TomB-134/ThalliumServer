package net.thallium.mixins;

import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(CPacketPlayerDigging.class)
public interface ICPacketPlayerDigging {
    @Accessor("position")
    void setPosition(BlockPos pos);
    @Accessor("facing")
    void setFacing(EnumFacing facing);
    @Accessor("action")
    void setAction(CPacketPlayerDigging.Action action);
}