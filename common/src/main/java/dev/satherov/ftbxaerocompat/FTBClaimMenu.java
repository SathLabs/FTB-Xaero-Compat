package dev.satherov.ftbxaerocompat;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

import dev.ftb.mods.ftbchunks.client.map.MapChunk;
import dev.ftb.mods.ftbchunks.client.map.MapDimension;
import dev.ftb.mods.ftbchunks.net.RequestChunkChangePacket;
import dev.ftb.mods.ftblibrary.math.XZ;
import xaero.map.MapProcessor;
import xaero.map.gui.GuiMap;
import xaero.map.gui.MapTileSelection;
import xaero.map.gui.dropdown.rightclick.RightClickOption;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class FTBClaimMenu {
    
    public static void addRightClickOptions(GuiMap screen, ArrayList<RightClickOption> options, MapTileSelection mapTileSelection, MapProcessor mapProcessor) {
        if (mapTileSelection != null) {
            if (mapProcessor.getMapWorld().isUsingCustomDimension()) {
                options.add(new RightClickOption("gui.xaero_pac_claim_selection_out_of_dimension", options.size(), screen) {
                    public void onAction(Screen screen) {
                    }
                });
                return;
            }
            
            Optional<MapDimension> optionalMap = MapDimension.getCurrent();
            if (optionalMap.isEmpty()) return;
            MapDimension dim = optionalMap.get();
            
            Minecraft mc = Minecraft.getInstance();
            int fromX = mc.player.chunkPosition().x;
            int fromZ = mc.player.chunkPosition().z;
            
            int left = mapTileSelection.getLeft();
            int top = mapTileSelection.getTop();
            int right = mapTileSelection.getRight();
            int bottom = mapTileSelection.getBottom();
            
            int checkLeft = left;
            int checkTop = top;
            int checkRight = right;
            int checkBottom = bottom;
            
            final int maxClaimDistance = 7;
            if (left < fromX - maxClaimDistance) {
                checkLeft = fromX - maxClaimDistance;
            }
            
            if (top < fromZ - maxClaimDistance) {
                checkTop = fromZ - maxClaimDistance;
            }
            
            if (right > fromX + maxClaimDistance) {
                checkRight = fromX + maxClaimDistance;
            }
            
            if (bottom > fromZ + maxClaimDistance) {
                checkBottom = fromZ + maxClaimDistance;
            }
            
            int maxRequestLength = 32;
            if (checkRight - checkLeft >= maxRequestLength) {
                checkRight = checkLeft + maxRequestLength - 1;
            }
            
            if (checkBottom - checkTop >= maxRequestLength) {
                checkBottom = checkTop + maxRequestLength - 1;
            }
            
            if (left < checkLeft) {
                left = checkLeft - 1;
            }
            
            if (top < checkTop) {
                top = checkTop - 1;
            }
            
            if (right > checkRight) {
                right = checkRight + 1;
            }
            
            if (bottom > checkBottom) {
                bottom = checkBottom + 1;
            }
            
            Set<XZ> chunks = new HashSet<>();
            
            for (int cx = left; cx <= right; cx++) {
                for (int cz = top; cz <= bottom; cz++) {
                    chunks.add(XZ.of(cx, cz));
                }
            }
            
            Set<MapChunk> mapChunks = chunks.stream()
                    .map(xz -> dim.getRegion(XZ.regionFromChunk(xz.x(), xz.z())).getChunkForAbsoluteChunkPos(xz))
                    .collect(Collectors.toSet());
            
            if (mapChunks.stream().anyMatch(chunk -> chunk.getClaimedDate().isEmpty())) {
                options.add(new RightClickOption("gui.xaero_pac_claim_chunks", options.size(), screen) {
                    public void onAction(Screen screen) {
                        new RequestChunkChangePacket(RequestChunkChangePacket.ChunkChangeOp.create(true, false), chunks).sendToServer();
                    }
                });
            }
            
            if (mapChunks.stream().anyMatch(chunk -> chunk.getClaimedDate().isPresent())) {
                options.add(new RightClickOption("gui.xaero_pac_unclaim_chunks", options.size(), screen) {
                    public void onAction(Screen screen) {
                        new RequestChunkChangePacket(RequestChunkChangePacket.ChunkChangeOp.create(false, false), chunks).sendToServer();
                    }
                });
            }
            
            if (mapChunks.stream().anyMatch(chunk -> chunk.getClaimedDate().isPresent() && chunk.getForceLoadedDate().isEmpty())) {
                options.add(new RightClickOption("gui.xaero_pac_forceload_chunks", options.size(), screen) {
                    public void onAction(Screen screen) {
                        new RequestChunkChangePacket(RequestChunkChangePacket.ChunkChangeOp.create(true, true), chunks).sendToServer();
                    }
                });
            }
            
            if (mapChunks.stream().anyMatch(chunk -> chunk.getClaimedDate().isPresent() && chunk.getForceLoadedDate().isPresent())) {
                options.add(new RightClickOption("gui.xaero_pac_unforceload_chunks", options.size(), screen) {
                    public void onAction(Screen screen) {
                        new RequestChunkChangePacket(RequestChunkChangePacket.ChunkChangeOp.create(false, true), chunks).sendToServer();
                    }
                });
            }
            
            if (mapChunks.isEmpty()) {
                options.add(new RightClickOption("gui.xaero_pac_claim_selection_out_of_range", options.size(), screen) {
                    public void onAction(Screen screen) { }
                });
            }
        }
    }
}
