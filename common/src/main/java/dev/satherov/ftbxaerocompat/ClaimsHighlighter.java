package dev.satherov.ftbxaerocompat;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import xaero.lib.client.config.ClientConfigManager;
import xaero.map.WorldMap;
import xaero.map.common.config.option.WorldMapProfiledConfigOptions;
import xaero.map.highlight.ChunkHighlighter;
import org.jspecify.annotations.Nullable;

import java.util.List;

public class ClaimsHighlighter extends ChunkHighlighter {

    private final int[] colors = new int[5];
    private Claims.@Nullable ChunkColors chunkColors;

    public ClaimsHighlighter() {
        super(true);
    }

    private static ClientConfigManager configs() {
        return WorldMap.INSTANCE.getConfigs().getClientConfigManager();
    }

    private static boolean isEnabled() {
        return ClaimsHighlighter.configs().getEffective(WorldMapProfiledConfigOptions.OPAC_CLAIMS);
    }

    private static int fillOpacity() {
        return ClaimsHighlighter.configs().getEffective(WorldMapProfiledConfigOptions.OPAC_CLAIMS_FILL_OPACITY);
    }

    private static int borderOpacity() {
        return ClaimsHighlighter.configs().getEffective(WorldMapProfiledConfigOptions.OPAC_CLAIMS_BORDER_OPACITY);
    }

    @Override
    public boolean regionHasHighlights(ResourceKey<Level> dimension, int regionX, int regionZ) {
        return Claims.hasRegion(regionX, regionZ);
    }

    @Override
    public boolean chunkIsHighlit(ResourceKey<Level> dimension, int chunkX, int chunkZ) {
        return ClaimsHighlighter.isEnabled() && Claims.isClaimed(chunkX, chunkZ);
    }

    @Override
    protected int @Nullable [] getColors(ResourceKey<Level> dimension, int chunkX, int chunkZ) {
        if (!ClaimsHighlighter.isEnabled()) {
            return null;
        }

        this.chunkColors = Claims.colors(chunkX, chunkZ, ClaimsHighlighter.fillOpacity(), ClaimsHighlighter.borderOpacity());
        if (this.chunkColors == null) {
            return null;
        }

        this.colors[0] = this.chunkColors.fill();
        this.colors[1] = this.chunkColors.top();
        this.colors[2] = this.chunkColors.right();
        this.colors[3] = this.chunkColors.bottom();
        this.colors[4] = this.chunkColors.left();
        return this.colors;
    }

    @Override
    public int @Nullable [] getChunkHighlitColor(ResourceKey<Level> dimension, int chunkX, int chunkZ) {
        int[] result = super.getChunkHighlitColor(dimension, chunkX, chunkZ);
        if (result == null || this.chunkColors == null || !Claims.isForceLoaded(chunkX, chunkZ)) {
            return result;
        }

        for (int pixelX = 1; pixelX < 15; pixelX++) {
            for (int pixelZ = 1; pixelZ < 15; pixelZ++) {
                if (Claims.isStripe(pixelX, pixelZ)) {
                    this.setResult(pixelX, pixelZ, this.chunkColors.border());
                }
            }
        }

        return result;
    }

    @Override
    public int calculateRegionHash(ResourceKey<Level> dimension, int regionX, int regionZ) {
        if (!ClaimsHighlighter.isEnabled()) {
            return 0;
        }

        return Claims.regionHash(regionX, regionZ, ClaimsHighlighter.fillOpacity(), ClaimsHighlighter.borderOpacity());
    }

    @Override
    public Component getChunkHighlightSubtleTooltip(ResourceKey<Level> dimension, int chunkX, int chunkZ) {
        return Claims.tooltip(chunkX, chunkZ);
    }

    @Override
    public @Nullable Component getChunkHighlightBluntTooltip(ResourceKey<Level> dimension, int chunkX, int chunkZ) {
        return null;
    }

    @Override
    public void addMinimapBlockHighlightTooltips(List<Component> tooltips, ResourceKey<Level> dimension, int blockX, int blockZ, int width) { }
}
