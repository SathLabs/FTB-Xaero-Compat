package dev.satherov.ftbxaerocompat;


import com.mojang.logging.LogUtils;

import org.slf4j.Logger;
import xaero.map.highlight.HighlighterRegistry;

public final class FTBXaeroCompat {
    
    public static final String MOD_ID = "ftbxaerocompat";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static void registerHighlighters(HighlighterRegistry registry) {
        LOGGER.info("Registering FTB x Xaero highlighter");
        registry.register(new ClaimsHighlighter());
    }
}
