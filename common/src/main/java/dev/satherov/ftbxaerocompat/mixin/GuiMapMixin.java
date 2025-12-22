package dev.satherov.ftbxaerocompat.mixin;

import dev.satherov.ftbxaerocompat.FTBClaimMenu;

import com.llamalad7.mixinextras.sugar.Local;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import xaero.map.MapProcessor;
import xaero.map.gui.GuiMap;
import xaero.map.gui.MapTileSelection;
import xaero.map.gui.dropdown.rightclick.RightClickOption;
import xaero.map.mods.SupportMods;

import java.util.ArrayList;

@Mixin(GuiMap.class)
public class GuiMapMixin {
    
    @Unique
    private GuiMap ftbxaerocompat$self() {
        return (GuiMap) (Object) this;
    }
    
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
            ),
            remap = false
    )
    public void getRightClickOptions(
            CallbackInfoReturnable<ArrayList<RightClickOption>> cir,
            @Local(name = "options") ArrayList<RightClickOption> options
    ) {
        if (SupportMods.pac()) return;
        FTBClaimMenu.addRightClickOptions(ftbxaerocompat$self(), options, mapTileSelection, mapProcessor);
    }
}
