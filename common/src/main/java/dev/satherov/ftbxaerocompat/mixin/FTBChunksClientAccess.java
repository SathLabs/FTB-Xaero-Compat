package dev.satherov.ftbxaerocompat.mixin;

import dev.ftb.mods.ftbchunks.client.FTBChunksClient;
import dev.ftb.mods.ftbchunks.net.SendGeneralDataPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = FTBChunksClient.class, remap = false)
public interface FTBChunksClientAccess {

    @Accessor("generalChunkData")
    SendGeneralDataPacket.GeneralChunkData ftbxaerocompat$generalChunkData();
}
