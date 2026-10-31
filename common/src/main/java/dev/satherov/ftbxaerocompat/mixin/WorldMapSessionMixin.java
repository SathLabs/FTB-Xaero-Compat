package dev.satherov.ftbxaerocompat.mixin;

import dev.satherov.ftbxaerocompat.FTBXaeroCompat;

import com.llamalad7.mixinextras.sugar.Local;
import xaero.map.WorldMapSession;
import xaero.map.highlight.HighlighterRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = WorldMapSession.class, remap = false)
public class WorldMapSessionMixin {

    @Inject(
            method = "init",
            at = @At(
                    value = "INVOKE",
                    target = "Lxaero/map/highlight/HighlighterRegistry;end()V"
            )
    )
    private void ftbxaerocompat$registerHighlighters(CallbackInfo ci, @Local(name = "highlightRegistry") HighlighterRegistry registry) {
        FTBXaeroCompat.registerHighlighters(registry);
    }
}
