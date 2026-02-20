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
import dev.ftb.mods.ftbchunks.net.SendChunkPacket;
import dev.satherov.ftbxaerocompat.duck.MapWorldDuck;
import xaero.map.WorldMapSession;
import xaero.map.world.MapDimension;
import xaero.map.world.MapWorld;

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
            at = @At("TAIL"),
            remap = false
    )
    public void updateFromServer(Date now, SendChunkPacket.SingleChunk chunk, UUID teamId, CallbackInfo ci) {
        WorldMapSession session = WorldMapSession.getCurrentSession();
        ResourceKey<Level> dimId = region.dimension.dimension;
        MapWorld world = session.getMapProcessor().getMapWorld();
        MapDimension dim = world.getDimension(dimId);
        
        if (dim == null) {
            if (((MapWorldDuck) world).ftbxaerocompat$isLoaded()) log.warn("No dimension found for {}", dimId);
            return;
        }
        
        dim.getHighlightHandler().clearCachedHash(chunk.getX() >> 5, chunk.getZ() >> 5);
    }
}
