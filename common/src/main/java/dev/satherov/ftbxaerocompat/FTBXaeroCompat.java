package dev.satherov.ftbxaerocompat;


import lombok.extern.slf4j.Slf4j;

import xaero.map.highlight.HighlighterRegistry;

@Slf4j
public final class FTBXaeroCompat {
    
    public static final String MOD_ID = "ftbxaerocompat";
    
    public static void registerHighlighters(HighlighterRegistry registry) {
        log.info("Registering FTB x Xaero highlighter");
        registry.register(new ClaimsHighlighter());
    }
}
