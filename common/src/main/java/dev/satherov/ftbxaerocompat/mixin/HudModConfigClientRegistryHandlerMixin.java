package dev.satherov.ftbxaerocompat.mixin;

import dev.satherov.ftbxaerocompat.config.CompatOptions;

import xaero.hud.config.channel.register.HudModConfigClientRegistryHandler;
import xaero.lib.client.config.listener.ClientConfigChangeListener;
import xaero.lib.client.config.option.ui.ConfigOptionUITypeManager;
import xaero.lib.client.config.option.ui.type.BuiltInConfigOptionUITypes;
import xaero.map.mods.SupportMods;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(value = HudModConfigClientRegistryHandler.class, remap = false)
public class HudModConfigClientRegistryHandlerMixin {

    @Inject(
            method = "registerConfigOptionUITypes",
            at = @At("TAIL")
    )
    private void ftbxaerocompat$registerUITypes(ConfigOptionUITypeManager manager, CallbackInfo ci) {
        manager.registerUIType(CompatOptions.MINIMAP_CLAIMS, BuiltInConfigOptionUITypes.TOGGLE);
    }

    @Inject(
            method = "registerClientOptionChangeHandlers",
            at = @At("TAIL")
    )
    private void ftbxaerocompat$registerChangeHandlers(ClientConfigChangeListener listener, CallbackInfo ci) {
        listener.register(CompatOptions.MINIMAP_CLAIMS, config -> SupportMods.xaeroMinimap.onClearHighlightHashes());
    }
}
