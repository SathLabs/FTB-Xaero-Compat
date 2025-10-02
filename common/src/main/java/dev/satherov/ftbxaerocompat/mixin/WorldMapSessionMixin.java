package dev.satherov.ftbxaerocompat.mixin;

import dev.satherov.ftbxaerocompat.FTBXaeroCompat;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import xaero.map.WorldMapSession;
import xaero.map.highlight.HighlighterRegistry;

@Mixin(WorldMapSession.class)
public class WorldMapSessionMixin {
    
    @Redirect(
            method = "init",
            at = @At(
                    value = "INVOKE",
                    target = "Lxaero/map/highlight/HighlighterRegistry;end()V" 
            )
    )
    private void injectBeforeEnd(HighlighterRegistry instance) {
        FTBXaeroCompat.registerHighlighters(instance);
        instance.end();
    }
}
