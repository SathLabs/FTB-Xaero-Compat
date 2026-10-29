package dev.satherov.ftbxaerocompat.mixin;

import dev.satherov.ftbxaerocompat.config.CompatOptions;

import xaero.common.gui.GuiMinimapOverlaysSettings;
import xaero.lib.client.gui.config.EditConfigScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;

@Pseudo
@Mixin(value = GuiMinimapOverlaysSettings.class, remap = false)
public abstract class GuiMinimapOverlaysSettingsMixin extends EditConfigScreen {

    private GuiMinimapOverlaysSettingsMixin() {
        super(null, null, null, null, null);
    }

    @Inject(
            method = "<init>",
            at = @At("TAIL")
    )
    private void ftbxaerocompat$addEntries(CallbackInfo ci) {
        this.entries = Arrays.copyOf(this.entries, this.entries.length + 1);
        this.entries[this.entries.length - 1] = this.optionEntry(CompatOptions.MINIMAP_CLAIMS);
    }
}
