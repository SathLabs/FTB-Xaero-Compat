package dev.satherov.ftbxaerocompat;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import dev.ftb.mods.ftbchunks.client.map.MapChunk;
import dev.ftb.mods.ftbchunks.client.map.MapDimension;
import dev.ftb.mods.ftbchunks.client.map.MapRegion;
import dev.ftb.mods.ftblibrary.math.XZ;
import dev.ftb.mods.ftbteams.api.Team;
import dev.ftb.mods.ftbteams.api.property.TeamProperties;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import xaero.map.WorldMap;
import xaero.map.highlight.ChunkHighlighter;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
public class ClaimsHighlighter extends ChunkHighlighter {
    
    public ClaimsHighlighter() {
        super(true);
    }
    
    @Override
    public boolean regionHasHighlights(ResourceKey<Level> key, int regionX, int regionZ) {
        return true;
    }
    
    @Override
    public boolean chunkIsHighlit(ResourceKey<Level> key, int x, int z) {
        if (!WorldMap.settings.displayClaims) return false;
        Optional<MapDimension> opt = MapDimension.getCurrent();
        if (opt.isEmpty()) return false;
        MapChunk chunk = getChunk(opt.get(), x, z);
        return chunk != null && chunk.getTeam().isPresent();
    }
    
    @Override
    protected int[] getColors(ResourceKey<Level> key, int x, int z) {
        if (!WorldMap.settings.displayClaims) return null;
        Optional<MapDimension> opt = MapDimension.getCurrent();
        if (opt.isEmpty()) return null;
        MapDimension dim = opt.get();
        
        MapChunk centerChunk = getChunk(dim, x, z);
        if (centerChunk == null) return null;
        
        Optional<Team> optTeam = centerChunk.getTeam();
        if (optTeam.isEmpty()) return null;
        Team team = optTeam.get();
        
        MapChunk top    = getChunk(dim, x, z - 1);
        MapChunk right  = getChunk(dim, x + 1, z);
        MapChunk bottom = getChunk(dim, x, z + 1);
        MapChunk left   = getChunk(dim, x - 1, z);
        
        int rgb = team.getProperties().get(TeamProperties.COLOR).rgb() & 0xFFFFFF;
        int packed = ((rgb & 0xFF) << 24) | ((rgb >> 8 & 0xFF) << 16) | ((rgb >> 16 & 0xFF) << 8);
        int fillOpacity = 255 * WorldMap.settings.claimsFillOpacity / 100;
        int borderOpacity = 255 * WorldMap.settings.claimsBorderOpacity / 100;
        
        int fill  = (packed & 0xFFFFFF00) | fillOpacity;
        int edge  = (packed & 0xFFFFFF00) | borderOpacity;
        
        this.resultStore[0] = fill;
        this.resultStore[1] = sameOwner(top, centerChunk)    ? fill : edge;   // top
        this.resultStore[2] = sameOwner(right, centerChunk)  ? fill : edge;   // right
        this.resultStore[3] = sameOwner(bottom, centerChunk) ? fill : edge;   // bottom
        this.resultStore[4] = sameOwner(left, centerChunk)   ? fill : edge;   // left
        return this.resultStore;
    }
    
    
    private static boolean sameOwner(MapChunk a, MapChunk b) {
        if (a == null || b == null) return false;
        Optional<Team> aTeam = a.getTeam();
        Optional<Team> bTeam = b.getTeam();
        if (aTeam.isEmpty() || bTeam.isEmpty()) return false;
        return aTeam.get().getId().equals(bTeam.get().getId());
    }
    
    @Override
    public int calculateRegionHash(ResourceKey<Level> key, int regionX, int regionZ) {
        if (!WorldMap.settings.displayClaims) return 0;
        Optional<MapDimension> opt = MapDimension.getCurrent();
        if (opt.isEmpty()) return 0;
        MapDimension dim = opt.get();
        
        final int chunkX = regionX * 32;
        final int chunkZ = regionZ * 32;
        
        long acc = 0L;
        acc = acc * 37L + WorldMap.settings.claimsBorderOpacity;
        acc = acc * 37L + WorldMap.settings.claimsFillOpacity;
        
        for (int i = 0; i < 32; i++) {
            acc = accountChunk(acc, getChunk(dim, chunkX + i, chunkZ - 1));   // top neighbor row
            acc = accountChunk(acc, getChunk(dim, chunkX + 32, chunkZ + i));  // right neighbor col
            acc = accountChunk(acc, getChunk(dim, chunkX + i, chunkZ + 32));  // bottom neighbor row
            acc = accountChunk(acc, getChunk(dim, chunkX - 1, chunkZ + i));   // left neighbor col
            
            for (int j = 0; j < 32; j++) {
                acc = accountChunk(acc, getChunk(dim, chunkX + i, chunkZ + j));
            }
        }
        
        return (int)(acc >> 32) * 37 + (int)(acc & 0xFFFFFFFFL);
    }
    
    
    private long accountChunk(long acc, @Nullable MapChunk chunk) {
        if (chunk != null) {
            Optional<Team> optional = chunk.getTeam();
            if (optional.isEmpty()) return acc * 37L;
            
            Team team = optional.get();
            
            UUID teamId = team.getId();
            int color = team.getProperties().get(TeamProperties.COLOR).rgb();
            
            acc += teamId.getLeastSignificantBits();
            acc *= 37L;
            acc += teamId.getMostSignificantBits();
            acc *= 37L;
            
            acc += color;
            acc *= 37L;
            
            acc += chunk.getForceLoadedDate().isPresent() ? 1L : 0L;
            acc *= 37L;
        }
        return acc * 37L;
    }
    
    
    @Override
    public Component getChunkHighlightSubtleTooltip(ResourceKey<Level> key, int x, int z) {
        Optional<MapDimension> opt = MapDimension.getCurrent();
        if (opt.isEmpty()) return null;
        MapChunk chunk = getChunk(opt.get(), x, z);
        if (chunk == null) return null;
        return chunk.getTeam().map(Team::getColoredName).orElse(Component.empty());
    }
    
    
    @Override
    public Component getChunkHighlightBluntTooltip(ResourceKey<Level> resourceKey, int x, int t) {
        return null;
    }
    
    @Override
    public void addMinimapBlockHighlightTooltips(List<Component> list, ResourceKey<Level> resourceKey, int x, int z, int width) { }
    
    @Nullable
    private static MapChunk getChunk(MapDimension dim, int chunkX, int chunkZ) {
        MapRegion r = dim.getRegion(XZ.regionFromChunk(chunkX, chunkZ));
        return r.getChunkForAbsoluteChunkPos(XZ.of(chunkX, chunkZ));
    }
    
}