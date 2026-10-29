package dev.satherov.ftbxaerocompat.minimap;

import dev.satherov.ftbxaerocompat.Claims;
import dev.satherov.ftbxaerocompat.config.CompatOptions;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import xaero.common.HudMod;
import xaero.common.minimap.highlight.ChunkHighlighter;
import xaero.hud.minimap.common.config.option.MinimapProfiledConfigOptions;
import xaero.hud.minimap.info.render.compile.InfoDisplayCompiler;
import xaero.lib.client.config.ClientConfigManager;
import org.jspecify.annotations.Nullable;

public class MinimapClaimsHighlighter extends ChunkHighlighter {

    private final int[] colors = new int[5];
    private Claims.@Nullable ChunkColors chunkColors;

    public MinimapClaimsHighlighter() {
        super(true);
    }

    private static ClientConfigManager configs() {
        return HudMod.INSTANCE.getHudConfigs().getClientConfigManager();
    }

    private static boolean isEnabled() {
        return MinimapClaimsHighlighter.configs().getEffective(CompatOptions.MINIMAP_CLAIMS);
    }

    @Override
    public boolean regionHasHighlights(ResourceKey<Level> dimension, int regionX, int regionZ) {
        return MinimapClaimsHighlighter.isEnabled() && Claims.hasRegion(regionX, regionZ);
    }

    @Override
    public boolean chunkIsHighlit(ResourceKey<Level> dimension, int chunkX, int chunkZ) {
        return MinimapClaimsHighlighter.isEnabled() && Claims.isClaimed(chunkX, chunkZ);
    }

    @Override
    protected int @Nullable [] getColors(ResourceKey<Level> dimension, int chunkX, int chunkZ) {
        if (!MinimapClaimsHighlighter.isEnabled()) {
            return null;
        }

        ClientConfigManager configs = MinimapClaimsHighlighter.configs();
        this.chunkColors = Claims.colors(chunkX, chunkZ, configs.getEffective(MinimapProfiledConfigOptions.OPAC_CLAIMS_FILL_OPACITY), configs.getEffective(MinimapProfiledConfigOptions.OPAC_CLAIMS_BORDER_OPACITY));
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
    public void addChunkHighlightTooltips(InfoDisplayCompiler compiler, ResourceKey<Level> dimension, int chunkX, int chunkZ, int width) {
        if (MinimapClaimsHighlighter.isEnabled() && Claims.isClaimed(chunkX, chunkZ)) {
            compiler.addLine(Claims.tooltip(chunkX, chunkZ));
        }
    }
}
