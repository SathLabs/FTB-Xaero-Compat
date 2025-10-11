package dev.satherov.ftbxaerocompat.forge;

import dev.architectury.platform.forge.EventBuses;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import dev.satherov.ftbxaerocompat.FTBXaeroCompat;

@Mod(FTBXaeroCompat.MOD_ID)
public final class FTBXaeroCompatForgeClient {
    public FTBXaeroCompatForgeClient() {
        EventBuses.registerModEventBus(FTBXaeroCompat.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
    }
}
