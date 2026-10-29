package dev.satherov.ftbxaerocompat.mixin;

import dev.satherov.ftbxaerocompat.FTBXaeroCompat;

import xaero.map.WorldMapSession;
import xaero.map.highlight.HighlighterRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = WorldMapSession.class, remap = false)
public class WorldMapSessionMixin {

    @Redirect(
            method = "init",
            at = @At(
                    value = "INVOKE",
                    target = "Lxaero/map/highlight/HighlighterRegistry;end()V"
            )
    )
    private void ftbxaerocompat$registerHighlighters(HighlighterRegistry registry) {
        FTBXaeroCompat.registerHighlighters(registry);
        registry.end();
    }
}
