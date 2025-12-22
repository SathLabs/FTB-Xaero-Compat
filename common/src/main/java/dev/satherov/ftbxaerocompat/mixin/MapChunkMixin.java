package dev.satherov.ftbxaerocompat.mixin;

import lombok.extern.slf4j.Slf4j;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.ftb.mods.ftbchunks.client.map.MapChunk;
import dev.ftb.mods.ftbchunks.client.map.MapRegion;
import dev.ftb.mods.ftbchunks.data.ChunkSyncInfo;
import xaero.map.WorldMapSession;
import xaero.map.world.MapDimension;

import java.util.Date;
import java.util.UUID;

@Slf4j
@Mixin(MapChunk.class)
public class MapChunkMixin {
    
    @Shadow
    @Final
    private MapRegion region;
    
    @Inject(
            method = "updateFromServer",
            at = @At("TAIL"),
            remap = false
    )
    public void updateFromServer(Date now, ChunkSyncInfo packet, UUID teamId, CallbackInfo ci) {
        WorldMapSession session = WorldMapSession.getCurrentSession();
        ResourceKey<Level> dimId = region.dimension.dimension;
        MapDimension dim = session.getMapProcessor().getMapWorld().getDimension(dimId);
        
        if (dim == null) {
            log.warn("No dimension found for {}", dimId);
            return;
        }
        
        dim.getHighlightHandler().clearCachedHash(packet.x() >> 5, packet.z() >> 5);
    }
}
