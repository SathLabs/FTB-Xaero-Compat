
package dev.satherov.ftbxaerocompat.neoforge;

import dev.satherov.ftbxaerocompat.FTBXaeroCompat;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = FTBXaeroCompat.MOD_ID, dist = Dist.CLIENT)
public class FTBXaeroCompatNeoForgeClient {
    
    public FTBXaeroCompatNeoForgeClient(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }
}
