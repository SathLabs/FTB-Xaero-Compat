package dev.satherov.ftbxaerocompat;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import dev.ftb.mods.ftbchunks.api.ClaimedChunk;
import dev.ftb.mods.ftbchunks.api.ClaimedChunkManager;
import dev.ftb.mods.ftbchunks.api.FTBChunksAPI;
import dev.ftb.mods.ftblibrary.math.ChunkDimPos;
import dev.ftb.mods.ftbteams.api.Team;
import dev.ftb.mods.ftbteams.api.property.TeamProperties;
import dev.ftb.mods.ftbteams.data.AbstractTeamBase;
import org.jetbrains.annotations.Nullable;
import xaero.map.WorldMap;
import xaero.map.highlight.ChunkHighlighter;

import java.util.List;
import java.util.UUID;

public class ClaimsHighlighter extends ChunkHighlighter {
    
    public ClaimsHighlighter() {
        super(true);
    }
    
    @Override
    public boolean regionHasHighlights(ResourceKey<Level> key, int regionX, int regionZ) {
        if (!WorldMap.settings.displayClaims) return false;
        ClaimedChunkManager manager = FTBChunksAPI.api().getManager();
        
        final int baseX = regionX * 32;
        final int baseZ = regionZ * 32;
        
        for (int dx = 0; dx < 32; dx++) {
            for (int dz = 0; dz < 32; dz++) {
                if (manager.getChunk(new ChunkDimPos(key, baseX + dx, baseZ + dz)) != null) {
                    return true;
                }
            }
        }
        return false;
    }
    
    @Override
    public boolean chunkIsHighlit(ResourceKey<Level> key, int x, int z) {
        if (!WorldMap.settings.displayClaims) return false;
        ChunkDimPos pos = new ChunkDimPos(key, x, z);
        return FTBChunksAPI.api().getManager().getChunk(pos) != null;
    }
    
    @Override
    protected int[] getColors(ResourceKey<Level> key, int x, int z) {
        if (!WorldMap.settings.displayClaims) return null;
        
        ClaimedChunk chunk = FTBChunksAPI.api().getManager().getChunk(new ChunkDimPos(key, x, z));
        if (chunk == null) return null;
        
        ClaimedChunk top = FTBChunksAPI.api().getManager().getChunk(new ChunkDimPos(key, x, z - 1));
        ClaimedChunk right = FTBChunksAPI.api().getManager().getChunk(new ChunkDimPos(key, x + 1, z));
        ClaimedChunk bottom = FTBChunksAPI.api().getManager().getChunk(new ChunkDimPos(key, x, z + 1));
        ClaimedChunk left = FTBChunksAPI.api().getManager().getChunk(new ChunkDimPos(key, x - 1, z));
        
        int rgb = chunk.getTeamData().getTeam().getProperties().get(TeamProperties.COLOR).rgb() & 0xFFFFFF;
        
        int packed = ((rgb & 0xFF) << 24) | ((rgb >> 8 & 0xFF) << 16) | ((rgb >> 16 & 0xFF) << 8);
        int fillOpacity = 255 * WorldMap.settings.claimsFillOpacity / 100;
        int borderOpacity = 255 * WorldMap.settings.claimsBorderOpacity / 100;
        
        int center  = (packed & 0xFFFFFF00) | fillOpacity;
        int side    = (packed & 0xFFFFFF00) | borderOpacity;
        
        this.resultStore[0] = center;
        this.resultStore[1] = sameOwner(top, chunk) ? center : side;
        this.resultStore[2] = sameOwner(right, chunk) ? center : side;
        this.resultStore[3] = sameOwner(bottom, chunk) ? center : side;
        this.resultStore[4] = sameOwner(left, chunk) ? center : side;
        
        return this.resultStore;
    }
    
    private static boolean sameOwner(ClaimedChunk a, ClaimedChunk b) {
        if (a == null || b == null) return false;
        return a.getTeamData().getTeam().getId().equals(b.getTeamData().getTeam().getId());
    }
    
    @Override
    public int calculateRegionHash(ResourceKey<Level> key, int regionX, int regionZ) {
        if (!WorldMap.settings.displayClaims) return 0;
        
        ClaimedChunkManager manager = FTBChunksAPI.api().getManager();
        if (manager == null) return 0;
        
        final int chunkX = regionX * 32;
        final int chunkZ = regionZ * 32;
        
        long acc = 0L;
        acc = acc * 37L + WorldMap.settings.claimsBorderOpacity;
        acc = acc * 37L + WorldMap.settings.claimsFillOpacity;
        
        
        for (int i = 0; i < 32; i++) {
            acc = accountChunk(acc, manager.getChunk(new ChunkDimPos(key, chunkX + i, chunkZ - 1)));
            acc = accountChunk(acc, manager.getChunk(new ChunkDimPos(key, chunkX + 32, chunkZ + i)));
            acc = accountChunk(acc, manager.getChunk(new ChunkDimPos(key, chunkX + i, chunkZ + 32)));
            acc = accountChunk(acc, manager.getChunk(new ChunkDimPos(key, chunkX - 1, chunkZ + i)));
            
            for (int j = 0; j < 32; j++) {
                ClaimedChunk c = manager.getChunk(new ChunkDimPos(key, chunkX + i, chunkZ + j));
                acc = accountChunk(acc, c);
            }
        }
        
        return (int) (acc >> 32) * 37 + (int) (acc & 0xFFFFFFFFL);
    }
    
    private long accountChunk(long acc, @Nullable ClaimedChunk chunk) {
        if (chunk != null) {
            Team team = chunk.getTeamData().getTeam();
            UUID teamId = team.getId();
            int color = team.getProperties().get(TeamProperties.COLOR).rgb();
            
            acc += teamId.getLeastSignificantBits();
            acc *= 37L;
            acc += teamId.getMostSignificantBits();
            acc *= 37L;
            
            acc += color;
            acc *= 37L;
            
            acc += chunk.isForceLoaded() ? 1L : 0L;
            acc *= 37L;
        }
        return acc * 37L;
    }
    
    
    @Override
    public Component getChunkHighlightSubtleTooltip(ResourceKey<Level> key, int x, int z) {
        ClaimedChunk chunk = FTBChunksAPI.api().getManager().getChunk(new ChunkDimPos(key, x, z));
        if (chunk == null) return null;
        AbstractTeamBase team = (AbstractTeamBase) chunk.getTeamData().getTeam();
        return Component.literal(team.getDisplayName());
    }
    
    @Override
    public Component getChunkHighlightBluntTooltip(ResourceKey<Level> resourceKey, int x, int t) {
        return null;
    }
    
    @Override
    public void addMinimapBlockHighlightTooltips(List<Component> list, ResourceKey<Level> resourceKey, int x, int z, int width) { }
}
