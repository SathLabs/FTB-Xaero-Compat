package dev.satherov.ftbxaerocompat;


import com.mojang.logging.LogUtils;

import dev.architectury.event.events.common.PlayerEvent;
import dev.ftb.mods.ftbchunks.api.ClaimedChunk;
import dev.ftb.mods.ftbchunks.api.event.ClaimedChunkEvent;
import org.slf4j.Logger;
import xaero.map.WorldMapSession;
import xaero.map.highlight.HighlighterRegistry;
import xaero.map.world.MapDimension;

public final class FTBXaeroCompat {
    
    public static final String MOD_ID = "ftbxaerocompat";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static void init() {
        ClaimedChunkEvent.AFTER_CLAIM.register((source, chunk) -> FTBXaeroCompat.clearCache(chunk));
        ClaimedChunkEvent.AFTER_UNCLAIM.register((source, chunk) -> FTBXaeroCompat.clearCache(chunk));
        PlayerEvent.CHANGE_DIMENSION.register((player, before, after) -> {
            WorldMapSession session = WorldMapSession.getCurrentSession();
            MapDimension dim = session.getMapProcessor().getMapWorld().getDimension(before);
            if (dim != null) {
                dim.getHighlightHandler().clearCachedHashes();
            }
        });
    }
    
    private static void clearCache(ClaimedChunk chunk) {
        WorldMapSession session = WorldMapSession.getCurrentSession();
        MapDimension dim = session.getMapProcessor().getMapWorld().getDimension(chunk.getPos().dimension());
        if (dim == null) return;
        
        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                if (i == 0 && j == 0 || i * i != j * j) {
                    dim.getHighlightHandler().clearCachedHash(chunk.getPos().x() + i >> 5, chunk.getPos().z() + j >> 5);
                }
            }
        }
    }
    
    public static void registerHighlighters(HighlighterRegistry registry) {
        LOGGER.info("Registering FTB x Xaero highlighter");
        registry.register(new ClaimsHighlighter());
    }
}
