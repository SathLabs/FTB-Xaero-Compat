package dev.satherov.ftbxaerocompat.mixin;

import dev.satherov.ftbxaerocompat.ClaimCounts;
import dev.satherov.ftbxaerocompat.ClaimMenu;

import net.minecraft.client.gui.GuiGraphicsExtractor;

import com.llamalad7.mixinextras.sugar.Local;
import xaero.map.MapProcessor;
import xaero.map.gui.GuiMap;
import xaero.map.gui.MapTileSelection;
import xaero.map.gui.dropdown.rightclick.RightClickOption;
import xaero.map.mods.SupportMods;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;

@Mixin(value = GuiMap.class, remap = false)
public class GuiMapMixin {

    @Shadow
    private MapTileSelection mapTileSelection;

    @Shadow
    private MapProcessor mapProcessor;

    @Inject(
            method = "getRightClickOptions",
            at = @At(
                    value = "INVOKE",
                    target = "Lxaero/map/mods/SupportMods;pac()Z",
                    shift = At.Shift.AFTER
            )
    )
    private void ftbxaerocompat$addClaimOptions(CallbackInfoReturnable<ArrayList<RightClickOption>> cir, @Local(name = "options") ArrayList<RightClickOption> options) {
        if (SupportMods.pac()) {
            return;
        }

        ClaimMenu.addRightClickOptions((GuiMap) (Object) this, options, this.mapTileSelection, this.mapProcessor);
    }

    @Inject(
            method = "renderPreDropdown",
            at = @At("TAIL")
    )
    private void ftbxaerocompat$renderClaimCounts(GuiGraphicsExtractor graphics, int scaledMouseX, int scaledMouseY, float partialTicks, CallbackInfo ci) {
        ClaimCounts.render(graphics);
    }
}
