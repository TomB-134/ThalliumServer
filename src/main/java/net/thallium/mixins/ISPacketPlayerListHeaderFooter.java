package net.thallium.mixins;

import net.minecraft.network.play.server.SPacketPlayerListHeaderFooter;
import net.minecraft.util.text.ITextComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SPacketPlayerListHeaderFooter.class)
public interface ISPacketPlayerListHeaderFooter {
	@Accessor("header")
	void setHeader(ITextComponent component);

	@Accessor("footer")
	void setFooter(ITextComponent component);
}
