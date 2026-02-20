package dev.satherov.ftbxaerocompat.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.satherov.ftbxaerocompat.duck.MapWorldDuck;
import xaero.map.world.MapWorld;

@Mixin(value = MapWorld.class, remap = false)
public class MapWorldMixin implements MapWorldDuck {
    
    @Unique
    private boolean ftbxaerocompat$loaded = false;
    
    @Inject(
            method = "load",
            at = @At("TAIL")
    )
    public void createDimensionUnsynced(CallbackInfo ci) {
        ftbxaerocompat$loaded = true;
    }

    @Override
    public boolean ftbxaerocompat$isLoaded() {
        return ftbxaerocompat$loaded;
    }
}
