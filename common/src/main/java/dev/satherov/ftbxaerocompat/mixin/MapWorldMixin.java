package dev.satherov.ftbxaerocompat.mixin;

import dev.satherov.ftbxaerocompat.MapWorldExtension;

import xaero.map.world.MapWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = MapWorld.class, remap = false)
public class MapWorldMixin implements MapWorldExtension {

    @Unique
    private boolean ftbxaerocompat$loaded = false;

    @Inject(
            method = "load",
            at = @At("TAIL")
    )
    private void ftbxaerocompat$markLoaded(CallbackInfo ci) {
        this.ftbxaerocompat$loaded = true;
    }

    @Override
    public boolean ftbxaerocompat$isLoaded() {
        return this.ftbxaerocompat$loaded;
    }
}
