package net.thallium.launch;

import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.LaunchClassLoader;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.Mixins;

import java.io.File;
import java.util.List;

public class ThalliumTweaker implements ITweaker {
	@Override
	public void injectIntoClassLoader(LaunchClassLoader loader) {
		MixinBootstrap.init();
		Mixins.addConfiguration("mixins.thalliumserver.json");
	}

	@Override
	public String getLaunchTarget() {
		return "net.minecraft.server.MinecraftServer";
	}

	// @formatter:off
	@Override public String[] getLaunchArguments() { return new String[0]; }
	@Override public void acceptOptions(List<String> args, File gameDir, File assetsDir, String profile) {}
	// @formatter:on
}
