package dev.satherov.ftbxaerocompat;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import xaero.map.highlight.HighlighterRegistry;

@Slf4j
@UtilityClass
public class FTBXaeroCompat {

    public static final String MOD_ID = "ftbxaerocompat";

    public static void registerHighlighters(HighlighterRegistry registry) {
        FTBXaeroCompat.log.info("Registering the FTB claims highlighter");
        registry.register(new ClaimsHighlighter());
    }
}
