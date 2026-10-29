package dev.satherov.ftbxaerocompat.mixin;

import dev.satherov.ftbxaerocompat.config.CompatOptions;

import xaero.hud.common.config.channel.register.HudModConfigCommonRegistryHandler;
import xaero.lib.common.config.option.ConfigOptionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(value = HudModConfigCommonRegistryHandler.class, remap = false)
public class HudModConfigCommonRegistryHandlerMixin {

    @Inject(
            method = "registerProfiledOptions",
            at = @At("TAIL")
    )
    private void ftbxaerocompat$registerOptions(ConfigOptionManager manager, CallbackInfo ci) {
        CompatOptions.register(manager);
    }
}
