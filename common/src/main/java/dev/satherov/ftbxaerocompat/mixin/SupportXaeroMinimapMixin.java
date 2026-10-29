package dev.satherov.ftbxaerocompat.mixin;

import dev.satherov.ftbxaerocompat.minimap.MinimapClaimsHighlighter;

import xaero.common.minimap.highlight.HighlighterRegistry;
import xaero.map.mods.SupportXaeroMinimap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = SupportXaeroMinimap.class, remap = false)
public class SupportXaeroMinimapMixin {

    @Inject(
            method = "registerMinimapHighlighters",
            at = @At("TAIL")
    )
    private void ftbxaerocompat$registerHighlighters(Object registry, CallbackInfo ci) {
        ((HighlighterRegistry) registry).register(new MinimapClaimsHighlighter());
    }
}
