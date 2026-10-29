package dev.satherov.ftbxaerocompat.mixin;

import lombok.extern.slf4j.Slf4j;

import dev.satherov.ftbxaerocompat.MapWorldExtension;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import dev.ftb.mods.ftbchunks.client.map.MapChunk;
import dev.ftb.mods.ftbchunks.client.map.MapRegion;
import dev.ftb.mods.ftbchunks.data.ChunkSyncInfo;
import xaero.map.WorldMapSession;
import xaero.map.world.MapDimension;
import xaero.map.world.MapWorld;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Date;
import java.util.UUID;

@Slf4j
@Mixin(value = MapChunk.class, remap = false)
public class MapChunkMixin {

    @Shadow
    @Final
    private MapRegion region;

    @Inject(
            method = "updateFromServer",
            at = @At("TAIL")
    )
    private void ftbxaerocompat$clearCachedHash(Date now, ChunkSyncInfo packet, UUID teamId, CallbackInfo ci) {
        ResourceKey<Level> dimensionKey = this.region.dimension.dimension;
        MapWorld world = WorldMapSession.getCurrentSession().getMapProcessor().getMapWorld();
        MapDimension dimension = world.getDimension(dimensionKey);
        if (dimension == null) {
            if (((MapWorldExtension) world).ftbxaerocompat$isLoaded()) {
                MapChunkMixin.log.warn("Could not find the world map dimension '{}' of a synced chunk", dimensionKey.location());
            }

            return;
        }

        dimension.getHighlightHandler().clearCachedHash(packet.x() >> 5, packet.z() >> 5);
    }
}
