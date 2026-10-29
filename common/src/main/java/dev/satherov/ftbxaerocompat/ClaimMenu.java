package dev.satherov.ftbxaerocompat;

import lombok.experimental.UtilityClass;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;

import dev.ftb.mods.ftbchunks.client.map.MapChunk;
import dev.ftb.mods.ftbchunks.client.map.MapDimension;
import dev.ftb.mods.ftbchunks.net.RequestChunkChangePacket;
import dev.ftb.mods.ftblibrary.math.XZ;
import xaero.map.MapProcessor;
import xaero.map.gui.GuiMap;
import xaero.map.gui.MapTileSelection;
import xaero.map.gui.dropdown.rightclick.RightClickOption;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@UtilityClass
public class ClaimMenu {

    private static final int MAX_CLAIM_DISTANCE = 7;
    private static final int MAX_REQUEST_LENGTH = 32;

    public static void addRightClickOptions(GuiMap screen, ArrayList<RightClickOption> options, @Nullable MapTileSelection selection, MapProcessor mapProcessor) {
        if (selection == null) {
            return;
        }

        if (mapProcessor.getMapWorld().isUsingCustomDimension()) {
            options.add(ClaimMenu.inertOption("gui.xaero_pac_claim_selection_out_of_dimension", options, screen));
            return;
        }

        Optional<MapDimension> dimension = MapDimension.getCurrent();
        LocalPlayer player = Minecraft.getInstance().player;
        if (dimension.isEmpty() || player == null) {
            return;
        }

        int fromX = player.chunkPosition().x;
        int fromZ = player.chunkPosition().z;
        int left = Math.max(selection.getLeft(), fromX - ClaimMenu.MAX_CLAIM_DISTANCE);
        int top = Math.max(selection.getTop(), fromZ - ClaimMenu.MAX_CLAIM_DISTANCE);
        int right = Math.min(Math.min(selection.getRight(), fromX + ClaimMenu.MAX_CLAIM_DISTANCE), left + ClaimMenu.MAX_REQUEST_LENGTH - 1);
        int bottom = Math.min(Math.min(selection.getBottom(), fromZ + ClaimMenu.MAX_CLAIM_DISTANCE), top + ClaimMenu.MAX_REQUEST_LENGTH - 1);
        if (left > right || top > bottom) {
            options.add(ClaimMenu.inertOption("gui.xaero_pac_claim_selection_out_of_range", options, screen));
            return;
        }

        Set<XZ> chunks = new HashSet<>();
        List<MapChunk> mapChunks = new ArrayList<>();
        for (int chunkX = left; chunkX <= right; chunkX++) {
            for (int chunkZ = top; chunkZ <= bottom; chunkZ++) {
                XZ chunk = XZ.of(chunkX, chunkZ);
                chunks.add(chunk);
                mapChunks.add(dimension.get().getRegion(XZ.regionFromChunk(chunkX, chunkZ)).getChunkForAbsoluteChunkPos(chunk));
            }
        }

        if (mapChunks.stream().anyMatch(chunk -> chunk.getClaimedDate().isEmpty())) {
            options.add(ClaimMenu.requestOption("gui.xaero_pac_claim_chunks", options, screen, RequestChunkChangePacket.ChunkChangeOp.CLAIM, chunks));
        }

        if (mapChunks.stream().anyMatch(chunk -> chunk.getClaimedDate().isPresent())) {
            options.add(ClaimMenu.requestOption("gui.xaero_pac_unclaim_chunks", options, screen, RequestChunkChangePacket.ChunkChangeOp.UNCLAIM, chunks));
        }

        if (mapChunks.stream().anyMatch(chunk -> chunk.getClaimedDate().isPresent() && chunk.getForceLoadedDate().isEmpty())) {
            options.add(ClaimMenu.requestOption("gui.xaero_pac_forceload_chunks", options, screen, RequestChunkChangePacket.ChunkChangeOp.LOAD, chunks));
        }

        if (mapChunks.stream().anyMatch(chunk -> chunk.getForceLoadedDate().isPresent())) {
            options.add(ClaimMenu.requestOption("gui.xaero_pac_unforceload_chunks", options, screen, RequestChunkChangePacket.ChunkChangeOp.UNLOAD, chunks));
        }
    }

    private static RightClickOption inertOption(String key, ArrayList<RightClickOption> options, GuiMap screen) {
        return new RightClickOption(key, options.size(), screen) {

            @Override
            public void onAction(Screen screen) { }
        };
    }

    private static RightClickOption requestOption(String key, ArrayList<RightClickOption> options, GuiMap screen, RequestChunkChangePacket.ChunkChangeOp operation, Set<XZ> chunks) {
        return new RightClickOption(key, options.size(), screen) {

            @Override
            public void onAction(Screen screen) {
                new RequestChunkChangePacket(operation, chunks).sendToServer();
            }
        };
    }
}
