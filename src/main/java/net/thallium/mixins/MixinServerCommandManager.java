package net.thallium.mixins;

import net.minecraft.command.CommandHandler;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandListener;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.ServerCommandManager;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.thallium.commands.*;

@Mixin(ServerCommandManager.class)
public abstract class MixinServerCommandManager extends CommandHandler implements ICommandListener {
	@Inject(method = "<init>", at = @At("RETURN"))
	private void onCtor(MinecraftServer server, CallbackInfo ci) {
		this.registerCommand(new CommandLog());
		this.registerCommand(new CommandPing());
		this.registerCommand(new CommandStackBoxes());
		this.registerCommand(new CommandTick());
		this.registerCommand(new CommandStat());
		this.registerCommand(new CommandMacro());
		this.registerCommand(new CommandBot());
	}

	@Inject(method = "notifyListener", at = @At("HEAD"), cancellable = true)
	private void silenceRcon(ICommandSender sender, ICommand command, int flags, String translationKey, Object[] translationArgs, CallbackInfo ci) {
		if (sender.getName().equals("Rcon")) {
			ci.cancel();
		}
	}
}
