package dev.satherov.ftbxaerocompat.fabric;

import net.fabricmc.api.ModInitializer;

import dev.satherov.ftbxaerocompat.FTBXaeroCompat;

public final class FTBXaeroCompatFabric implements ModInitializer {
    
    @Override
    public void onInitialize() {
        FTBXaeroCompat.init();
    }
}
