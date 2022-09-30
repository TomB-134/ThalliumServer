package net.thallium.mixins;

import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import net.thallium.utils.Profiler;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

@Mixin(World.class)
public abstract class MixinWorld {
	// @formatter:off
	private String worldName;
	private Iterator<TileEntity> myIterator;
	@Shadow @Final public WorldProvider provider;
	@Shadow @Final public List<TileEntity> loadedTileEntityList;
	@Shadow @Final public List<TileEntity> tickableTileEntities;
	@Shadow @Final private List<TileEntity> tileEntitiesToBeRemoved;
	@Shadow public abstract Chunk getChunk(BlockPos pos);
	@Shadow public abstract boolean isBlockLoaded(BlockPos pos);
	// @formatter:on

	@Inject(method = "<init>", at = @At("RETURN"))
	private void setWorldName(ISaveHandler saveHandlerIn, WorldInfo info, WorldProvider providerIn, net.minecraft.profiler.Profiler profilerIn, boolean client, CallbackInfo ci) {
		this.worldName = this.provider.getDimensionType().getName();
	}

	@Redirect(method = "updateEntities", at = @At(value = "INVOKE", target = "Ljava/util/List;isEmpty()Z", ordinal = 0, remap = false))
	private boolean fasterTEremoval(List<TileEntity> list) {
		if (!this.tileEntitiesToBeRemoved.isEmpty()) {
			final Set<TileEntity> remove = Collections.newSetFromMap(new IdentityHashMap<>());
			remove.addAll(this.tileEntitiesToBeRemoved);
			this.tickableTileEntities.removeAll(remove);
			this.loadedTileEntityList.removeAll(remove);
			this.tileEntitiesToBeRemoved.clear();
		}

		return true;
	}

	@Inject(method = "updateEntities", at = @At(value = "INVOKE_STRING", target = "Lnet/minecraft/profiler/Profiler;endStartSection(Ljava/lang/String;)V", args = "ldc=remove"))
	private void onRemoveEntities(CallbackInfo ci) {
		Profiler.start_section(this.worldName, "entities");
	}

	@Inject(method = "updateEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;getRidingEntity()Lnet/minecraft/entity/Entity;"), locals = LocalCapture.CAPTURE_FAILHARD)
	private void onGetRidingEntity(CallbackInfo ci, int i, Entity entity) {
		Profiler.start_entity_section(this.worldName, entity);
	}

	@Inject(method = "updateEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/profiler/Profiler;endSection()V", ordinal = 1))
	private void postUpdateEntities(CallbackInfo ci) {
		Profiler.end_current_entity_section();
	}

	@Inject(method = "updateEntities", at = @At(value = "INVOKE_STRING", target = "Lnet/minecraft/profiler/Profiler;endStartSection(Ljava/lang/String;)V", args = "ldc=blockEntities"))
	private void onStartBlockEntities(CallbackInfo ci) {
		Profiler.end_current_section();
		Profiler.start_section(this.worldName, "tileentities");
	}

	@Inject(method = "updateEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/tileentity/TileEntity;isInvalid()Z", ordinal = 0), locals = LocalCapture.CAPTURE_FAILHARD)
	private void keepACopy(CallbackInfo ci, Iterator<TileEntity> iterator, TileEntity entity) {
		this.myIterator = iterator;
		Profiler.start_entity_section(this.worldName, entity);
	}

	@Redirect(method = "updateEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/tileentity/TileEntity;isInvalid()Z", ordinal = 1))
	private boolean onRemoveBlockEntity(TileEntity tileEntity) {
		if (tileEntity.isInvalid()) {
			this.myIterator.remove();
			this.loadedTileEntityList.remove(tileEntity);

			if (this.isBlockLoaded(tileEntity.getPos())) {
				this.getChunk(tileEntity.getPos()).removeTileEntity(tileEntity.getPos());
			}
		}

		Profiler.end_current_entity_section();
		return false;
	}

	@Inject(method = "updateEntities", at = @At("RETURN"))
	private void onEndUpdateEntities(CallbackInfo ci) {
		Profiler.end_current_section();
	}

	@Redirect(method = "updateEntityWithOptionalForce", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;setPositionNonDirty()Z"))
	private boolean alwaysLoadChunk(Entity entity) {
		return true;
	}
}
