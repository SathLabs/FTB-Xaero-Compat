package dev.satherov.ftbxaerocompat.config;

import lombok.experimental.UtilityClass;

import net.minecraft.network.chat.Component;

import xaero.lib.common.config.option.BooleanConfigOption;
import xaero.lib.common.config.option.ConfigOption;
import xaero.lib.common.config.option.ConfigOptionManager;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class CompatOptions {

    private static final List<ConfigOption<?>> OPTIONS = new ArrayList<>();

    public static final BooleanConfigOption MINIMAP_CLAIMS = ((BooleanConfigOption.Builder) BooleanConfigOption.Builder.begin()
            .setId("ftbxaerocompat_minimap_claims")
            .setDefaultValue(true)
            .setDisplayName(Component.translatable("gui.ftbxaerocompat_minimap_claims"))
            .setTooltip(Component.translatable("gui.ftbxaerocompat_box_minimap_claims"))
    ).build(CompatOptions.OPTIONS);

    public static void register(ConfigOptionManager manager) {
        for (ConfigOption<?> option : CompatOptions.OPTIONS) {
            manager.register(option);
        }
    }
}
