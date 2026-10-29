package dev.satherov.ftbxaerocompat.forge;

import dev.satherov.ftbxaerocompat.FTBXaeroCompat;

import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkConstants;

@Mod(FTBXaeroCompat.MOD_ID)
public class FTBXaeroCompatForge {

    public FTBXaeroCompatForge(FMLJavaModLoadingContext context) {
        context.registerExtensionPoint(IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(() -> NetworkConstants.IGNORESERVERONLY, (remote, isServer) -> true));
    }
}
