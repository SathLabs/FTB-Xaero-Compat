package dev.satherov.ftbxaerocompat;

import lombok.experimental.UtilityClass;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import dev.ftb.mods.ftbchunks.client.FTBChunksClient;
import dev.ftb.mods.ftbchunks.net.SendGeneralDataPacket;

@UtilityClass
public class ClaimCounts {

    private static final int X = 35;
    private static final int Y = 5;
    private static final int LINE_HEIGHT = 10;
    private static final int COLOR = 0xFFFFFFFF;

    public static void render(GuiGraphics graphics) {
        SendGeneralDataPacket.GeneralChunkData data = FTBChunksClient.INSTANCE.getGeneralChunkData();
        if (data == null) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        graphics.drawString(minecraft.font, Component.translatable("gui.ftbxaerocompat_claimed", data.claimed(), data.maxClaimChunks()), ClaimCounts.X, ClaimCounts.Y, ClaimCounts.COLOR);
        graphics.drawString(minecraft.font, Component.translatable("gui.ftbxaerocompat_force_loaded", data.loaded(), data.maxForceLoadChunks()), ClaimCounts.X, ClaimCounts.Y + ClaimCounts.LINE_HEIGHT, ClaimCounts.COLOR);
    }
}
