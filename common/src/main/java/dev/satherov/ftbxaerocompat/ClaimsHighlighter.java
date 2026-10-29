package dev.satherov.ftbxaerocompat;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import dev.ftb.mods.ftbchunks.client.map.MapChunk;
import dev.ftb.mods.ftbchunks.client.map.MapDimension;
import dev.ftb.mods.ftblibrary.math.XZ;
import dev.ftb.mods.ftbteams.api.Team;
import dev.ftb.mods.ftbteams.api.property.TeamProperties;
import xaero.lib.client.config.ClientConfigManager;
import xaero.map.WorldMap;
import xaero.map.common.config.option.WorldMapProfiledConfigOptions;
import xaero.map.highlight.ChunkHighlighter;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ClaimsHighlighter extends ChunkHighlighter {

    private static final int REGION_SIZE = 32;

    private final int[] colors = new int[5];

    public ClaimsHighlighter() {
        super(true);
    }

    private static ClientConfigManager configs() {
        return WorldMap.INSTANCE.getConfigs().getClientConfigManager();
    }

    private static boolean isEnabled() {
        return ClaimsHighlighter.configs().getEffective(WorldMapProfiledConfigOptions.OPAC_CLAIMS);
    }

    private static @Nullable MapChunk getChunk(MapDimension dimension, int chunkX, int chunkZ) {
        return dimension.getRegion(XZ.regionFromChunk(chunkX, chunkZ)).getChunkForAbsoluteChunkPos(XZ.of(chunkX, chunkZ));
    }

    private static boolean isSameOwner(@Nullable MapChunk first, @Nullable MapChunk second) {
        if (first == null || second == null) {
            return false;
        }

        Optional<Team> firstTeam = first.getTeam();
        Optional<Team> secondTeam = second.getTeam();
        return firstTeam.isPresent() && secondTeam.isPresent() && firstTeam.get().getId().equals(secondTeam.get().getId());
    }

    private static long hashChunk(long hash, @Nullable MapChunk chunk) {
        if (chunk == null) {
            return hash * 37L;
        }

        Optional<Team> team = chunk.getTeam();
        if (team.isEmpty()) {
            return hash * 37L * 37L;
        }

        UUID teamId = team.get().getId();
        long result = hash;
        result = (result + teamId.getLeastSignificantBits()) * 37L;
        result = (result + teamId.getMostSignificantBits()) * 37L;
        result = (result + team.get().getProperties().get(TeamProperties.COLOR).rgb()) * 37L;
        result = (result + (chunk.getForceLoadedDate().isPresent() ? 1L : 0L)) * 37L;
        return result * 37L;
    }

    @Override
    public boolean regionHasHighlights(ResourceKey<Level> dimension, int regionX, int regionZ) {
        return MapDimension.getCurrent().map(current -> current.getRegions().get(XZ.of(regionX, regionZ)) != null).orElse(false);
    }

    @Override
    public boolean chunkIsHighlit(ResourceKey<Level> dimension, int chunkX, int chunkZ) {
        if (!ClaimsHighlighter.isEnabled()) {
            return false;
        }

        return MapDimension.getCurrent().map(current -> {
            MapChunk chunk = ClaimsHighlighter.getChunk(current, chunkX, chunkZ);
            return chunk != null && chunk.getTeam().isPresent();
        }).orElse(false);
    }

    @Override
    protected int @Nullable [] getColors(ResourceKey<Level> dimension, int chunkX, int chunkZ) {
        if (!ClaimsHighlighter.isEnabled()) {
            return null;
        }

        Optional<MapDimension> current = MapDimension.getCurrent();
        if (current.isEmpty()) {
            return null;
        }

        MapChunk center = ClaimsHighlighter.getChunk(current.get(), chunkX, chunkZ);
        if (center == null) {
            return null;
        }

        Optional<Team> team = center.getTeam();
        if (team.isEmpty()) {
            return null;
        }

        int rgb = team.get().getProperties().get(TeamProperties.COLOR).rgb() & 0xFFFFFF;
        int packed = ((rgb & 0xFF) << 24) | ((rgb >> 8 & 0xFF) << 16) | ((rgb >> 16 & 0xFF) << 8);
        int fill = packed | (255 * ClaimsHighlighter.configs().getEffective(WorldMapProfiledConfigOptions.OPAC_CLAIMS_FILL_OPACITY) / 100);
        int border = packed | (255 * ClaimsHighlighter.configs().getEffective(WorldMapProfiledConfigOptions.OPAC_CLAIMS_BORDER_OPACITY) / 100);

        this.colors[0] = fill;
        this.colors[1] = ClaimsHighlighter.isSameOwner(center, ClaimsHighlighter.getChunk(current.get(), chunkX, chunkZ - 1)) ? fill : border;
        this.colors[2] = ClaimsHighlighter.isSameOwner(center, ClaimsHighlighter.getChunk(current.get(), chunkX + 1, chunkZ)) ? fill : border;
        this.colors[3] = ClaimsHighlighter.isSameOwner(center, ClaimsHighlighter.getChunk(current.get(), chunkX, chunkZ + 1)) ? fill : border;
        this.colors[4] = ClaimsHighlighter.isSameOwner(center, ClaimsHighlighter.getChunk(current.get(), chunkX - 1, chunkZ)) ? fill : border;
        return this.colors;
    }

    @Override
    public int calculateRegionHash(ResourceKey<Level> dimension, int regionX, int regionZ) {
        if (!ClaimsHighlighter.isEnabled()) {
            return 0;
        }

        Optional<MapDimension> current = MapDimension.getCurrent();
        if (current.isEmpty()) {
            return 0;
        }

        int chunkX = regionX * ClaimsHighlighter.REGION_SIZE;
        int chunkZ = regionZ * ClaimsHighlighter.REGION_SIZE;
        long hash = 0L;
        hash = hash * 37L + ClaimsHighlighter.configs().getEffective(WorldMapProfiledConfigOptions.OPAC_CLAIMS_BORDER_OPACITY);
        hash = hash * 37L + ClaimsHighlighter.configs().getEffective(WorldMapProfiledConfigOptions.OPAC_CLAIMS_FILL_OPACITY);

        for (int i = 0; i < ClaimsHighlighter.REGION_SIZE; i++) {
            hash = ClaimsHighlighter.hashChunk(hash, ClaimsHighlighter.getChunk(current.get(), chunkX + i, chunkZ - 1));
            hash = ClaimsHighlighter.hashChunk(hash, ClaimsHighlighter.getChunk(current.get(), chunkX + ClaimsHighlighter.REGION_SIZE, chunkZ + i));
            hash = ClaimsHighlighter.hashChunk(hash, ClaimsHighlighter.getChunk(current.get(), chunkX + i, chunkZ + ClaimsHighlighter.REGION_SIZE));
            hash = ClaimsHighlighter.hashChunk(hash, ClaimsHighlighter.getChunk(current.get(), chunkX - 1, chunkZ + i));

            for (int j = 0; j < ClaimsHighlighter.REGION_SIZE; j++) {
                hash = ClaimsHighlighter.hashChunk(hash, ClaimsHighlighter.getChunk(current.get(), chunkX + i, chunkZ + j));
            }
        }

        return (int) (hash >> 32) * 37 + (int) (hash & 0xFFFFFFFFL);
    }

    @Override
    public Component getChunkHighlightSubtleTooltip(ResourceKey<Level> dimension, int chunkX, int chunkZ) {
        Optional<MapDimension> current = MapDimension.getCurrent();
        if (current.isEmpty()) {
            return Component.empty();
        }

        MapChunk chunk = ClaimsHighlighter.getChunk(current.get(), chunkX, chunkZ);
        if (chunk == null) {
            return Component.empty();
        }

        Optional<Team> team = chunk.getTeam();
        if (team.isEmpty()) {
            return Component.empty();
        }

        MutableComponent tooltip = Component.empty().withStyle(team.get().getProperty(TeamProperties.COLOR).toStyle()).append(team.get().getColoredName());
        if (chunk.getForceLoadedDate().isPresent()) {
            tooltip.append(Component.literal(" □ ").append(Component.translatable("ftbchunks.gui.force_loaded")));
        }

        return tooltip;
    }

    @Override
    public @Nullable Component getChunkHighlightBluntTooltip(ResourceKey<Level> dimension, int chunkX, int chunkZ) {
        return null;
    }

    @Override
    public void addMinimapBlockHighlightTooltips(List<Component> tooltips, ResourceKey<Level> dimension, int blockX, int blockZ, int width) { }
}
