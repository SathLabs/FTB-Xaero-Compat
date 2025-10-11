package dev.satherov.ftbxaerocompat.mixin;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import dev.ftb.mods.ftbchunks.client.FTBChunksClient;
import dev.ftb.mods.ftbchunks.client.map.MapManager;
import dev.ftb.mods.ftbchunks.data.ChunkSyncInfo;
import lombok.extern.slf4j.Slf4j;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xaero.map.WorldMapSession;
import xaero.map.world.MapDimension;

import java.util.Collection;
import java.util.UUID;

@Slf4j
@Mixin(value = FTBChunksClient.class, remap = false)
public class FTBChunksClientMixin {
    
    @Inject(
            method = "updateChunksFromServer", 
            at = @At("HEAD"),
            remap = false
    )
    public void updateChunksFromServer(ResourceKey<Level> dimId, UUID teamId, Collection<ChunkSyncInfo> chunkSyncInfoList, CallbackInfo ci) {
        MapManager.getInstance().ifPresent(manager -> {
            
            WorldMapSession session = WorldMapSession.getCurrentSession();
            MapDimension dim = session.getMapProcessor().getMapWorld().getDimension(dimId);
            if (dim == null) {
                log.warn("No dimension found for {}", dimId);
                return;
            }
            
            chunkSyncInfoList.forEach(chunkSyncInfo -> {
                for (int i = -1; i < 2; i++) {
                    for (int j = -1; j < 2; j++) {
                        if (i == 0 && j == 0 || i * i != j * j) {
                            dim.getHighlightHandler().clearCachedHash(chunkSyncInfo.x() + i >> 5, chunkSyncInfo.z() + j >> 5);
                        }
                    }
                }
            });
        });
    }
}
